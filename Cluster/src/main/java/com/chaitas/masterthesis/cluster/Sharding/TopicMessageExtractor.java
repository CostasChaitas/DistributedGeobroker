package com.chaitas.masterthesis.cluster.Sharding;

import akka.cluster.sharding.ShardRegion;
import com.chaitas.masterthesis.cluster.Messages.*;

public class TopicMessageExtractor {

    private final static Integer maxNumberOfShards = 100;

    public static final ShardRegion.MessageExtractor MESSAGE_EXTRACTOR = new ShardRegion.HashCodeMessageExtractor(maxNumberOfShards) {

        // This method will map an incoming message to an entity identifier
        @Override
        public String entityId(Object msg) {
            if (msg instanceof ProcessUNSUBSCRIBE) {
                return String.valueOf(((ProcessUNSUBSCRIBE) msg).message.getPayload().getUNSUBSCRIBEPayload().topic.getTopic());
            }else if (msg instanceof ProcessSUBSCRIBE) {
                return String.valueOf(((ProcessSUBSCRIBE) msg).message.getPayload().getSUBSCRIBEPayload().topic.getTopic());
            }else if (msg instanceof ProcessPUBLISH) {
                return String.valueOf(((ProcessPUBLISH) msg).message.getPayload().getPUBLISHPayload().topic.getTopic());
            } else {
                return null;
            }
        }

        // This method will map an incoming message to the message that should be sent to the entity
        @Override
        public Object entityMessage(Object msg) {
            if (msg instanceof ProcessUNSUBSCRIBE) {
                return msg;
            }else if (msg instanceof ProcessSUBSCRIBE) {
                return msg;
            }else if (msg instanceof ProcessPUBLISH) {
                return msg;
            }else {
                return null;
            }
        }

    };

}
