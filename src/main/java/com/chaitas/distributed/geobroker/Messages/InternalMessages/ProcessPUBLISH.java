package com.chaitas.distributed.geobroker.Messages.InternalMessages;

import akka.actor.ActorRef;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ExternalMessage;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.Spatial.Location;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ProcessPUBLISH {

    public ExternalMessage message;
    public ActorRef wsClientActor;
    public Location clientLocation;

    public ProcessPUBLISH(@NotNull @JsonProperty("message") ExternalMessage message,
                          @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor,
                          @JsonProperty("clientLocation") Location clientLocation){
        this.message = message;
        this.wsClientActor = wsClientActor;
        this.clientLocation = clientLocation;
    }
}
