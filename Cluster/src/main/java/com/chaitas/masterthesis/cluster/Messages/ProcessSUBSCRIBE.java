package com.chaitas.masterthesis.cluster.Messages;

import akka.actor.ActorRef;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ProcessSUBSCRIBE {

    public InternalServerMessage message;
    public ActorRef wsClientActor;
    public int tileId;

    public ProcessSUBSCRIBE(@NotNull @JsonProperty("message") InternalServerMessage message,
                            @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor,
                            @NotNull @JsonProperty("tileId") int tileId){
        this.message = message;
        this.wsClientActor = wsClientActor;
        this.tileId = tileId;
    }
}