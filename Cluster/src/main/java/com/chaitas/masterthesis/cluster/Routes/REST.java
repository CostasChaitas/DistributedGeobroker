package com.chaitas.masterthesis.cluster.Routes;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.cluster.Member;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;

import com.chaitas.masterthesis.cluster.HTTP;
import com.chaitas.masterthesis.commons.spatial.Location;
import org.json.JSONObject;
import scala.collection.Iterator;
import scala.collection.SortedSet;
import scala.compat.java8.FutureConverters;
import scala.concurrent.ExecutionContext;

import static akka.http.javadsl.server.PathMatchers.integerSegment;
import static akka.http.javadsl.server.PathMatchers.segment;

// In order to access all directives the code needs to be inside an instance of a class that extends AllDirectives
public class REST extends AllDirectives {

    public Route createRoute(ActorSystem system, ActorRef mainActor, ActorRef shardRegion, ExecutionContext ec, String hostname, int port) {

        final LoggingAdapter log = Logging.getLogger(system, this);
        final String host = hostname + ":" + port;

        return route(

                path(segment("customer2").slash(integerSegment()).slash("address"), id ->
                        onSuccess(() -> FutureConverters.toJava(
                                // Send the ask to the shard region that will route it to the correct actor

                                Patterns.ask(shardRegion, new Location(4.6, id), 3000)
                                        .map(response -> "Server " + host + " replying:\n" + response, ec)
                        ), this::complete)
                ),

//                path("publish", () ->
//                        entity(Jackson.unmarshaller(Publication.class), publication -> {
//
//                            log.info("Publication with message: " +  publication.getMessage() + " to the topic: " + publication.getTopic() + " from " + publication.getId());
//
//                            ActorRef publisherActor = system.actorOf(Props.create(Publisher.class), "publisher-" + publication.getId());
//
//                            log.info("publisherActor: " + publisherActor);
//
//                            publisherActor.tell("Yooo " + publication.getMessage(), null);
//                            return complete("ok");
//                        })
//                ),
//
//                path("subscribe" , () ->
//                        entity(Jackson.unmarshaller(Subscription.class), subscription -> {
//                            log.info("Subscription to topic : " + subscription.getTopic() + " from " + subscription.getId());
//                            ActorRef subscriberActor = system.actorOf(Props.create(Subscriber.class), "subscription-" + subscription.getId() );
//                            return complete("ok");
//                        })
//                ),

                path("geoTest", () ->
                        get(() -> {
//                            Location paris = new Location(48.86, 2.35);
//                            log.info("Paris location = " + paris.getLocation());
//
//                            Location berlin = new Location(52.52, 13.40);
//                            Geofence parisArea = Geofence.circle(paris, 3.0);
//                            Geofence berlinArea = Geofence.circle(berlin, 3.0);
//
//                            log.info("Paris area = {}", parisArea);
//                            log.info("Berlin area = {}", berlinArea);
//                            log.info("The areas intersect: {}", berlinArea.intersects(parisArea));
//
//                            Location justIn = new Location(45.87, 2.3);
//                            log.info("paris contains justin: " + parisArea.contains(justIn));
//
//                            Rectangle[] tiles = Tile.getTiles();

                            return complete("ok");
                        })
                ),

                path("info", () ->
                        get(() -> {
                            Cluster cluster = Cluster.get(system);
                            SortedSet<Member> members = cluster.state().members();
                            final Iterator<Member> iter = members.iterator();

                            JSONObject info = new JSONObject();
                            info.put("System", system);
                            info.put("Shard Region", shardRegion);
                            info.put("Main Actor" , mainActor);
                            info.put("Cluster" , cluster);
                            info.put("Members" , members);
                            info.put("Iter" , iter);
                            return complete(info.toString() );
                        })
                ),

                path("stop", () ->
                        get(() -> {
                            mainActor.tell(HTTP.Stop.INSTANCE, ActorRef.noSender());
                            return complete("Server " + host + " is shutting Down\n");
                        })
                )
        );
    }
}