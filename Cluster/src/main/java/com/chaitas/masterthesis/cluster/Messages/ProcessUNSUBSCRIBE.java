package com.chaitas.masterthesis.cluster.Messages;

import akka.actor.ActorRef;
import com.chaitas.masterthesis.commons.message.ExternalMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ProcessUNSUBSCRIBE {
    public ExternalMessage message;
    public ActorRef wsClientActor;

    public ProcessUNSUBSCRIBE(@NotNull @JsonProperty("message") ExternalMessage message,
                            @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor){
        this.message = message;
        this.wsClientActor = wsClientActor;
    }
}
