package com.chaitas.masterthesis.cluster.Location;

import akka.actor.AbstractActor;
import akka.actor.Address;
import akka.actor.PoisonPill;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.chaitas.masterthesis.cluster.Messages.processPUBLISH;
import com.chaitas.masterthesis.cluster.Messages.sendSUBACK;
import com.chaitas.masterthesis.cluster.Messages.processSUBSCRIBE;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.payloads.SUBACKPayload;
import com.chaitas.masterthesis.commons.spatial.Location;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TileManager extends AbstractActor {

    private final Address actorSystemAddress = getContext().system().provider().getDefaultAddress();
    private LoggingAdapter log = Logging.getLogger(getContext().system(), getSelf().path().toStringWithAddress(actorSystemAddress));
    private String tileId;

    private static Map<Topic, List<processSUBSCRIBE>> subs = new ConcurrentHashMap<>();

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

                .match(String.class, message-> receiveString(message))

                .match(processPUBLISH.class, message-> receiveProcessPUBLISH(message))
                .match(processSUBSCRIBE.class, message-> receiveProcessSUBSCRIBE(message))

                .match(Location.class, message-> receiveLocation(message))

                // .matchEquals(ReceiveTimeout.getInstance(), msg -> passivate())
                .build();
    }

    private void receiveLocation(Location message) {

        log.info("TileManager actor received Location : " + message);
        getSender().tell("Customer: " +
                getSelf().path().toStringWithAddress(actorSystemAddress) + "\n", getSelf());
    }

    private void receiveString(String message) {
        log.info("TileManager actor received String : " + message);
    }


    private void receiveProcessPUBLISH(processPUBLISH processPublish){
        log.info("TileManager actor received processPUBLISH " + tileId);

        List<processSUBSCRIBE> subsForTopic = topicMatching(processPublish);

        InternalServerMessage PUBACK = new InternalServerMessage(
                processPublish.message.getClientIdentifier(),
                ControlPacketType.PUBACK,
                processPublish.message.getPayload()
        );
        processPublish.wsClientActor.tell(PUBACK, getSender());

        log.info("Sending PUBACK : " + PUBACK + " to clientActor : " + processPublish.wsClientActor);

        if(subsForTopic != null){
            // make forEach here
            for(int i=0; i< subsForTopic.size(); i++){

                log.info("Sending message : " + processPublish.message.getPayload().getPUBLISHPayload().getContent() + " to subscriber : " + subsForTopic.get(i).wsClientActor);

                InternalServerMessage PUBLISH = new InternalServerMessage(
                        subsForTopic.get(i).message.getClientIdentifier(),
                        ControlPacketType.PUBLISH,
                        processPublish.message.getPayload()
                );

                subsForTopic.get(i).wsClientActor.tell(PUBLISH, getSelf());
            }
        }


    }


//    private void subscriberGeoMatching(processSUBSCRIBE processSubscribe, processPUBLISH processPublish){
//        if(processSubscribe.message.getPayload().getSUBSCRIBEPayload().geofence.){
//
//        }
//
//    }


    private List<processSUBSCRIBE> topicMatching(processPUBLISH processPublish) {
        Topic topic = processPublish.message.getPayload().getPUBLISHPayload().getTopic();
        log.info("Topic matching for topic " + topic);

        List<processSUBSCRIBE> subsForTopic = subs.get(topic);
        log.info("Topic matching found " + subsForTopic.size() + " for topic " + topic);
        return subsForTopic;
    }

    private void receiveProcessSUBSCRIBE(processSUBSCRIBE processSubscribe){
        log.info("TileManager actor received processSUBSCRIBE " + tileId);

        // Create SUBACK
        SUBACKPayload subackPayload = new SUBACKPayload(ReasonCode.Success);
        InternalServerMessage SUBACK = new InternalServerMessage(
                processSubscribe.message.getClientIdentifier(),
                ControlPacketType.SUBACK,
                subackPayload
        );

        sendSUBACK sendSuback = new sendSUBACK(SUBACK);
        // Sending sendSUBACK to the responsible wsClientActor
        processSubscribe.wsClientActor.tell(sendSuback, getSender());

        // Register Subscription
        Topic topic = processSubscribe.message.getPayload().getSUBSCRIBEPayload().getTopic();
        List<processSUBSCRIBE> subsForTopic = subs.get(topic);

        if(subsForTopic == null ){
            subsForTopic = new ArrayList<>();
        }

        subsForTopic.add(processSubscribe);
        subs.put(topic, subsForTopic);
        log.info("There are  : " + subsForTopic.size() + " subs for topic "   + topic + " in tile " + tileId);

    }


}
