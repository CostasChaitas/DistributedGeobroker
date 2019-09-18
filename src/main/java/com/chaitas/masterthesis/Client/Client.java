package com.chaitas.masterthesis.Client;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ws.*;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.chaitas.masterthesis.Messages.ExternalMessages.ControlPacketType;
import com.chaitas.masterthesis.Messages.ExternalMessages.ExternalMessage;
import com.chaitas.masterthesis.Messages.ExternalMessages.Topic;
import com.chaitas.masterthesis.Messages.ExternalMessages.Payloads.CONNECTPayload;
import com.chaitas.masterthesis.Messages.ExternalMessages.Payloads.PUBLISHPayload;
import com.chaitas.masterthesis.Messages.ExternalMessages.Spatial.Geofence;
import com.chaitas.masterthesis.Messages.ExternalMessages.Spatial.Location;
import com.chaitas.masterthesis.Utils.KryoSerializer;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class Client {

    public static KryoSerializer kryo = new KryoSerializer();


    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create();
        Materializer materializer = ActorMaterializer.create(system);
        Http http = Http.get(system);


        Location paris = new Location(44.0, 14.40);

        CONNECTPayload connectPayload = new CONNECTPayload(paris);
        ExternalMessage externalMessageConnect = new ExternalMessage("12345", ControlPacketType.CONNECT , connectPayload);

        Geofence parisArea = Geofence.circle(paris, 3.0);
        PUBLISHPayload publishPayload = new PUBLISHPayload(new Topic("cars"), parisArea , "test" );
        ExternalMessage externalMessagePublish = new ExternalMessage("12345", ControlPacketType.PUBLISH , publishPayload);


        byte[] arr1 = kryo.write(externalMessageConnect);
        byte[] arr2 = kryo.write(externalMessagePublish);
        ByteString byteString1 = ByteString.fromArray(arr1);
        ByteString byteString2 = ByteString.fromArray(arr2);

        // emit "one" and then "two" and then keep the source from completing
        final Source<Message, CompletableFuture<Optional<Message>>> source =
                Source.from(Arrays.asList(BinaryMessage.create(byteString1), (Message) BinaryMessage.create(byteString2)))
                        .concatMat(Source.maybe(), Keep.right());

        final Flow<Message, Message, CompletableFuture<Optional<Message>>> flow =
                Flow.fromSinkAndSourceMat(
                        Sink.foreach((message) ->
                                System.out.println("Got message: " + message.asTextMessage().getStrictText())
                        ),
                        source,
                        Keep.right());


        final Pair<CompletionStage<WebSocketUpgradeResponse>, CompletableFuture<Optional<Message>>> pair =
                http.singleWebSocketRequest(
                        WebSocketRequest.create("ws://127.0.0.1:8000/api"),
                        flow,
                        materializer);

    }
}
