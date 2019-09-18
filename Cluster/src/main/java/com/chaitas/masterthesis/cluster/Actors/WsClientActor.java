package com.chaitas.masterthesis.cluster.Actors;

import akka.actor.*;
<<<<<<< HEAD
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import com.chaitas.masterthesis.cluster.Messages.*;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.payloads.*;
import com.chaitas.masterthesis.commons.spatial.Location;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class WsClientActor extends AbstractActor {

    private final Address actorSystemAddress = getContext().system().provider().getDefaultAddress();
    private LoggingAdapter log = Logging.getLogger(getContext().system(), getSelf().path().toStringWithAddress(actorSystemAddress));

    private String wsClientActorId;
    private ActorRef outgoing;
    private ActorRef shardRegion;

    private static Map<String, Location> clients = new ConcurrentHashMap<>();
    private static Map<String, Map<Topic, ProcessSUBSCRIBE>> subs = new ConcurrentHashMap<>();

    public WsClientActor(ActorRef shardRegion) {
        this.shardRegion = shardRegion;
=======
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chaitas.masterthesis.cluster.Messages.*;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.ExternalMessage;
import com.chaitas.masterthesis.commons.payloads.*;

public class WsClientActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef clientShardRegion;
    private String wsClientActorId;
    private ActorRef outgoing;

    public WsClientActor(ActorRef clientShardRegion) {
        this.clientShardRegion = clientShardRegion;
>>>>>>> master
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        wsClientActorId = getSelf().path().name();
        log.info("Creating WsClientActor Actor : {}", wsClientActorId);
    }

    @Override
    public void postStop() throws Exception {
        log.info("Shutting down WsClientActor Actor : {}", wsClientActorId);
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
<<<<<<< HEAD

                // Incoming from WS
                .match(InternalServerMessage.class, message-> receiveInternalServerMessage(message))
                .match(OutgoingDestination.class, message -> receiveOutgoingDestination(message))

                // Incoming from Sharding Entity - TileManager
                .match(SendSUBACK.class, message-> receiveSendSuback(message))
                .match(SendPUBACK.class, message-> receiveSendPuback(message))
                .match(GeoMatching.class, message-> receiveGeoMatching(message))

                .build();
    }


    private void receiveInternalServerMessage(InternalServerMessage message) {
        log.info("WsClientActor Actor received InternalServerMessage ");

=======
            // Incoming from WS
            .match(ExternalMessage.class, message-> receiveExternalMessage(message))
            .match(OutgoingDestination.class, message -> receiveOutgoingDestination(message))
            // Incoming from Sharding Entity - ClientShardEntity
            .match(SendACK.class, message-> receiveSendACK(message))
            .build();
    }

    private void receiveExternalMessage(ExternalMessage message) {
        log.info("WsClientActor Actor received ExternalMessage ");
>>>>>>> master
        switch (message.getControlPacketType()) {
                case CONNECT:
                    CONNECTPayload connectPayload = message.getPayload().getCONNECTPayload();
                    if (connectPayload != null) {
<<<<<<< HEAD
                        log.info("Message CONNECTPayload received :" + connectPayload.location);

                        // Connect WsClient and update the location
                        clients.put(message.getClientIdentifier(), connectPayload.location);

                        log.info("Client with ID " + message.getClientIdentifier() + " is connected with location :" + connectPayload.location);

                        CONNACKPayload connackPayload = new CONNACKPayload(ReasonCode.Success);

                        InternalServerMessage internalServerMessage = new InternalServerMessage(
                                message.getClientIdentifier(),
                                ControlPacketType.CONNACK,
                                connackPayload
                        );

                        outgoing.tell(internalServerMessage, getSender());
=======
                        log.info("WsClientActor {} received message CONNECTPayload", wsClientActorId);
                        ProcessCONNECT processCONNECT = new ProcessCONNECT(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processCONNECT, getSelf());
>>>>>>> master
                    }
                    break;
                case DISCONNECT:
                    DISCONNECTPayload disconnectPayload = message.getPayload().getDISCONNECTPayload();
                    if (disconnectPayload != null) {
<<<<<<< HEAD
                        System.out.println("Message DISCONNECTPayload received :" + disconnectPayload.reasonCode);
=======
                        log.info("WsClientActor {} received message DISCONNECTPayload", wsClientActorId);
                        ProcessDISCONNECT processDISCONNECT = new ProcessDISCONNECT(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processDISCONNECT, getSelf());
>>>>>>> master
                        // Killing current WsClientActor
                        getSelf().tell(PoisonPill.getInstance(), getSelf());
                    }
                    break;
                case PINGREQ:
                    PINGREQPayload pingreqPayload = message.getPayload().getPINGREQPayload();
                    if (pingreqPayload != null) {
<<<<<<< HEAD
                        log.info("Message PINGREQ received :" + pingreqPayload.location);

                        // Check if Client is connected
                        if(!clients.containsKey(message.getClientIdentifier())){
                            PINGRESPPayload pingrespPayload = new PINGRESPPayload(ReasonCode.NotConnected);
                            InternalServerMessage internalServerMessage = new InternalServerMessage(
                                    message.getClientIdentifier(),
                                    ControlPacketType.NOTCONNECTED,
                                    pingrespPayload
                            );
                            outgoing.tell(internalServerMessage, getSender());
                            return;
                        }

                        // Update location
                        clients.put(message.getClientIdentifier(), pingreqPayload.location);
                        log.info("Client with ID " + message.getClientIdentifier() + " updated the location :" + pingreqPayload.location);

                        PINGRESPPayload pingrespPayload = new PINGRESPPayload(ReasonCode.Success);

                        InternalServerMessage internalServerMessage = new InternalServerMessage(
                                message.getClientIdentifier(),
                                ControlPacketType.PINGRESP,
                                pingrespPayload
                        );

                        outgoing.tell(internalServerMessage, getSender());
                        // tileDeciderActor.tell(new ProcessSUBSCRIBE(message, outgoing),  getSelf());

=======
                        log.info("WsClientActor {} received message PINGREQ", wsClientActorId);
                        ProcessPINGREQ processPINGREQ = new ProcessPINGREQ(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processPINGREQ, getSelf());
>>>>>>> master
                    }
                    break;
                case SUBSCRIBE:
                    SUBSCRIBEPayload subscribePayload = message.getPayload().getSUBSCRIBEPayload();
                    if (subscribePayload != null) {
<<<<<<< HEAD
                        log.info("Message SUBSCRIBE received :" + subscribePayload.getTopic());

                        // Check if Client is connected
                        if(!clients.containsKey(message.getClientIdentifier())){
                            SUBACKPayload subackPayload = new SUBACKPayload(ReasonCode.NotConnected);
                            InternalServerMessage internalServerMessage = new InternalServerMessage(
                                    message.getClientIdentifier(),
                                    ControlPacketType.NOTCONNECTED,
                                    subackPayload
                            );
                            outgoing.tell(internalServerMessage, getSender());
                            return;
                        }

                        // Find responsible tile(s)
                        int randomNum = findTiles("test");
                        ProcessSUBSCRIBE processSUBSCRIBE = new ProcessSUBSCRIBE(message,  getSelf(), randomNum);

                        // Register subscription for the client
                        Map<Topic, ProcessSUBSCRIBE> subsForClient = subs.get(message.getClientIdentifier());
                        if(subsForClient == null ){
                            subsForClient = new ConcurrentHashMap<>();
                        }
                        subsForClient.put(message.getPayload().getSUBSCRIBEPayload().topic, processSUBSCRIBE);
                        subs.put(message.getClientIdentifier(), subsForClient);

                        log.info("There are  : " + subsForClient.size() + " subs for client "  + message.getClientIdentifier());

                        // Ask TileManager Entity
                        Patterns.ask(shardRegion, processSUBSCRIBE, 3000);

=======
                        log.info("WsClientActor {} received message SUBSCRIBE", wsClientActorId);
                        ProcessSUBSCRIBE processSUBSCRIBE = new ProcessSUBSCRIBE(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processSUBSCRIBE, getSelf());
                    }
                    break;
                case UNSUBSCRIBE:
                    UNSUBSCRIBEPayload unsubscribePayload = message.getPayload().getUNSUBSCRIBEPayload();
                    if (unsubscribePayload != null) {
                        log.info("WsClientActor {} received message UNSUBSCRIBE", wsClientActorId);
                        ProcessUNSUBSCRIBE processUNSUBSCRIBE = new ProcessUNSUBSCRIBE(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processUNSUBSCRIBE, getSelf());
>>>>>>> master
                    }
                    break;
                case PUBLISH:
                    PUBLISHPayload publishPayload = message.getPayload().getPUBLISHPayload();
                    if (publishPayload != null) {
<<<<<<< HEAD
                        log.info("WsClientActor Actor: Message PUBLISH received :" + publishPayload.getTopic());

                        // Check if Client is connected
                        if(!clients.containsKey(message.getClientIdentifier())){
                            PUBACKPayload pubackPayload = new PUBACKPayload(ReasonCode.NotConnected);
                            InternalServerMessage internalServerMessage = new InternalServerMessage(
                                    message.getClientIdentifier(),
                                    ControlPacketType.NOTCONNECTED,
                                    pubackPayload
                            );
                            outgoing.tell(internalServerMessage, getSender());
                            return;
                        }

                        int randomNum = findTiles("test");
                        ProcessPUBLISH processPUBLISH = new ProcessPUBLISH(message, getSelf(), clients.get(message.getClientIdentifier()), randomNum);
                        Patterns.ask(shardRegion, processPUBLISH, 3000);
                    }
                    break;
                default:
=======
                        log.info("WsClientActor {} received message PUBLISH", wsClientActorId);
                        ProcessPUBLISH processPUBLISH = new ProcessPUBLISH(message, getSelf(), null);
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processPUBLISH, getSelf());
                    }
                    break;
                case MATCH:
                    PUBLISHPayload publishPayload1 = message.getPayload().getPUBLISHPayload();
                    if (publishPayload1 != null) {
                        log.info("WsClientActor {} received message MATCH", wsClientActorId);
                        outgoing.tell(message, getSelf());
                    }
                    break;
                default:
                    log.info("WsClientActor {} received message INCOMPATIBLE", wsClientActorId);
                    ExternalMessage externalMessage = new ExternalMessage(
                            "404",
                            ControlPacketType.INCOMPATIBLEPayload,
                            new INCOMPATIBLEPayload(ReasonCode.IncompatiblePayload)
                    );
                    outgoing.tell(externalMessage, getSender());
>>>>>>> master
                    log.info("Cannot process message +" + message.toString());
        }
    }

