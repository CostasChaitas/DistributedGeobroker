package com.chaitas.masterthesis.Messages.InternalMessages;

import akka.actor.ActorRef;
import com.chaitas.masterthesis.Messages.ExternalMessages.ExternalMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ProcessPINGREQ {

    public ExternalMessage message;
    public ActorRef wsClientActor;

    public ProcessPINGREQ(@NotNull @JsonProperty("message") ExternalMessage message,
                          @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor){
        this.message = message;
        this.wsClientActor = wsClientActor;
    }
}
