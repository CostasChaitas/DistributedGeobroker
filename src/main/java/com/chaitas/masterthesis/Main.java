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
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Main {

    public static void main(String[] args) {
        Config baseConfig = ConfigFactory.load();
        String actorSystemName = "master-thesis-cluster";
        // If we have added ports on the command line, then override them in the config and start multiple actor systems
        if (args.length > 0) {
            // Check that we have an even number of ports, one remoting and one http for each actor system
            if (args.length % 2 == 1) {
                System.out.println("[ERROR] Need an even number of ports! One remoting and one HttpServerActor port for each actor system.");
                System.exit(1);
            }
            for (int i = 0; i < args.length; i += 2) {
                String remoting = args[i];
                String http = args[i + 1];
                // Override the configuration of the port
                Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port = " + remoting);
                createAndStartActorSystem(actorSystemName, config);
            }
        }
        else {
            createAndStartActorSystem(actorSystemName, baseConfig);
        }
    }

    private static void createAndStartActorSystem(String name, Config config) {
        // Create an Akka system
        ActorSystem system = ActorSystem.create(name, config);
        // Start Akka management and Cluster Bootstrap on the system
        AkkaManagement.get(system).start();
        ClusterBootstrap.get(system).start();
        // Set up and start Cluster Sharding
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
