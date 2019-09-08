package com.chaitas.masterthesis.cluster.Actors;

import akka.NotUsed;
import akka.actor.*;
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

import com.chaitas.masterthesis.cluster.Routes.WebSocketRoutes;
import com.typesafe.config.Config;

import java.util.concurrent.CompletionStage;

public class WsServerActor extends AbstractActor {

    public enum Stop {}

    private final ActorContext context = getContext();
    private final ActorSystem system = context.getSystem();
    private final LoggingAdapter log = Logging.getLogger(system, this);
    private final CompletionStage<ServerBinding> binding; // The HttpServerActor server binding
    private String webSocketActorId;

    public WsServerActor(ActorRef topicShardRegion, ActorRef clientShardRegion) {

        // Set up TCP WsServerActor Server
        final Materializer materializer = ActorMaterializer.create(system);

        WebSocketRoutes routes = new WebSocketRoutes(system, topicShardRegion, clientShardRegion);

        Config config = system.settings().config();
        String hostname = config.getString("api.http.hostname");
        int port = config.getInt("api.http.port");

        // Router
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
                        .thenCompose(ServerBinding::unbind) // Trigger HttpServerActor server unbinding from the port
                        .thenAccept(unbound -> CoordinatedShutdown.get(system)); // and shutdown when done
                })
                .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        webSocketActorId = getSelf().path().name();
        log.info("Creating WsServerActor Actor : {}", webSocketActorId);
    }

    @Override
    public void postStop() throws Exception {
        log.info("Shutting down WsServerActor Actor : {}", webSocketActorId);
        super.postStop();
    }
}
