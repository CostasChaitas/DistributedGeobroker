package com.chaitas.masterthesis.cluster.Messages;

import akka.actor.ActorRef;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.chaitas.masterthesis.commons.spatial.Location;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ProcessPUBLISH {

    public InternalServerMessage message;
    public ActorRef wsClientActor;
    public Location wsClientLocation;
    public int tileId;

    public ProcessPUBLISH(@NotNull @JsonProperty("message") InternalServerMessage message,
                          @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor,
                          @NotNull @JsonProperty("wsClientLocation") Location wsClientLocation,
                          @NotNull @JsonProperty("tileId") int tileId){
        this.message = message;
        this.wsClientActor = wsClientActor;
        this.wsClientLocation = wsClientLocation;
        this.tileId = tileId;
    }
}

