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
import com.chaitas.masterthesis.cluster.Utils.JSONable;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.KryoSerializer;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.ExternalMessage;
import com.chaitas.masterthesis.commons.payloads.INCOMPATIBLEPayload;

import java.util.Optional;

    public class WebSocketRoutes extends AllDirectives {

    private final ActorSystem system;
    private final ActorRef clientShardRegion;
    private KryoSerializer kryo = new KryoSerializer();

    public WebSocketRoutes(ActorSystem system, ActorRef clientShardRegion) {
        this.system = system;
        this.clientShardRegion = clientShardRegion;
    }

    public Route createRoute() {
        return route(
                path("test", () ->
                        get(() -> {
                            System.out.println("WsServerActor connection on route {/test} has been initiated." );
                            return complete("Test successfully completed.");
                        })
                ),
                path("api", () ->
                        get(() -> {
                            System.out.println("WsServerActor connection on route {/api} has been initiated." );
                            Flow<Message, Message, NotUsed> flow = createFlowRoute();
                            return handleWebSocketMessages(flow);
                        })
                )
        );
    }

    private <T> Flow<Message, Message, NotUsed> createFlowRoute(){
        final Flow<Message, Message, NotUsed> flow = createWebSocketFlow().watchTermination((nu, cd) -> nu);
        return flow;
    }

    private Flow<Message, Message, NotUsed> createWebSocketFlow() {
        // Create WsClientActor
        ActorRef wsClientActor = system.actorOf(Props.create(WsClientActor.class, clientShardRegion));
        System.out.println("TCP Connection opened");

        // Outgoing messages
        Source<Message, NotUsed> source = Source.<ExternalMessage>actorRef(5, OverflowStrategy.fail())
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
                        Optional<ExternalMessage> message0 = JSONable.fromJSON(msg.asTextMessage().getStrictText(), ExternalMessage.class);
                        if (message0.isPresent()) {
                            ExternalMessage message = message0.get();
                            return new ExternalMessage(
                                    message.getClientIdentifier(),
                                    message.getControlPacketType(),
                                    message.getPayload()
                            );
                        } else {
                            System.out.println("Received an incompatible Text Message: +" + msg);
                            return new ExternalMessage(
                                    "404",
                                    ControlPacketType.INCOMPATIBLEPayload,
                                    new INCOMPATIBLEPayload(ReasonCode.IncompatiblePayload)
                            );
                        }
                    } else{
                        // Message is Binary
                        ByteString msg1 = msg.asBinaryMessage().getStrictData();
                        byte[] arr = msg1.toArray();
                        System.out.print("Received a binary message");
                        ExternalMessage message = kryo.read(arr, ExternalMessage.class);
                        if(message.getPayload() != null ) {
                            return new ExternalMessage(
                                    message.getClientIdentifier(),
                                    message.getControlPacketType(),
                                    message.getPayload()
                            );
                        }else{
                            System.out.println("Received an incompatible Binary Message: +" + msg);
                            return new ExternalMessage(
                                    "404",
                                    ControlPacketType.INCOMPATIBLEPayload,
                                    new INCOMPATIBLEPayload(ReasonCode.IncompatiblePayload)
                            );
                        }
                    }
                })
                .to(Sink.actorRef(wsClientActor, PoisonPill.getInstance() ));

        return Flow.fromSinkAndSource(sink, source);
    }

}




