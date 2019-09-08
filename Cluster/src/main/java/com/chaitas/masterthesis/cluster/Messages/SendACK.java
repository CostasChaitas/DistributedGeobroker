package com.chaitas.masterthesis.cluster.Messages;

import com.chaitas.masterthesis.commons.message.ExternalMessage;
import com.chaitas.masterthesis.commons.spatial.Location;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class SendACK {

    public ExternalMessage message;
    public Location clientLocation;

    public SendACK(@NotNull @JsonProperty("message") ExternalMessage message){
        this.message = message;
    }

    public SendACK(@NotNull @JsonProperty("message") ExternalMessage message,
                   @NotNull @JsonProperty("clientLocation") Location clientLocation){
        this.message = message;
        this.clientLocation = clientLocation;
    }
}