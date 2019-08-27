package com.chaitas.masterthesis.cluster.Routes;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.chaitas.masterthesis.cluster.Actors.WsClientActor;
import com.chaitas.masterthesis.cluster.Messages.OutgoingDestination;
import com.chaitas.masterthesis.cluster.util.JSONable;
import com.chaitas.masterthesis.commons.KryoSerializer;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WebSocketRoutes extends AllDirectives {

    private final ActorSystem system;
    private final ActorRef shardRegion;
    private Map<String, ActorRef> connections = new HashMap<>();
    private KryoSerializer kryo = new KryoSerializer();

    public WebSocketRoutes(ActorSystem system, ActorRef shardRegion) {
        this.system = system;
        this.shardRegion = shardRegion;
    }

    public Route createRoute() {
        return route(
                path("test", () ->
                        get(() -> {
                            System.out.println("WebSocket connection on route {test} has been initiated." );
                            return complete("Test completed");
                        })
                ),
                path("pubsub", () ->
                        get(() -> {
                            System.out.println("WebSocket connection on route {api/v1/pubsub} has been initiated." );
                            Flow<Message, Message, NotUsed> flow = createFlowRoute();
                            return handleWebSocketMessages(flow);
                        })
                )
        );
    }

    private <T> Flow<Message, Message, NotUsed> createFlowRoute(){
        final String connectionId = UUID.randomUUID().toString();
        final Flow<Message, Message, NotUsed> flow = createWebSocketFlow(connectionId).watchTermination((nu, cd) -> {
            cd.whenComplete((done, throwable) -> connections.remove(connectionId));
            return nu;
        });
        return flow;
    }


    private Flow<Message, Message, NotUsed> createWebSocketFlow(String connectionId) {

        ActorRef wsClientActor = system.actorOf(Props.create(WsClientActor.class, shardRegion));

        connections.put(connectionId, wsClientActor);
        System.out.println("Connection added: " + connections.size());

        // Outgoing  messages
        Source<Message, NotUsed> source = Source.<InternalServerMessage>actorRef(5, OverflowStrategy.fail())
                .map((outgoing) -> {
                    String json = JSONable.toJSON(outgoing);
                    return (Message) TextMessage.create(json);
                })
                .mapMaterializedValue(destinationRef -> {
                    wsClientActor.tell(new OutgoingDestination(destinationRef), ActorRef.noSender());
                    return NotUsed.getInstance();
                });

        // Incoming messages
        Sink<Message, NotUsed> sink = Flow.<Message>create()
                .map((msg) -> {

                    // Message is Text
                    if(msg.isText()){

                        System.out.print("Received a text message");

                        Optional<InternalServerMessage> message0 = JSONable.fromJSON(msg.asTextMessage().getStrictText(), InternalServerMessage.class);

                        if (message0.isPresent()) {
                            InternalServerMessage message = message0.get();
                            System.out.println("The message has been successfully deserialized : " + message.getControlPacketType());
                            return new InternalServerMessage(
                                    message.getClientIdentifier(),
                                    message.getControlPacketType(),
                                    message.getPayload()
                            );
                        } else {
                            System.out.println("Received an incompatible text message: +" + msg);
                            return null;
                        }
                    } else{
                        // Message is Binary
                        ByteString msg1 = msg.asBinaryMessage().getStrictData();
                        byte[] arr = msg1.toArray();
                        System.out.print("Received a binary message");

                        InternalServerMessage message = kryo.read(arr, InternalServerMessage.class);

                        if(message.getPayload() != null ) {
                            return new InternalServerMessage(
                                    message.getClientIdentifier(),
                                    message.getControlPacketType(),
                                    message.getPayload()
                            );
                        }else{
                            System.out.println("Received an incompatible binary message: +" + msg);
                            return null;
                        }

                    }

                })
                .to(Sink.actorRef(wsClientActor, PoisonPill.getInstance() ));

        return Flow.fromSinkAndSource(sink, source);
    }


}




