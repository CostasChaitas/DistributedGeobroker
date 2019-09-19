package com.chaitas.masterthesis;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.management.cluster.bootstrap.ClusterBootstrap;
import akka.management.javadsl.AkkaManagement;
import com.chaitas.masterthesis.Actors.WsServerActor;
import com.chaitas.masterthesis.Sharding.ClientMessageExtractor;
import com.chaitas.masterthesis.Sharding.ClientShardEntity;
import com.chaitas.masterthesis.Sharding.TopicMessageExtractor;
import com.chaitas.masterthesis.Sharding.TopicShardEntity;

public class Main {

    public static void main(String[] args) {
        startupClusterNode();
    }

    private static void startupClusterNode() {
        String actorSystemName = "master-thesis-cluster";
        // Create an Akka system
        ActorSystem system = ActorSystem.create(actorSystemName);
        // Start Akka management and Cluster Bootstrap on the system
        AkkaManagement.get(system).start();
        ClusterBootstrap.get(system).start();
        // Set up and start Cluster Sharding
        setupClusterSharding(system);
    }

    private static void setupClusterSharding(ActorSystem system) {
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        ActorRef clientShardRegion = ClusterSharding.get(system).start(
                "Clients",
                Props.create(ClientShardEntity.class),
                settings,
                ClientMessageExtractor.MESSAGE_EXTRACTOR
        );
        ClusterSharding.get(system).start(
                "Topics",
                Props.create(TopicShardEntity.class, clientShardRegion),
                settings,
                TopicMessageExtractor.MESSAGE_EXTRACTOR
        );
        // Create an actor that starts the TCP Websocket Server
        system.actorOf(Props.create(WsServerActor.class, clientShardRegion));
    }

}
