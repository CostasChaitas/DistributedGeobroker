package com.chaitas.masterthesis.cluster.Sharding;

import akka.cluster.sharding.ShardRegion;
import com.chaitas.masterthesis.cluster.Messages.*;

public class ClientMessageExtractor {

    private final static Integer maxNumberOfShards = 100;

    public static final ShardRegion.MessageExtractor MESSAGE_EXTRACTOR = new ShardRegion.HashCodeMessageExtractor(maxNumberOfShards) {

        // This method will map an incoming message to an entity identifier
        @Override
        public String entityId(Object msg) {
            if (msg instanceof ProcessCONNECT) {
                return String.valueOf(((ProcessCONNECT) msg).message.getClientIdentifier());
            }else if (msg instanceof ProcessDISCONNECT) {
                return String.valueOf(((ProcessDISCONNECT) msg).message.getClientIdentifier());
            }else if (msg instanceof ProcessPINGREQ) {
                return String.valueOf(((ProcessPINGREQ) msg).message.getClientIdentifier());
            }else if (msg instanceof ProcessUNSUBSCRIBE) {
                return String.valueOf(((ProcessUNSUBSCRIBE) msg).message.getClientIdentifier());
            }else if (msg instanceof ProcessSUBSCRIBE) {
                return String.valueOf(((ProcessSUBSCRIBE) msg).message.getClientIdentifier());
            }else if (msg instanceof ProcessPUBLISH) {
                return String.valueOf(((ProcessPUBLISH) msg).message.getClientIdentifier());
            } else if (msg instanceof PublisherGeoMatching) {
                return String.valueOf(((PublisherGeoMatching) msg).subscription.getSubscriptionId().getLeft());
            }else{
                return null;
            }
        }

        // This method will map an incoming message to the message that should be sent to the entity
        @Override
        public Object entityMessage(Object msg) {
            if (msg instanceof ProcessCONNECT)
                return msg;
            else if (msg instanceof ProcessDISCONNECT) {
                return msg;
            }else if (msg instanceof ProcessPINGREQ) {
                return msg;
            }else if (msg instanceof ProcessUNSUBSCRIBE) {
                return msg;
            }else if (msg instanceof ProcessSUBSCRIBE) {
                return msg;
            }else if (msg instanceof ProcessPUBLISH) {
                return msg;
            }else if (msg instanceof PublisherGeoMatching) {
                return msg;
            } else {
                return null;
            }
        }
    };

}
