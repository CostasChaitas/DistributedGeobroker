package com.chaitas.masterthesis.cluster.Sharding;

import akka.actor.*;
import akka.cluster.sharding.ClusterSharding;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.chaitas.masterthesis.cluster.Messages.*;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.ExternalMessage;
import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.payloads.*;
import com.chaitas.masterthesis.commons.spatial.Location;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientShardEntity extends AbstractActor {

    private final Address actorSystemAddress = getContext().system().provider().getDefaultAddress();
    private LoggingAdapter log = Logging.getLogger(getContext().system(), getSelf().path().toStringWithAddress(actorSystemAddress));

    private final ActorSystem actorSystem = getContext().getSystem();
    private final ActorRef topicShardRegion = ClusterSharding.get(actorSystem).shardRegion("Topics");
    private String clientShardEntityId;
    private Location clientLocation;
    private ActorRef wsClientActor;
    private Map<Topic, ProcessSUBSCRIBE> subscriptions = new ConcurrentHashMap<>();

    @Override
    public void preStart() throws Exception {
        super.preStart();
        clientShardEntityId = getSelf().path().name();
        log.info("Started ClientShardEntity actor {}", clientShardEntityId);
        getContext().setReceiveTimeout(Duration.ofSeconds(120));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            // Incoming from WSClientActor
            .match(ProcessCONNECT.class, message-> receiveProcessCONNECT(message))
            .match(ProcessDISCONNECT.class, message-> receiveProcessDISCONNECT(message))
            .match(ProcessPINGREQ.class, message-> receiveProcessPINGREQ(message))
            .match(ProcessUNSUBSCRIBE.class, message-> receiveProcessUNSUBSCRIBE(message))
            .match(ProcessSUBSCRIBE.class, message-> receiveProcessSUBSCRIBE(message))
            .match(ProcessPUBLISH.class, message-> receiveProcessPUBLISH(message))
            // Incoming from Sharding Entity - TileShardEntity
            .match(PublisherGeoMatching.class, message-> receivePublisherGeoMatching(message))
            .build();
    }

    private void receiveProcessCONNECT(ProcessCONNECT processCONNECT) {
        log.info("Message ProcessCONNECT received ");

        // Check if it's needed to update clientActor
        if(clientLocation != null){
            for (Map.Entry<Topic, ProcessSUBSCRIBE> sub : subscriptions.entrySet()) {
                ProcessSUBSCRIBE newSub = sub.getValue();
                newSub.wsClientActor = sender();
                log.info( "Telling TopicShardRegion to update user connections");
                topicShardRegion.tell(newSub, getSelf());
            }
        }

        // Connect ClientShardEntity and update the location
        clientLocation = processCONNECT.message.getPayload().getCONNECTPayload().location;
        log.info( "wsClientActor" + wsClientActor);
        wsClientActor = processCONNECT.wsClientActor;
        CONNACKPayload connackPayload = new CONNACKPayload(ReasonCode.Success);
        ExternalMessage CONNACK = new ExternalMessage(
                processCONNECT.message.getClientIdentifier(),
                ControlPacketType.CONNACK,
                connackPayload
        );
        SendACK sendACK = new SendACK(CONNACK);
        sender().tell(sendACK, getSelf());
    }

    private void receiveProcessDISCONNECT(ProcessDISCONNECT processDISCONNECT) {
        System.out.println("Message ProcessDISCONNECT received");
        clientLocation = null;
        wsClientActor = null;
    }

    private void receiveProcessPINGREQ(ProcessPINGREQ processPINGREQ) {
        log.info("Message ProcessPINGREQ received ");
        // Check if client is connected
        if(this.clientLocation == null){
            PINGRESPPayload pingrespPayload = new PINGRESPPayload(ReasonCode.NotConnected);
            ExternalMessage NOTCONNECTED = new ExternalMessage(
                    processPINGREQ.message.getClientIdentifier(),
                    ControlPacketType.NOTCONNECTED,
                    pingrespPayload
            );
            SendACK sendACK = new SendACK(NOTCONNECTED);
            sender().tell(sendACK, getSender());
            return;
        }
        // Check if connection already exists
        if(this.wsClientActor.compareTo(processPINGREQ.wsClientActor) != 0){
            PINGRESPPayload pingrespPayload = new PINGRESPPayload(ReasonCode.ConnectionAlreadyExist);
            ExternalMessage CONNECTIONEXIST = new ExternalMessage(
                    processPINGREQ.message.getClientIdentifier(),
                    ControlPacketType.CONNECTIONEXIST,
                    pingrespPayload
            );
            SendACK sendACK = new SendACK(CONNECTIONEXIST);
            sender().tell(sendACK, getSender());
            return;
        }

        // Update location
        clientLocation = processPINGREQ.message.getPayload().getPINGREQPayload().location;

        PINGRESPPayload pingrespPayload = new PINGRESPPayload(ReasonCode.Success);
        ExternalMessage PINGRESP = new ExternalMessage(
                processPINGREQ.message.getClientIdentifier(),
                ControlPacketType.PINGRESP,
                pingrespPayload
        );
        SendACK sendACK = new SendACK(PINGRESP);
        sender().tell(sendACK, getSelf());
    }

    private void receiveProcessUNSUBSCRIBE(ProcessUNSUBSCRIBE processUNSUBSCRIBE) {
        log.info("Message ProcessUNSUBSCRIBE received ");
        // Check if client is connected
        if(this.clientLocation == null){
            UNSUBACKPayload unsubackPayload = new UNSUBACKPayload(ReasonCode.NotConnected);
            ExternalMessage NOTCONNECTED = new ExternalMessage(
                    processUNSUBSCRIBE.message.getClientIdentifier(),
                    ControlPacketType.NOTCONNECTED,
                    unsubackPayload
            );
            SendACK sendACK = new SendACK(NOTCONNECTED);
            sender().tell(sendACK, getSender());
            return;
        }

        // Check if connection already exists
        if(this.wsClientActor.compareTo(processUNSUBSCRIBE.wsClientActor) != 0){
            UNSUBACKPayload unsubackPayload = new UNSUBACKPayload(ReasonCode.ConnectionAlreadyExist);
            ExternalMessage CONNECTIONEXIST = new ExternalMessage(
                    processUNSUBSCRIBE.message.getClientIdentifier(),
                    ControlPacketType.CONNECTIONEXIST,
                    unsubackPayload
            );
            SendACK sendACK = new SendACK(CONNECTIONEXIST);
            sender().tell(sendACK, getSender());
            return;
        }

        // Unsubscribe
        subscriptions.remove(processUNSUBSCRIBE.message.getPayload().getUNSUBSCRIBEPayload().topic);

        // Update location
        UNSUBACKPayload unsubackPayload = new UNSUBACKPayload(ReasonCode.Success);
        ExternalMessage UNSUBACK = new ExternalMessage(
                processUNSUBSCRIBE.message.getClientIdentifier(),
                ControlPacketType.UNSUBACK,
                unsubackPayload
        );
        SendACK sendACK = new SendACK(UNSUBACK, clientLocation);
        sender().tell(sendACK, getSender());

        // Ask TileShardEntity Entity
        topicShardRegion.tell(processUNSUBSCRIBE, getSelf());
    }

    private void receiveProcessSUBSCRIBE(ProcessSUBSCRIBE processSUBSCRIBE) {
        log.info("Message ProcessSUBSCRIBE received ");

        // Check if client is connected
        if(this.clientLocation == null){
            SUBACKPayload subackPayload = new SUBACKPayload(ReasonCode.NotConnected);
            ExternalMessage NOTCONNECTED = new ExternalMessage(
                    processSUBSCRIBE.message.getClientIdentifier(),
                    ControlPacketType.NOTCONNECTED,
                    subackPayload
            );
            SendACK sendACK = new SendACK(NOTCONNECTED);
            sender().tell(sendACK, getSender());
            return;
        }

        // Check if connection already exists
        if(this.wsClientActor.compareTo(processSUBSCRIBE.wsClientActor) != 0){
            SUBACKPayload subackPayload = new SUBACKPayload(ReasonCode.ConnectionAlreadyExist);
            ExternalMessage CONNECTIONEXIST = new ExternalMessage(
                    processSUBSCRIBE.message.getClientIdentifier(),
                    ControlPacketType.CONNECTIONEXIST,
                    subackPayload
            );
            SendACK sendACK = new SendACK(CONNECTIONEXIST);
            sender().tell(sendACK, getSender());
            return;
        }

        // Register subscription for the client
        Topic topic = processSUBSCRIBE.message.getPayload().getSUBSCRIBEPayload().topic;
        subscriptions.put(topic, processSUBSCRIBE);

        log.info("There are  : " + subscriptions.size() + " subs for client "  + processSUBSCRIBE.message.getClientIdentifier());

        SUBACKPayload subackPayload = new SUBACKPayload(ReasonCode.Success);
        ExternalMessage SUBACK = new ExternalMessage(
                processSUBSCRIBE.message.getClientIdentifier(),
                ControlPacketType.SUBACK,
                subackPayload
        );
        SendACK sendACK = new SendACK(SUBACK, clientLocation);
        sender().tell(sendACK, getSelf());

        // Ask TileShardEntity Entity
        topicShardRegion.tell(processSUBSCRIBE, getSelf());
    }

    private void receiveProcessPUBLISH(ProcessPUBLISH processPUBLISH) {
        log.info("Message ProcessPUBLISH received ");
        // Check if client is connected
        if(this.clientLocation == null){
            PUBACKPayload pubackPayload = new PUBACKPayload(ReasonCode.NotConnected);
            ExternalMessage NOTCONNECTED = new ExternalMessage(
                    processPUBLISH.message.getClientIdentifier(),
                    ControlPacketType.NOTCONNECTED,
                    pubackPayload
            );
            SendACK sendACK = new SendACK(NOTCONNECTED);
            sender().tell(sendACK, getSender());
            return;
        }

        // Check if connection already exist
        if(this.wsClientActor.compareTo(processPUBLISH.wsClientActor) != 0){
            PUBACKPayload pubackPayload = new PUBACKPayload(ReasonCode.ConnectionAlreadyExist);
            ExternalMessage CONNECTIONEXIST = new ExternalMessage(
                    processPUBLISH.message.getClientIdentifier(),
                    ControlPacketType.CONNECTIONEXIST,
                    pubackPayload
            );
            SendACK sendACK = new SendACK(CONNECTIONEXIST);
            sender().tell(sendACK, getSender());
            return;
        }
        PUBACKPayload pubackPayload = new PUBACKPayload(ReasonCode.Success);
        ExternalMessage PUBACK = new ExternalMessage(
                processPUBLISH.message.getClientIdentifier(),
                ControlPacketType.PUBACK,
                pubackPayload
        );
        SendACK sendACK = new SendACK(PUBACK, clientLocation);
        sender().tell(sendACK, getSelf());

        // Ask TileShardEntity Entity
        processPUBLISH.clientLocation = clientLocation;
        topicShardRegion.tell(processPUBLISH, getSelf());
    }


    private void receivePublisherGeoMatching(PublisherGeoMatching publisherGeoMatching){
        log.info("Doing the PublisherGeoMatching for " + clientShardEntityId);
        // Publisher geo matching
        Boolean isPublisherGeoMatching = publisherGeoMatching(publisherGeoMatching.publication);
        if(isPublisherGeoMatching){
            ExternalMessage externalMessage = new ExternalMessage(
                    clientShardEntityId,
                    ControlPacketType.MATCH,
                    publisherGeoMatching.publication.message.getPayload().getPUBLISHPayload()
            );
            log.info("Sending MATCH message to " + wsClientActor);
            wsClientActor.tell(externalMessage, getSelf());
        }
    }

    private Boolean publisherGeoMatching(ProcessPUBLISH publication){
        // Match message geofence & subscriber location.
        if(publication.message.getPayload().getPUBLISHPayload().geofence.contains(clientLocation)){
            return true;
        }
        return false;
    }


}