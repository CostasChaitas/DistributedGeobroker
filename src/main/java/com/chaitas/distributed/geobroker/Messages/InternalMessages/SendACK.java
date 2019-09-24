package com.chaitas.distributed.geobroker.Messages.InternalMessages;

import com.chaitas.distributed.geobroker.Messages.ExternalMessages.ExternalMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class SendACK {

    public ExternalMessage message;

    public SendACK(@NotNull @JsonProperty("message") ExternalMessage message){
        this.message = message;
    }

}