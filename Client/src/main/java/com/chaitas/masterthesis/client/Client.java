package com.chaitas.masterthesis.client;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.ws.*;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.KryoSerializer;
import com.chaitas.masterthesis.commons.message.ExternalMessage;
import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.payloads.CONNECTPayload;
import com.chaitas.masterthesis.commons.payloads.PUBLISHPayload;
import com.chaitas.masterthesis.commons.spatial.Geofence;
import com.chaitas.masterthesis.commons.spatial.Location;

import java.util.concurrent.CompletionStage;


public class Client {

    public static KryoSerializer kryo = new KryoSerializer();

//    public static void testSerialize() {
//        Location paris = new Location(152.6, 14.40);
//        Geofence parisArea = Geofence.circle(paris, 3.0);
//        PUBLISHPayload publishPayload = new PUBLISHPayload(new Topic("cars"), parisArea , "test" );
//        ExternalMessage internalServerMessage1 = new ExternalMessage("1234", ControlPacketType.PUBLISH , publishPayload);
//
//        byte[] arr = kryo.write(internalServerMessage1);
//
//        ExternalMessage internalServerMessage2 = kryo.read(arr, ExternalMessage.class);
//
//        System.out.println("internalServerMessage2 : " +  internalServerMessage2);
//
//        System.out.println("type : " + internalServerMessage2.getControlPacketType());
//
//        assertEquals(internalServerMessage1, internalServerMessage2);
//    }
//

    public static void main(String[] args) throws Exception{

        ActorSystem system = ActorSystem.create();
        Materializer materializer = ActorMaterializer.create(system);
        Http http = Http.get(system);
        // would throw exception on non strict or binary message
        final Sink<Message, CompletionStage<Done>> printSink =
                Sink.foreach((message) ->
                        System.out.println("Got message: " + message.asTextMessage().getStrictText())
                );


        Location paris = new Location(44.0, 14.40);
        Geofence parisArea = Geofence.circle(paris, 3.0);
        PUBLISHPayload publishPayload = new PUBLISHPayload(new Topic("cars"), parisArea , "test" );
        ExternalMessage externalMessage = new ExternalMessage("1234", ControlPacketType.PUBLISH , publishPayload);


        CONNECTPayload connectPayload = new CONNECTPayload(paris);
        ExternalMessage externalMessage1 = new ExternalMessage("12345", ControlPacketType.CONNECT , connectPayload);


        byte[] arr = kryo.write(externalMessage1);

        ByteString byteString = ByteString.fromArray(arr);

        // send this as a message over the WebSocket
        final Source<Message, NotUsed> helloSource =
                Source.single((Message) BinaryMessage.create(byteString));

        // the CompletionStage<Done> is the materialized value of Sink.foreach
        // and it is completed when the stream completes
        final Flow<Message, Message, CompletionStage<Done>> flow =
                Flow.fromSinkAndSourceMat(printSink, helloSource, Keep.left());

        final Pair<CompletionStage<WebSocketUpgradeResponse>, CompletionStage<Done>> pair =
                http.singleWebSocketRequest(
                        WebSocketRequest.create("ws://127.0.0.1:8000/api"),
                        flow,
                        materializer
                );
        // The first value in the pair is a CompletionStage<WebSocketUpgradeResponse> that
        // completes when the WebSocket request has connected successfully (or failed)
        final CompletionStage<Done> connected = pair.first().thenApply(upgrade -> {
            // just like a regular http request we can access response status which is available via upgrade.response.status
            // status code 101 (Switching Protocols) indicates that server support WebSockets
            if (upgrade.response().status().equals(StatusCodes.SWITCHING_PROTOCOLS)) {
                return Done.getInstance();
            } else {
                throw new RuntimeException("Connection failed: " + upgrade.response().status());
            }
        });

        // the second value is the completion of the sink from above
        // in other words, it completes when the WebSocket disconnects
        final CompletionStage<Done> closed = pair.second();

        // in a real application you would not side effect here
        // and handle errors more carefully
        // connected.thenAccept(done -> System.out.println("Connected"));
        // closed.thenAccept(done -> System.out.println("Connection closed"));
    }
}
