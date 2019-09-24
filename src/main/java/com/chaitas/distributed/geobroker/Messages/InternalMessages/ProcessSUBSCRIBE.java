package com.chaitas.distributed.geobroker.Messages.InternalMessages;

import akka.actor.ActorRef;
import com.chaitas.distributed.geobroker.Storage.Subscription;
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ExternalMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ProcessSUBSCRIBE {

    public ExternalMessage message;
    public ActorRef wsClientActor;
    public Subscription subscription;

    public ProcessSUBSCRIBE(@NotNull @JsonProperty("message") ExternalMessage message,
                            @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor){
        this.message = message;
        this.wsClientActor = wsClientActor;
    }

}
