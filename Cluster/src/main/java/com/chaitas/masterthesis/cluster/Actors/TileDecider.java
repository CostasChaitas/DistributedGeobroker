package com.chaitas.masterthesis.cluster.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import com.chaitas.masterthesis.cluster.Messages.processPUBLISH;
import com.chaitas.masterthesis.cluster.Messages.processSUBSCRIBE;

import java.util.concurrent.ThreadLocalRandom;


public class TileDecider extends AbstractActor {

    private final Address actorSystemAddress = getContext().system().provider().getDefaultAddress();
    private LoggingAdapter log = Logging.getLogger(getContext().system(), getSelf().path().toStringWithAddress(actorSystemAddress));

    private String tileDeciderId;
    private ActorRef shardRegion;

    public TileDecider(ActorRef shardRegion) {
        this.shardRegion = shardRegion;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        tileDeciderId = getSelf().path().name();
        log.info("Creating TileDecider Actor : {}", tileDeciderId);
    }

    @Override
    public void postStop() throws Exception {
        log.info("Shutting down TileDecider Actor : {}", tileDeciderId);
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()

                .match(processPUBLISH.class, message-> receiveProcessPUBLISH(message))
                .match(processSUBSCRIBE.class, message-> receiveProcessSUBSCRIBE(message))

                .build();
    }

    private void receiveProcessPUBLISH(processPUBLISH message){
        log.info("TileDecider Actor received processPUBLISH ");
        int randomNum = findTiles("test");
        message.tileId = randomNum;
        Patterns.ask(shardRegion, message, 3000);
    }

    private void receiveProcessSUBSCRIBE(processSUBSCRIBE message){
        log.info("TileDecider Actor received processSUBSCRIBE ");
        int randomNum = findTiles("test");
        message.tileId = randomNum;
        Patterns.ask(shardRegion, message, 3000);
    }


    // Normaly receives spatial.Geofence
    private int findTiles(String geofence){

        // DO JOB HERE

        int randomNum = ThreadLocalRandom.current().nextInt(0, 10 + 1);
        log.info("Tile Decider actor finds  tileId:  " + randomNum);

        return randomNum;
    }

}



