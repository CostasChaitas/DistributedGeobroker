package com.chaitas.masterthesis.cluster.Sharding;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.chaitas.masterthesis.cluster.Messages.*;
import com.chaitas.masterthesis.cluster.Storage.Raster;
import com.chaitas.masterthesis.cluster.Storage.Subscription;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TopicShardEntity extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef clientShardRegion;
    private String topicShardId;
    private Raster raster;
    private Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();

    public TopicShardEntity(ActorRef clientShardRegion) {
        this.clientShardRegion = clientShardRegion;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        topicShardId = getSelf().path().name();
        raster = new Raster(1);
        log.info("Started TopicShardEntity actor {}", topicShardId);
        getContext().setReceiveTimeout(Duration.ofSeconds(120));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // Incoming messages from WsClientActor
                .match(ProcessUNSUBSCRIBE.class, message-> receiveProcessUNSUBSCRIBE(message))
                .match(ProcessSUBSCRIBE.class, message-> receiveProcessSUBSCRIBE(message))
                .match(ProcessPUBLISH.class, message-> receiveProcessPUBLISH(message))
                .build();
    }

    private void receiveProcessUNSUBSCRIBE(ProcessUNSUBSCRIBE processUNSUBSCRIBE){
        log.info("TopicShardEntity {} received message ProcessUNSUBSCRIBE", topicShardId);
        // Unsubscribe
        Subscription subscription = subscriptions.get(processUNSUBSCRIBE.message.getClientIdentifier());
        raster.removeSubscriptionIdFromRasterEntries(subscription.getGeofence(), subscription.getSubscriptionId());
    }

    private void receiveProcessSUBSCRIBE(ProcessSUBSCRIBE processSUBSCRIBE){
        log.info("TopicShardEntity {} received message ProcessSUBSCRIBE", topicShardId);
        // Check if client has been already subscribed and remove old subscriptions
        Subscription subscription = subscriptions.get(processSUBSCRIBE.subscription.getSubscriptionId().getLeft());
        if(subscription != null){
            log.info("Subscription exist" );
            raster.removeSubscriptionIdFromRasterEntries(subscription.getGeofence(), subscription.getSubscriptionId());
        }
        // Register subscription for the client
        subscriptions.put(processSUBSCRIBE.subscription.getSubscriptionId().getLeft(), processSUBSCRIBE.subscription);
        raster.putSubscriptionIdIntoRasterEntries(processSUBSCRIBE.subscription.getGeofence(), processSUBSCRIBE.subscription.getSubscriptionId());
    }

    private void receiveProcessPUBLISH(ProcessPUBLISH processPUBLISH){
        log.info("TopicShardEntity {} received message ProcessPUBLISH", topicShardId);
        // Subscriber GeoMatching
        List<ImmutablePair<String, String>> subscriptionIds = raster.getSubscriptionIdsInRasterEntryForPublisherLocation(processPUBLISH.clientLocation);
        subscriptionIds.forEach((sub) -> {
            Subscription subscription = subscriptions.get(sub.left);
            PublisherGeoMatching publisherGeoMatching = new PublisherGeoMatching(processPUBLISH, subscription);
            // Check if publisher is the subscriber
            if(processPUBLISH.message.getClientIdentifier().compareTo(subscription.getSubscriptionId().getLeft()) != 0) {
                clientShardRegion.tell(publisherGeoMatching, getSelf());
            }
        });
    }

}
