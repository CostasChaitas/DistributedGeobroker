package com.chaitas.masterthesis.cluster.Messages;

import akka.actor.ActorRef;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class processPUBLISH {

    public InternalServerMessage message;
    public ActorRef clientActor;
    public int tileId;

    public processPUBLISH(@NotNull @JsonProperty("message") InternalServerMessage message,
                          @NotNull @JsonProperty("clientActor") ActorRef clientActor){
        this.message = message;
        this.clientActor = clientActor;
    }

    public processPUBLISH(@NotNull @JsonProperty("message") InternalServerMessage message,
                          @NotNull @JsonProperty("clientActor") ActorRef clientActor,
                          @NotNull @JsonProperty("tileId") int tileId){
        this.message = message;
        this.clientActor = clientActor;
        this.tileId = tileId;
    }
}

