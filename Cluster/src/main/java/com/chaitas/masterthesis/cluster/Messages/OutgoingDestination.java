package com.chaitas.masterthesis.cluster.Messages;

import akka.actor.ActorRef;

public class OutgoingDestination {
    public final ActorRef destination;
    public OutgoingDestination(ActorRef destination) {
        this.destination = destination;
    }
}