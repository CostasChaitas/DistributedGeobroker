package com.chaitas.masterthesis.cluster.Actors;

import akka.actor.*;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import com.chaitas.masterthesis.cluster.Messages.OutgoingDestination;
import com.chaitas.masterthesis.cluster.Messages.processPUBLISH;
import com.chaitas.masterthesis.cluster.Messages.sendSUBACK;
import com.chaitas.masterthesis.cluster.Messages.processSUBSCRIBE;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.chaitas.masterthesis.commons.payloads.*;
import com.chaitas.masterthesis.commons.spatial.Location;

import java.util.concurrent.ThreadLocalRandom;

public class WsClientActor extends AbstractActor {

    private final Address actorSystemAddress = getContext().system().provider().getDefaultAddress();
    private LoggingAdapter log = Logging.getLogger(getContext().system(), getSelf().path().toStringWithAddress(actorSystemAddress));

    private String wsClientActorId;
    private ActorRef outgoing;
    private ActorRef shardRegion;
    private Boolean isConnected = false;
    private Location location;

    public WsClientActor(ActorRef shardRegion) {
        this.shardRegion = shardRegion;
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

                .match(InternalServerMessage.class, message-> receiveInternalServerMessage(message))

                .match(sendSUBACK.class, message-> receiveSendSuback(message))

                .match(INCOMPATIBLEPayload.class, message-> receiveIncompatiblePayload())

                .match(OutgoingDestination.class, message -> receiveOutgoingDestination(message))

                // .matchEquals(ReceiveTimeout.getInstance(), msg -> passivate())
                .build();
    }


    private void receiveInternalServerMessage(InternalServerMessage message) {
        log.info("WsClientActor Actor received InternalServerMessage ");

        switch (message.getControlPacketType()) {
                case CONNECT:
                    CONNECTPayload connectPayload = message.getPayload().getCONNECTPayload();
                    if (connectPayload != null) {
                        log.info("Message CONNECTPayload received :" + connectPayload.location);

                        // Connect WsClient and update the location
                        this.isConnected = true;
                        this.location = connectPayload.location;

                        log.info("Client is connected with location :" + this.location);

                        CONNACKPayload connackPayload = new CONNACKPayload(ReasonCode.Success);

                        InternalServerMessage internalServerMessage = new InternalServerMessage(
                                message.getClientIdentifier(),
                                ControlPacketType.CONNACK,
                                connackPayload
                        );

                        outgoing.tell(internalServerMessage, getSender());
                    }
                    break;
                case DISCONNECT:
                    DISCONNECTPayload disconnectPayload = message.getPayload().getDISCONNECTPayload();
                    if (disconnectPayload != null) {
                        System.out.println("Message DISCONNECTPayload received :" + disconnectPayload.reasonCode);
                        // Killing current WsClientActor
                        getSelf().tell(PoisonPill.getInstance(), getSelf());
                    }
                    break;
                case PINGREQ:
                    PINGREQPayload pingreqPayload = message.getPayload().getPINGREQPayload();
                    if (pingreqPayload != null) {
                        log.info("Message PINGREQ received :" + pingreqPayload.location);

                        // Check if Client is connected
                        if(!this.isConnected){
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
                        this.location = pingreqPayload.location;
                        log.info("Client location :" + this.location);


                        PINGRESPPayload pingrespPayload = new PINGRESPPayload(ReasonCode.Success);

                        InternalServerMessage internalServerMessage = new InternalServerMessage(
                                message.getClientIdentifier(),
                                ControlPacketType.PINGRESP,
                                pingrespPayload
                        );

                        outgoing.tell(internalServerMessage, getSender());
                        // tileDeciderActor.tell(new processSUBSCRIBE(message, outgoing),  getSelf());

                    }
                    break;
                case SUBSCRIBE:
                    SUBSCRIBEPayload subscribePayload = message.getPayload().getSUBSCRIBEPayload();
                    if (subscribePayload != null) {
                        log.info("Message SUBSCRIBE received :" + subscribePayload.getTopic());

                        // Check if Client is connected
                        if(!this.isConnected){
                            SUBACKPayload subackPayload = new SUBACKPayload(ReasonCode.NotConnected);
                            InternalServerMessage internalServerMessage = new InternalServerMessage(
                                    message.getClientIdentifier(),
                                    ControlPacketType.NOTCONNECTED,
                                    subackPayload
                            );
                            outgoing.tell(internalServerMessage, getSender());
                            return;
                        }

                        int randomNum = findTiles("test");
                        processSUBSCRIBE processSUBSCRIBE = new processSUBSCRIBE(message,  getSelf(), randomNum);
                        Patterns.ask(shardRegion, processSUBSCRIBE, 3000);

                    }
                    break;
                case PUBLISH:
                    PUBLISHPayload publishPayload = message.getPayload().getPUBLISHPayload();
                    if (publishPayload != null) {
                        log.info("WsClientActor Actor: Message PUBLISH received :" + publishPayload.getTopic());

                        // Check if Client is connected
                        if(!this.isConnected){
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
                        processPUBLISH processPUBLISH = new processPUBLISH(message, getSelf(), randomNum);
                        Patterns.ask(shardRegion, processPUBLISH, 3000);
                    }
                    break;
                default:
                    log.info("Cannot process message +" + message.toString());
                    receiveIncompatiblePayload();
        }
    }


    private void receiveSendSuback(sendSUBACK sendSuback) {
        log.info("WsClientActor Actor: Message sendSUBACK received :" + sendSuback.message.getPayload());

        outgoing.tell(sendSuback.message, getSender());
    }

    private int findTiles(String geofence){
        // DO JOB HERE

        //NEED TO KNOW all the available tiles
        // num of tiles e.g 100

        int randomNum = ThreadLocalRandom.current().nextInt(0, 10 + 1);
        log.info("TileId's found :  " + randomNum);

        return randomNum;
    }

    private void receiveIncompatiblePayload() {
        log.info("WsClientActor Actor received IncompatibleMessage ");
        INCOMPATIBLEPayload incompatibleMessage = new INCOMPATIBLEPayload(ReasonCode.IncompatiblePayload);
        InternalServerMessage internalServerMessage = new InternalServerMessage(
                "",
                ControlPacketType.INCOMPATIBLEPAYLOAD,
                incompatibleMessage
        );
        outgoing.tell(internalServerMessage, getSender());
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
}



