package com.chaitas.masterthesis.cluster.Messages;

import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class sendSUBACK {

    public InternalServerMessage message;

    public sendSUBACK(@NotNull @JsonProperty("message") InternalServerMessage message){
        this.message = message;
    }
}