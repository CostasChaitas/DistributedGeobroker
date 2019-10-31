package com.chaitas.distributed.geobroker.Actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ControlPacketType;
<<<<<<< HEAD:src/main/java/com/chaitas/distributed/geobroker/Actors/WsClientActor.java
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ExternalMessage;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.Payloads.*;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ReasonCode;
=======
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.Payloads.*;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ReasonCode;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ExternalMessage;
>>>>>>> kubernetes:src/main/java/com/chaitas/distributed/geobroker/Actors/WsClientActor.java
import com.chaitas.distributed.geobroker.Messages.InternalMessages.*;

public class WsClientActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
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
            .match(Publish.class, message-> receivePublish(message))
            .build();
    }

    private void receiveExternalMessage(ExternalMessage message) {
        log.info("WsClientActor Actor received ExternalMessage ");
        switch (message.getControlPacketType()) {
                case CONNECT:
                    CONNECTPayload connectPayload = message.getPayload().getCONNECTPayload();
                    if (connectPayload != null) {
                        log.info("WsClientActor {} received message CONNECTPayload", wsClientActorId);
                        ProcessCONNECT processCONNECT = new ProcessCONNECT(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processCONNECT, getSelf());
                    }
                    break;
                case DISCONNECT:
                    DISCONNECTPayload disconnectPayload = message.getPayload().getDISCONNECTPayload();
                    if (disconnectPayload != null) {
                        log.info("WsClientActor {} received message DISCONNECTPayload", wsClientActorId);
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
                        log.info("WsClientActor {} received message PINGREQ", wsClientActorId);
                        ProcessPINGREQ processPINGREQ = new ProcessPINGREQ(message, getSelf());
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processPINGREQ, getSelf());
                    }
                    break;
                case SUBSCRIBE:
                    SUBSCRIBEPayload subscribePayload = message.getPayload().getSUBSCRIBEPayload();
                    if (subscribePayload != null) {
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
                    }
                    break;
                case PUBLISH:
                    PUBLISHPayload publishPayload = message.getPayload().getPUBLISHPayload();
                    if (publishPayload != null) {
                        log.info("WsClientActor {} received message PUBLISH", wsClientActorId);
                        ProcessPUBLISH processPUBLISH = new ProcessPUBLISH(message, getSelf(), null);
                        // Ask clientShardRegion Entity
                        clientShardRegion.tell(processPUBLISH, getSelf());
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
                    log.info("Cannot process message +" + message.toString());
        }
    }

    private void receiveSendACK(SendACK sendACK) {
        log.info("WsClientActor {} received message SendACK", wsClientActorId);
        outgoing.tell(sendACK.message, getSender());
    }

    private void receivePublish(Publish publish) {
        log.info("WsClientActor {} received message Publish", wsClientActorId);
        outgoing.tell(publish.message, getSender());
    }

    private void receiveOutgoingDestination(OutgoingDestination message) {
        log.info("WsClientActor {} received message OutgoingDestination", wsClientActorId);
        outgoing = message.destination;
    }

}



