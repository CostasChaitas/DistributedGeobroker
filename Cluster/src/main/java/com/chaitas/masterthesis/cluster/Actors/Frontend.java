package com.chaitas.masterthesis.cluster.Actors;

import akka.actor.*;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chaitas.masterthesis.cluster.Messages.OutgoingDestination;
import com.chaitas.masterthesis.cluster.Messages.processPUBLISH;
import com.chaitas.masterthesis.cluster.Messages.processSUBSCRIBE;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.chaitas.masterthesis.commons.payloads.PUBLISHPayload;
import com.chaitas.masterthesis.commons.payloads.SUBSCRIBEPayload;

public class Frontend extends AbstractActor {

    private final ActorSystem system = getContext().system();
    private final Address actorSystemAddress = getContext().system().provider().getDefaultAddress();
    private LoggingAdapter log = Logging.getLogger(getContext().system(), getSelf().path().toStringWithAddress(actorSystemAddress));

    private String frontendId;
    private ActorRef outgoing;
    private ActorRef tileDeciderActor;
    private ActorRef shardRegion;


    public Frontend(ActorRef shardRegion) {
        this.shardRegion = shardRegion;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        frontendId = getSelf().path().name();
        log.info("Creating Frontend Actor : {}", frontendId);
        tileDeciderActor = system.actorOf(Props.create(TileDecider.class, shardRegion));
    }

    @Override
    public void postStop() throws Exception {
        log.info("Shutting down Frontend Actor : {}", frontendId);
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()

                .match(InternalServerMessage.class, message-> receiveInternalServerMessage(message))

                .match(OutgoingDestination.class, message -> receiveOutgoingDestination(message))
                // .match(BADPayload.class, message-> receiveBadPayload(message))
                // .matchEquals(ReceiveTimeout.getInstance(), msg -> passivate())
                .build();
    }


    private void receiveInternalServerMessage(InternalServerMessage message) {
        log.info("Frontend Actor received InternalServerMessage ");

        switch (message.getControlPacketType()) {

                case SUBSCRIBE:
                    SUBSCRIBEPayload subscribePayload = message.getPayload().getSUBSCRIBEPayload();
                    if (subscribePayload != null) {
                        System.out.println("Message SUBSCRIBE received :" + subscribePayload.getTopic());
                        tileDeciderActor.tell(new processSUBSCRIBE(message, outgoing),  getSelf());
                        //Sending back ACK

//                        SUBACKPayload subackPayload = new SUBACKPayload(ReasonCode.Success);

//                        InternalServerMessage internalServerMessage = new InternalServerMessage(
//                                message.getClientIdentifier(),
//                                ControlPacketType.SUBACK,
//                                subackPayload
//                        );

//                        outgoing.tell(internalServerMessage, getSender());

                    }
                    break;
                case PUBLISH:
                    PUBLISHPayload publishPayload = message.getPayload().getPUBLISHPayload();
                    if (publishPayload != null) {
                        System.out.println("Frontend Actor: Message PUBLISH received :" + publishPayload.getTopic());
                        tileDeciderActor.tell(new processPUBLISH(message, outgoing),  getSelf());
                        //Sending back ACK
//                        InternalServerMessage internalServerMessage = new InternalServerMessage(
//                                message.getClientIdentifier(),
//                                ControlPacketType.PUBACK,
//                                message.getPayload()
//                        );
//                        outgoing.tell(internalServerMessage, getSender());
                    }
                    break;
                default:
                    // BADPayload badPayload = new BADPayload("Cannot process message", ReasonCode.IncompatiblePayload, message.getClientIdentifier());
                    System.out.println("Cannot process message +" + message.toString());
                    // receiveBadPayload(badPayload);

        }
    }


//    private void receiveBadPayload(BADPayload message) {
//        log.info("Frontend Actor received BADPayload ");
//
//        BADPayload badPayload = new BADPayload(message.message, message.reasonCode, message.clientIdentifier);
//        InternalServerMessage internalServerMessage =  new InternalServerMessage(
//                badPayload.clientIdentifier,
//                ControlPacketType.CONNACK,
//                badPayload
//        );
//        outgoing.tell(internalServerMessage, getSender());
//    }


    private void receiveOutgoingDestination(OutgoingDestination message) {
        log.info("Frontend Actors received OutgoingDestination msg : {}", message.destination);
        outgoing = message.destination;
    }

    private void passivate() {
        // Tell our shard region that we want to shut down to free up resources
        getContext().getParent().tell(
                new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
    }
}