<<<<<<< HEAD


    private void receiveGeoMatching(GeoMatching geoMatching){
        log.info("Doing the GeoMatching for " + geoMatching.processSUBSCRIBE.message.getClientIdentifier());

        // Do the subscriber geo matching
        Boolean isSubscriberGeoMatching = subscriberGeoMatching(geoMatching.processSUBSCRIBE, geoMatching.processPUBLISH);

        if(isSubscriberGeoMatching){
            // Do the publisher geo matching
            Boolean isPublisherGeoMatching = publisherGeoMatching(geoMatching.processSUBSCRIBE, geoMatching.processPUBLISH);
            if(isPublisherGeoMatching){

                InternalServerMessage internalServerMessage = new InternalServerMessage(
                        geoMatching.processSUBSCRIBE.message.getClientIdentifier(),
                        ControlPacketType.PUBLISH,
                        geoMatching.processPUBLISH.message.getPayload()
                );
                log.info("Sending PUBLISH message to " + geoMatching.processSUBSCRIBE.message.getClientIdentifier());

                outgoing.tell(internalServerMessage, getSelf());
            }
        }
    }

    private Boolean subscriberGeoMatching(ProcessSUBSCRIBE processSubscribe, ProcessPUBLISH processPublish){
        // Match subscription geofence & publisher location.
        if(processSubscribe.message.getPayload().getSUBSCRIBEPayload().geofence.contains(processPublish.wsClientLocation)){
            return true;
        }
        return false;
    }

    private Boolean publisherGeoMatching(ProcessSUBSCRIBE processSubscribe, ProcessPUBLISH processPublish){
        // Match message geofence & subscriber location.
        Location wsClientLocation = clients.get(processSubscribe.message.getClientIdentifier());
        if(processPublish.message.getPayload().getPUBLISHPayload().geofence.contains(wsClientLocation)){
            return true;
        }
        return false;
    }

    private void receiveSendSuback(SendSUBACK sendSuback) {
        log.info("WsClientActor Actor: Message SendSUBACK received :" + sendSuback.message.getPayload());
        outgoing.tell(sendSuback.message, getSender());
    }

    private void receiveSendPuback(SendPUBACK sendPuback) {
        log.info("WsClientActor Actor: Message SendPUBACK received :" + sendPuback.message.getPayload());
        outgoing.tell(sendPuback.message, getSender());
    }

    private int findTiles(String geofence){
        // DO JOB HERE

        //NEED TO KNOW all the available tiles
        // num of tiles e.g 100

        int randomNum = ThreadLocalRandom.current().nextInt(0, 10 + 1);
        log.info("TileId's found :  " + randomNum);

        return 2;
    }

    private void receiveOutgoingDestination(OutgoingDestination message) {
        log.info("WsClientActor Actors received OutgoingDestination msg : {}", message.destination);
        outgoing = message.destination;
    }

    private void passivate() {
        // Tell our shard region that we want to shut down to free up resources
        getContext().getParent().tell(
                new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
    }
=======
    private void receiveSendACK(SendACK sendACK) {
        log.info("WsClientActor {} received message SendACK", wsClientActorId);
        outgoing.tell(sendACK.message, getSender());
    }

    private void receiveOutgoingDestination(OutgoingDestination message) {
        log.info("WsClientActor {} received message OutgoingDestination", wsClientActorId);
        outgoing = message.destination;
    }

>>>>>>> master
}



