package com.chaitas.masterthesis.cluster.Actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chaitas.masterthesis.cluster.Messages.*;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.ExternalMessage;
import com.chaitas.masterthesis.commons.payloads.*;

public class WsClientActor extends AbstractActor {

    private final Address actorSystemAddress = getContext().system().provider().getDefaultAddress();
    private LoggingAdapter log = Logging.getLogger(getContext().system(), getSelf().path().toStringWithAddress(actorSystemAddress));

    private final ActorRef clientShardRegion;
    private String wsClientActorId;
    private ActorRef outgoing;

    public WsClientActor(ActorRef clientShardRegion) {
        this.clientShardRegion = clientShardRegion;
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
            // Incoming from WS
            .match(ExternalMessage.class, message-> receiveExternalMessage(message))
            .match(OutgoingDestination.class, message -> receiveOutgoingDestination(message))
            // Incoming from Sharding Entity - ClientShardEntity
            .match(SendACK.class, message-> receiveSendACK(message))
            .build();
    }

    private void receiveExternalMessage(ExternalMessage message) {
        log.info("WsClientActor Actor received ExternalMessage ");

        switch (message.getControlPacketType()) {
                case CONNECT:
                    CONNECTPayload connectPayload = message.getPayload().getCONNECTPayload();
                    if (connectPayload != null) {
                        log.info("Message CONNECTPayload received :" + connectPayload.location);
                        ProcessCONNECT processCONNECT = new ProcessCONNECT(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processCONNECT, getSelf());
                    }
                    break;
                case DISCONNECT:
                    DISCONNECTPayload disconnectPayload = message.getPayload().getDISCONNECTPayload();
                    if (disconnectPayload != null) {
                        System.out.println("Message DISCONNECTPayload received :" + disconnectPayload.reasonCode);
                        ProcessDISCONNECT processDISCONNECT = new ProcessDISCONNECT(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processDISCONNECT, getSelf());
                        // Killing current WsClientActor
                        getSelf().tell(PoisonPill.getInstance(), getSelf());
                    }
                    break;
                case PINGREQ:
                    PINGREQPayload pingreqPayload = message.getPayload().getPINGREQPayload();
                    if (pingreqPayload != null) {
                        log.info("Message PINGREQ received :" + pingreqPayload.location);
                        ProcessPINGREQ processPINGREQ = new ProcessPINGREQ(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processPINGREQ, getSelf());
                    }
                    break;
                // Same case
                case SUBSCRIBE:
                    SUBSCRIBEPayload subscribePayload = message.getPayload().getSUBSCRIBEPayload();
                    if (subscribePayload != null) {
                        log.info("WsClientActor Actor: Message SUBSCRIBE received :");
                        ProcessSUBSCRIBE processSUBSCRIBE = new ProcessSUBSCRIBE(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processSUBSCRIBE, getSelf());
                    }
                    break;
                case UNSUBSCRIBE:
                    UNSUBSCRIBEPayload unsubscribePayload = message.getPayload().getUNSUBSCRIBEPayload();
                    if (unsubscribePayload != null) {
                        log.info("WsClientActor Actor: Message UNSUBSCRIBE received :");
                        ProcessUNSUBSCRIBE processUNSUBSCRIBE = new ProcessUNSUBSCRIBE(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processUNSUBSCRIBE, getSelf());
                    }
                    break;
                case PUBLISH:
                    PUBLISHPayload publishPayload = message.getPayload().getPUBLISHPayload();
                    if (publishPayload != null) {
                        log.info("WsClientActor Actor: Message PUBLISH received :");
                        ProcessPUBLISH processPUBLISH = new ProcessPUBLISH(message, getSelf(), null);
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processPUBLISH, getSelf());
                    }
                    break;
                case MATCH:
                    PUBLISHPayload publishPayload1 = message.getPayload().getPUBLISHPayload();
                    if (publishPayload1 != null) {
                        log.info("WsClientActor Actor: Message MATCH received :");
                        outgoing.tell(message, getSelf());
                    }
                    break;
                default:
                    ExternalMessage externalMessage = new ExternalMessage(
                            "404",
                            ControlPacketType.INCOMPATIBLEPayload,
                            new INCOMPATIBLEPayload(ReasonCode.IncompatiblePayload)
                    );
                    outgoing.tell(externalMessage, getSender());
                    log.info("Cannot process message +" + message.toString());
        }
    }

    private void receiveSendACK(SendACK sendACK) {
        log.info("WsClientActor Actor: Message SendACK received" );
        outgoing.tell(sendACK.message, getSender());
    }

    private void receiveOutgoingDestination(OutgoingDestination message) {
        log.info("WsClientActor Actors received OutgoingDestination msg : {}", message.destination);
        outgoing = message.destination;
    }

}



