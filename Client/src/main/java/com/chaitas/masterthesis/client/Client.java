//package com.chaitas.masterthesis.client;
//
//import akka.Done;
//import akka.NotUsed;
//import akka.actor.ActorSystem;
//import akka.http.javadsl.Http;
//import akka.http.javadsl.model.StatusCodes;
//import akka.http.javadsl.model.ws.*;
//import akka.japi.Pair;
//import akka.stream.ActorMaterializer;
//import akka.stream.Materializer;
//import akka.stream.javadsl.Flow;
//import akka.stream.javadsl.Keep;
//import akka.stream.javadsl.Sink;
//import akka.stream.javadsl.Source;
//import akka.util.ByteString;
//import com.chaitas.masterthesis.commons.ControlPacketType;
//import com.chaitas.masterthesis.commons.KryoSerializer;
//import com.chaitas.masterthesis.commons.message.ExternalMessage;
//import com.chaitas.masterthesis.commons.message.Topic;
//import com.chaitas.masterthesis.commons.payloads.PUBLISHPayload;
//import com.chaitas.masterthesis.commons.spatial.Geofence;
//import com.chaitas.masterthesis.commons.spatial.Location;
//
//import java.util.concurrent.CompletionStage;
//
//
//public class Client {
//
//        public static KryoSerializer kryo = new KryoSerializer();
//
////    public static void testSerialize() {
////        Location paris = new Location(152.6, 14.40);
////        Geofence parisArea = Geofence.circle(paris, 3.0);
////        PUBLISHPayload publishPayload = new PUBLISHPayload(new Topic("cars"), parisArea , "test" );
////        ExternalMessage internalServerMessage1 = new ExternalMessage("1234", ControlPacketType.PUBLISH , publishPayload);
////
////        byte[] arr = kryo.write(internalServerMessage1);
////
////        ExternalMessage internalServerMessage2 = kryo.read(arr, ExternalMessage.class);
////
////        System.out.println("internalServerMessage2 : " +  internalServerMessage2);
////
////        System.out.println("type : " + internalServerMessage2.getControlPacketType());
////
////        assertEquals(internalServerMessage1, internalServerMessage2);
////    }
////
////    public static void testPayloadConnect() {
//////        System.out.println("RUNNING testPayloadConnect TEST");
//////        ExternalMessage message = new ExternalMessage("tes", ControlPacketType.CONNECT, new CONNECTPayload(Location.random()));
//////        System.out.println(message);
//////        ZMsg zmsg = message.getZMsg(kryo);
//////        ExternalMessage message2 = ExternalMessage.buildMessage(zmsg, kryo).get();
//////        System.out.println(message2.getPayload().getCONNECTPayload().getLocation().getLon());
//////        assertEquals("Messages should be equal", message, message2);
//////        System.out.println("FINISHED TEST");
//////    }
////
////
////    public static void testValid() {
////        Location paris = new Location(152.6, 14.40);
////
////        PUBLISHPayload publishPayload = new PUBLISHPayload(new Topic("topic"), Geofence.world(), "content");
////        BrokerForwardPublishPayload bfpp = new BrokerForwardPublishPayload(publishPayload, paris);
////
////        ExternalMessage valid = new ExternalMessage("test", ControlPacketType.BrokerForwardPublish, bfpp);
////
////        ZMsg validZMsg = valid.getZMsg(new KryoSerializer());
////        System.out.println("valid msg" + validZMsg);
////
////        ExternalMessage valid2 = ExternalMessage.buildMessage(validZMsg, new KryoSerializer()).get();
////
////        System.out.println("test valid2 " + valid2.getPayload().getBrokerForwardPublishPayload().getPublishPayload());
////
////        assertEquals(valid, valid2);
////    }
//
//
//
//    public static void testPayloadConnect() {
//        System.out.println("RUNNING testPayloadConnect TEST");
//
//
//      try{
//
//          Location paris = new Location(152.6, 14.40);
//          // Geofence parisArea = Geofence.circle(paris, 3.0);
//
//          PUBLISHPayload publishPayload = new PUBLISHPayload(new Topic("cars"),  new Geofence(paris, 4.0) , "test");
//          ExternalMessage internalServerMessage = new ExternalMessage("1234", ControlPacketType.PUBLISH , publishPayload);
//
//          System.out.println(publishPayload);
//
//
//          byte[] arr = kryo.write(internalServerMessage);
//
//          ExternalMessage internalServerMessage2 =  kryo.read(arr, ExternalMessage.class);
//          System.out.println(internalServerMessage2.getPayload().getPUBLISHPayload().getGeofence().radiusDegree);
//
//
//          // assertEquals( publishPayload, internalServerMessage2);
//          System.out.println("FINISHED TEST");
//
//
//      } catch(Exception e) {
//          e.printStackTrace();
//      }
//
//
//    }
//
//    public static void main(String[] args) throws Exception{
//
//        ActorSystem system = ActorSystem.create();
//        Materializer materializer = ActorMaterializer.create(system);
//        Http http = Http.get(system);
//        // KryoSerializer kryo = new KryoSerializer();
//        // print each incoming text message
//        // would throw exception on non strict or binary message
//        final Sink<Message, CompletionStage<Done>> printSink =
//                Sink.foreach((message) ->
//                        System.out.println("Got message: " + message.asTextMessage().getStrictText())
//                );
//
//
//        // Client.testSerialize();
//
////
////
////        Location berlin = new Location(52.52, 13.40);
////        Location paris = new Location(152.6, 14.40);
////
////        Geofence parisArea = Geofence.circle(paris, 3.0);
////        Geofence berlinArea = Geofence.circle(berlin, 3.0);
////
////        PUBLISHPayload publishPayload = new PUBLISHPayload(new Topic("cars"), parisArea , "uooo" );
////
////        ExternalMessage internalServerMessage = new ExternalMessage("1234", ControlPacketType.PUBLISH , publishPayload);
//
////        final Charset UTF_8 = Charset.forName("UTF-8");
////        String text = "Hello World!";
////        byte[] bytes = text.getBytes(UTF_8);
////        System.out.println("bytes= "+ Arrays.toString(bytes));
////        System.out.println("text again= "+new String(bytes, UTF_8));
//
//
//
//
//        // ByteString byteString = ByteString.fromArray(bytes11);
//
//
////
////        ByteArrayOutputStream bos = new ByteArrayOutputStream();
////        ObjectOutputStream oos = new ObjectOutputStream(bos);
////        oos.writeObject(internalServerMessage);
////        oos.flush();
////        byte [] data = bos.toByteArray();
//
////        JSONObject payload = new JSONObject();
////        payload.put("@payloadType", "PUBLISHPayload");
////        payload.put("topic", "test");
////        payload.put("geofence", "test");
////        payload.put("content", "teas");
////
////        JSONObject obj = new JSONObject();
////        obj.put("controlPacketType", "PUBLISH");
////        obj.put("clientIdentifier", "test");
////        obj.put("payload", payload );
////
////
////        System.out.print(obj);
////        System.out.print(obj.toString());
//
//
//         // Client.testPayloadConnect();
//
//
//
//        Location paris = new Location(52.6, 14.40);
//        Geofence parisArea = new Geofence(paris, 3.0);
//        PUBLISHPayload publishPayload = new PUBLISHPayload(new Topic("cars"), parisArea , "test" );
//        ExternalMessage internalServerMessage = new ExternalMessage("1234", ControlPacketType.PUBLISH , publishPayload);
//
//
//        byte[] arr = kryo.write(internalServerMessage);
////
//        System.out.print("arr bytes arr: " + arr);
//
//
////        ExternalMessage internalServerMessage2 = kryo.read(arr, ExternalMessage.class);
////        System.out.print("arr2 bytes arr2: " + internalServerMessage2);
////        // assertEquals(internalServerMessage, internalServerMessage2);
////        System.out.print("internalServerMessage2: " + internalServerMessage2.getPayload().getPUBLISHPayload());
//
//
////        ExternalMessage custom = ExternalMessage.buildMessage(arr, kryo).get();
////
////        System.out.print("custom " + custom);
////
////        byte[] arr22 = kryo.write(custom);
////
////        System.out.print("arr bytes custom : " + arr22);
//////
////
////
////        ExternalMessage internalServerMessage2 = kryo.read(arr, ExternalMessage.class);
////
////        System.out.println("internalServerMessage2 : " +  internalServerMessage2);
////
////        System.out.println("type : " + internalServerMessage2.getControlPacketType());
//
//
//
//
//
//        ByteString byteString = ByteString.fromArray(arr);
//
//        // send this as a message over the WebSocket
//        final Source<Message, NotUsed> helloSource =
//                Source.single((Message) BinaryMessage.create(byteString));
//
//        // the CompletionStage<Done> is the materialized value of Sink.foreach
//        // and it is completed when the stream completes
//        final Flow<Message, Message, CompletionStage<Done>> flow =
//                Flow.fromSinkAndSourceMat(printSink, helloSource, Keep.left());
//
//        final Pair<CompletionStage<WebSocketUpgradeResponse>, CompletionStage<Done>> pair =
//                http.singleWebSocketRequest(
//                        WebSocketRequest.create("ws://127.0.0.1:8080/pubsub"),
//                        flow,
//                        materializer
//                );
//        // The first value in the pair is a CompletionStage<WebSocketUpgradeResponse> that
//        // completes when the WebSocket request has connected successfully (or failed)
//        final CompletionStage<Done> connected = pair.first().thenApply(upgrade -> {
//            // just like a regular http request we can access response status which is available via upgrade.response.status
//            // status code 101 (Switching Protocols) indicates that server support WebSockets
//            if (upgrade.response().status().equals(StatusCodes.SWITCHING_PROTOCOLS)) {
//                return Done.getInstance();
//            } else {
//                throw new RuntimeException("Connection failed: " + upgrade.response().status());
//            }
//        });
//
//        // the second value is the completion of the sink from above
//        // in other words, it completes when the WebSocket disconnects
//        final CompletionStage<Done> closed = pair.second();
//
//        // in a real application you would not side effect here
//        // and handle errors more carefully
//        // connected.thenAccept(done -> System.out.println("Connected"));
//        // closed.thenAccept(done -> System.out.println("Connection closed"));
//    }
//}
