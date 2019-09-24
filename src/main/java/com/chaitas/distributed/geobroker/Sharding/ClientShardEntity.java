package com.chaitas.distributed.geobroker.Sharding;

import akka.actor.*;
import akka.cluster.sharding.ClusterSharding;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ControlPacketType;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.Payloads.*;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ReasonCode;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ExternalMessage;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.Topic;
import com.chaitas.distributed.geobroker.Messages.InternalMessages.*;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.Spatial.Geofence;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.Spatial.Location;
import com.chaitas.distributed.geobroker.Storage.Subscription;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientShardEntity extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorSystem actorSystem = getContext().getSystem();
    private final ActorRef topicShardRegion = ClusterSharding.get(actorSystem).shardRegion("Topics");
    private String clientShardEntityId;
    private Location clientLocation;
    private ActorRef wsClientActor;
    private Map<Topic, Subscription> subscriptions = new ConcurrentHashMap<>();

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
        log.info("ClientShardEntity {} received message ProcessCONNECT", clientShardEntityId);
        // Check if it's needed to update clientActor
        if(clientLocation != null){
            for (Map.Entry<Topic, Subscription> subValue : subscriptions.entrySet()) {
                Subscription sub = subValue.getValue();
                ExternalMessage SUBSCRIBE = new ExternalMessage(
                        sub.getSubscriptionId().getLeft(),
                        ControlPacketType.SUBSCRIBE,
                        new SUBSCRIBEPayload(sub.getTopic(), sub.getGeofence())
                );
                ProcessSUBSCRIBE processSUBSCRIBE = new ProcessSUBSCRIBE(SUBSCRIBE, processCONNECT.wsClientActor);
                processSUBSCRIBE.subscription = sub;
                topicShardRegion.tell(processSUBSCRIBE, getSelf());
            }
        }
        // Connect ClientShardEntity and update the location
        clientLocation = processCONNECT.message.getPayload().getCONNECTPayload().location;
        wsClientActor = processCONNECT.wsClientActor;
        // Sending ACK
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
        log.info("ClientShardEntity {} received message ProcessDISCONNECT", clientShardEntityId);
        clientLocation = null;
        wsClientActor = null;
    }

    private void receiveProcessPINGREQ(ProcessPINGREQ processPINGREQ) {
        log.info("ClientShardEntity {} received message ProcessPINGREQ", clientShardEntityId);
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
        // Sending ACK
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
        log.info("ClientShardEntity {} received message ProcessUNSUBSCRIBE", clientShardEntityId);
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
        // Send message to TileShardEntity Entity
        topicShardRegion.tell(processUNSUBSCRIBE, getSelf());
        // Sending ACK
        UNSUBACKPayload unsubackPayload = new UNSUBACKPayload(ReasonCode.Success);
        ExternalMessage UNSUBACK = new ExternalMessage(
                processUNSUBSCRIBE.message.getClientIdentifier(),
                ControlPacketType.UNSUBACK,
                unsubackPayload
        );
        SendACK sendACK = new SendACK(UNSUBACK);
        sender().tell(sendACK, getSender());
    }

    private void receiveProcessSUBSCRIBE(ProcessSUBSCRIBE processSUBSCRIBE) {
        log.info("ClientShardEntity {} received message processSUBSCRIBE", clientShardEntityId);
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
        // Creating Subscription
        String clientId = processSUBSCRIBE.message.getClientIdentifier();
        String subId = UUID.randomUUID().toString();
        ImmutablePair<String, String> subscriptionId = new ImmutablePair(clientId, subId);
        Topic topic = processSUBSCRIBE.message.getPayload().getSUBSCRIBEPayload().topic;
        Geofence geofence = processSUBSCRIBE.message.getPayload().getSUBSCRIBEPayload().geofence;
        Subscription subscription = new Subscription(subscriptionId, topic, geofence, wsClientActor);
        // Register subscription for the client
        subscriptions.put(topic, subscription);
        // Send message to TileShardEntity Entity
        processSUBSCRIBE.subscription = subscription;
        topicShardRegion.tell(processSUBSCRIBE, getSelf());
        // Sending ACK
        SUBACKPayload subackPayload = new SUBACKPayload(ReasonCode.Success);
        ExternalMessage SUBACK = new ExternalMessage(
                processSUBSCRIBE.message.getClientIdentifier(),
                ControlPacketType.SUBACK,
                subackPayload
        );
        SendACK sendACK = new SendACK(SUBACK);
        sender().tell(sendACK, getSelf());
    }

    private void receiveProcessPUBLISH(ProcessPUBLISH processPUBLISH) {
        log.info("ClientShardEntity {} received message ProcessPUBLISH", clientShardEntityId);
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
        // Get client location and Send message to TileShardEntity Entity
        processPUBLISH.clientLocation = clientLocation;
        topicShardRegion.tell(processPUBLISH, getSelf());
        // Sending ACK
        PUBACKPayload pubackPayload = new PUBACKPayload(ReasonCode.Success);
        ExternalMessage PUBACK = new ExternalMessage(
                processPUBLISH.message.getClientIdentifier(),
                ControlPacketType.PUBACK,
                pubackPayload
        );
        SendACK sendACK = new SendACK(PUBACK);
        sender().tell(sendACK, getSelf());
    }

    private void receivePublisherGeoMatching(PublisherGeoMatching publisherGeoMatching){
        log.info("ClientShardEntity {} received message PublisherGeoMatching", clientShardEntityId);
        // Publisher geo matching
        Boolean isPublisherGeoMatching = publisherGeoMatching(publisherGeoMatching.publication);
        if(isPublisherGeoMatching){
            ExternalMessage externalMessage = new ExternalMessage(
                    publisherGeoMatching.publication.message.getClientIdentifier(),
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