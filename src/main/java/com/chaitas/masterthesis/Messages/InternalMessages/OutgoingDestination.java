package com.chaitas.masterthesis.Messages.InternalMessages;

import akka.actor.ActorRef;

public class OutgoingDestination {

    public final ActorRef destination;

    public OutgoingDestination(ActorRef destination) {
        this.destination = destination;
    }
}