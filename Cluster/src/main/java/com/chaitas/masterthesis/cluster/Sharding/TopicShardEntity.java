package com.chaitas.masterthesis.cluster.Sharding;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.chaitas.masterthesis.cluster.Messages.*;
import com.chaitas.masterthesis.cluster.Storage.Raster;
import com.chaitas.masterthesis.cluster.Storage.Subscription;
import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.spatial.Geofence;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class TopicShardEntity extends AbstractActor {

    private final Address actorSystemAddress = getContext().system().provider().getDefaultAddress();
    private LoggingAdapter log = Logging.getLogger(getContext().system(), getSelf().path().toStringWithAddress(actorSystemAddress));

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
        log.info("TopicShardEntity actor received ProcessUNSUBSCRIBE " + topicShardId);
        // Unsubscribe
        String clientId = processUNSUBSCRIBE.message.getClientIdentifier();

        Subscription subscription = subscriptions.get(processUNSUBSCRIBE.message.getClientIdentifier());

        ImmutablePair subscriptionId = new ImmutablePair(clientId, subscription.getSubscriptionId().getRight());

        raster.removeSubscriptionIdFromRasterEntries(subscription.getGeofence(), subscriptionId);
    }

    private void receiveProcessSUBSCRIBE(ProcessSUBSCRIBE processSUBSCRIBE){
        log.info("TopicShardEntity actor received ProcessSUBSCRIBE " + topicShardId);

        // Register subscription for the client
        String clientId = processSUBSCRIBE.message.getClientIdentifier();
        String subId = UUID.randomUUID().toString();
        ImmutablePair<String, String> subscriptionId = new ImmutablePair(clientId, subId);
        Topic topic = processSUBSCRIBE.message.getPayload().getSUBSCRIBEPayload().topic;
        Geofence geofence = processSUBSCRIBE.message.getPayload().getSUBSCRIBEPayload().geofence;
        ActorRef wsClientActor = processSUBSCRIBE.wsClientActor;
        Subscription subscription = new Subscription(subscriptionId, topic, geofence, wsClientActor);

        subscriptions.put(clientId, subscription);
        raster.putSubscriptionIdIntoRasterEntries(geofence, subscriptionId);

    }

    private void receiveProcessPUBLISH(ProcessPUBLISH processPUBLISH){
        log.info("TopicShardEntity actor received ProcessPUBLISH " + topicShardId);

        // Subscriber PublisherGeoMatching
        List<ImmutablePair<String, String>> subscriptionIds = raster.getSubscriptionIdsInRasterEntryForPublisherLocation(processPUBLISH.clientLocation);
        log.info("Subscriptions found : " + subscriptionIds.size());

        subscriptionIds.forEach((sub) -> {
            Subscription subscription = subscriptions.get(sub.left);
            PublisherGeoMatching publisherGeoMatching = new PublisherGeoMatching(processPUBLISH, subscription);
            clientShardRegion.tell(publisherGeoMatching, getSelf());
        });

    }


}
