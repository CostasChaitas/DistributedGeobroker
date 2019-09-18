package com.chaitas.masterthesis.cluster.Messages;

import com.chaitas.masterthesis.commons.message.ExternalMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class SendACK {

    public ExternalMessage message;

    public SendACK(@NotNull @JsonProperty("message") ExternalMessage message){
        this.message = message;
    }

}