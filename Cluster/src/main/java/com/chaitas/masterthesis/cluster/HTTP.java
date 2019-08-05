package com.chaitas.masterthesis.cluster;

import akka.NotUsed;
import akka.actor.*;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.chaitas.masterthesis.cluster.Location.MessageExtractor;
import com.chaitas.masterthesis.cluster.Location.TileManager;
import com.chaitas.masterthesis.cluster.Routes.REST;
import com.typesafe.config.Config;

import java.util.concurrent.CompletionStage;

public class HTTP extends AbstractActor {

    public enum Stop {
        INSTANCE
    }

    private final ActorContext context = getContext();
    private final ActorSystem system = context.getSystem();
    private final LoggingAdapter log = Logging.getLogger(system, this);
    private final CompletionStage<ServerBinding> binding; // The HTTP server binding

    public HTTP() {
        // Set up and start Cluster Sharding
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        ActorRef shardRegion = ClusterSharding.get(system)
                .start(
                        "Tile",
                        Props.create(TileManager.class),
                        settings,
                        MessageExtractor.MESSAGE_EXTRACTOR);

        // Set up and start the HTTP server
        Http http = Http.get(system);
        ActorMaterializer materializer = ActorMaterializer.create(system);
        // In order to access all directives we need an instance where the routes are defined
        REST routes = new REST();

        Config config = system.settings().config();
        String hostname = config.getString("api.http.hostname");
        int port = config.getInt("api.http.port");
        Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                routes.createRoute(system, context.self(), shardRegion, system.dispatcher(), hostname, port)
                        .flow(system, materializer);
        binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(hostname, port), materializer);
        log.info("Server online at http://{}:{}/", hostname, port);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Stop.class, stop -> {
                    log.info("Shutting down!");
                    binding
                            .thenCompose(ServerBinding::unbind) // Trigger HTTP server unbinding from the port
                            .thenAccept(unbound -> CoordinatedShutdown.get(system).run()); // and shutdown when done
                })
                .build();
    }
}
