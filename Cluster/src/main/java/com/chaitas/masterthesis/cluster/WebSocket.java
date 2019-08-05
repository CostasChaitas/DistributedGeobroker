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
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import com.chaitas.masterthesis.cluster.Location.MessageExtractor;
import com.chaitas.masterthesis.cluster.Location.TileManager;
import com.chaitas.masterthesis.cluster.Routes.WebSocketRoutes;
import com.typesafe.config.Config;

import java.util.concurrent.CompletionStage;


public class WebSocket extends AbstractActor {

    public enum Stop {}

    private final ActorContext context = getContext();
    private final ActorSystem system = context.getSystem();
    private final LoggingAdapter log = Logging.getLogger(system, this);
    private final CompletionStage<ServerBinding> binding; // The HTTP server binding


    public WebSocket() {
        // Set up and start Cluster Sharding
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        ActorRef shardRegion = ClusterSharding.get(system)
                .start(
                        "Tile",
                        Props.create(TileManager.class),
                        settings,
                        MessageExtractor.MESSAGE_EXTRACTOR);

        // Set up TCP WebSocket Server
        final Materializer materializer = ActorMaterializer.create(system);
        Http http = Http.get(system);

        WebSocketRoutes routes = new WebSocketRoutes(system, shardRegion);

        Config config = system.settings().config();
        String hostname = config.getString("api.http.hostname");
        int port = config.getInt("api.http.port");


        //router
        Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                routes.createRoute().flow(system, materializer);

        binding = Http.get(system).bindAndHandle(
                routeFlow, ConnectHttp.toHost(hostname, port), materializer);

        log.info("New Server online at ws://{}:{}/", hostname, port);

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Stop.class, stop -> {
                    log.info("Shutting down!");
                    binding
                            .thenCompose(ServerBinding::unbind) // Trigger HTTP server unbinding from the port
                            .thenAccept(unbound -> CoordinatedShutdown.get(system)); // and shutdown when done
                })
                .build();
    }
}
