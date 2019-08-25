package com.chaitas.masterthesis.cluster.Location;

import akka.actor.AbstractActor;
import akka.actor.Address;
import akka.actor.PoisonPill;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.chaitas.masterthesis.cluster.Messages.*;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.payloads.PUBACKPayload;
import com.chaitas.masterthesis.commons.payloads.SUBACKPayload;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TileManager extends AbstractActor {

    private final Address actorSystemAddress = getContext().system().provider().getDefaultAddress();
    private LoggingAdapter log = Logging.getLogger(getContext().system(), getSelf().path().toStringWithAddress(actorSystemAddress));
    private String tileId;

    private static Map<Topic, Map<String, ProcessSUBSCRIBE>> subs = new ConcurrentHashMap<>();

    @Override
    public void preStart() throws Exception {
        super.preStart();
        tileId = getSelf().path().name();
        log.info("Started TileManager actor {}", tileId);
        getContext().setReceiveTimeout(Duration.ofSeconds(120));
    }

    private void passivate() {
        log.info("Passivate TileManager actor {}", tileId);
        getContext().getParent().tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()

                .match(ProcessSUBSCRIBE.class, message-> receiveProcessSUBSCRIBE(message))

                .match(ProcessPUBLISH.class, message-> receiveProcessPUBLISH(message))

                // .matchEquals(ReceiveTimeout.getInstance(), msg -> passivate())
                .build();
    }


    private void receiveProcessSUBSCRIBE(ProcessSUBSCRIBE processSubscribe){
        log.info("TileManager actor received ProcessSUBSCRIBE " + tileId);

        // Create SUBACK
        SUBACKPayload subackPayload = new SUBACKPayload(ReasonCode.Success);
        InternalServerMessage SUBACK = new InternalServerMessage(
                processSubscribe.message.getClientIdentifier(),
                ControlPacketType.SUBACK,
                subackPayload
        );
        SendSUBACK sendSuback = new SendSUBACK(SUBACK);
        // Sending SendSUBACK to the responsible wsClientActor
        processSubscribe.wsClientActor.tell(sendSuback, getSender());
        log.info("Sending SUBACK : " + SUBACK + " to clientActor : " + processSubscribe.wsClientActor);

        // Register Subscription
        Topic topic = processSubscribe.message.getPayload().getSUBSCRIBEPayload().getTopic();
        Map<String, ProcessSUBSCRIBE> subsForTopic = subs.get(topic);

        if(subsForTopic == null ){
            subsForTopic = new ConcurrentHashMap<>();
        }

        subsForTopic.put(processSubscribe.message.getClientIdentifier(), processSubscribe);

        subs.put(topic, subsForTopic);

        log.info("There are  : " + subsForTopic.size() + " subs for topic "   + topic + " in tile " + tileId);

    }

    private void receiveProcessPUBLISH(ProcessPUBLISH processPublish){
        log.info("TileManager actor received ProcessPUBLISH " + tileId);

        // Create PUBACK
        PUBACKPayload pubackPayload = new PUBACKPayload(ReasonCode.Success);
        InternalServerMessage PUBACK = new InternalServerMessage(
                processPublish.message.getClientIdentifier(),
                ControlPacketType.PUBACK,
                pubackPayload
        );
        SendPUBACK sendPuback = new SendPUBACK(PUBACK);
        // Sending SendPUBACK to the responsible wsClientActor
        processPublish.wsClientActor.tell(sendPuback, getSender());
        log.info("Sending PUBACK : " + PUBACK + " to clientActor : " + processPublish.wsClientActor);

        // Do the topic matching
        List<ProcessSUBSCRIBE> subsForTopic = topicMatching(processPublish);

        if(subsForTopic != null){
            log.info("Topic matching found " + subsForTopic.size() + " subscriptions");

            // make forEach here
            for(int i=0; i< subsForTopic.size(); i++){

                GeoMatching geoMatching = new GeoMatching(processPublish, subsForTopic.get(i));
                subsForTopic.get(i).wsClientActor.tell(geoMatching, getSelf());

            }
        }

    }

    private List<ProcessSUBSCRIBE> topicMatching(ProcessPUBLISH processPublish) {
        Topic topic = processPublish.message.getPayload().getPUBLISHPayload().getTopic();
        Map<String, ProcessSUBSCRIBE> subsForTopicPerClient = subs.get(topic);
        List<ProcessSUBSCRIBE> subsForTopic = new ArrayList(subsForTopicPerClient.values());
        return subsForTopic;
    }

}
