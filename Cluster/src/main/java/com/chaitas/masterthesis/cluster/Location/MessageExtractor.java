package com.chaitas.masterthesis.cluster.Location;

import akka.cluster.sharding.ShardRegion;
import com.chaitas.masterthesis.cluster.Messages.processPUBLISH;
import com.chaitas.masterthesis.cluster.Messages.processSUBSCRIBE;
import com.chaitas.masterthesis.commons.spatial.Location;


public class MessageExtractor {

    public static final ShardRegion.MessageExtractor MESSAGE_EXTRACTOR = new ShardRegion.MessageExtractor() {

        // This method will map an incoming message to an entity identifier
        @Override
        public String entityId(Object msg) {
            System.out.println("entityId : " + msg);

           if (msg instanceof Location) {
                System.out.println("testingout : " + String.valueOf(((Location) msg).lat));
                return String.valueOf(((Location) msg).lat);
           }else if (msg instanceof processPUBLISH) {
               System.out.println(String.valueOf(((processPUBLISH) msg).tileId));
               return String.valueOf(((processPUBLISH) msg).tileId);
           } else if (msg instanceof processSUBSCRIBE)
               return String.valueOf(((processSUBSCRIBE) msg).tileId);
           else
                return null;
        }

        // This method will map an incoming message to the message that should be sent to the entity
        @Override
        public Object entityMessage(Object msg) {
            System.out.println("entityMessage : " + msg);
            if (msg instanceof Location)
                return msg;
            else if (msg instanceof processPUBLISH || msg instanceof processSUBSCRIBE)
                return msg;
            else
                return msg;
        }

        // This method will map an incoming message to a shard identifier
        @Override
        public String shardId(Object msg) {
            System.out.println("shardId : " + msg);

            int numberOfShards = 100;
            if (msg instanceof Location) {
                long id = 50;
                return String.valueOf(id % numberOfShards);
                // Needed if you want to use 'remember entities', that will restart entities if a shard is rebalanced,
                // otherwise entities are restarted on the first message they receive.
                //
                // Think carefully about using 'remember entities' and the implications of starting all entities
                // in the rebalanced shard, since it can put more load on your persistence mechanism, maybe at a time
                // when it is already under load, or the network is under load.
                //
                // } else if (message instanceof ShardRegion.StartEntity) {
                //   long id = ((ShardRegion.StartEntity) message).id;
                //   return String.valueOf(id % numberOfShards)
            } else if (msg instanceof processPUBLISH) {
                long id = ((processPUBLISH) msg).tileId;
                return String.valueOf(id % numberOfShards);
            } else if (msg instanceof processSUBSCRIBE) {
                long id = ((processSUBSCRIBE) msg).tileId;
                return String.valueOf(id % numberOfShards);
            }else {
                return null;
            }
        }
    };

}
