package com.chaitas.masterthesis.commons.payloads;

import com.chaitas.masterthesis.commons.spatial.Location;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class PINGREQPayload extends AbstractPayload {

    public Location location;

    public PINGREQPayload(@NotNull @JsonProperty("topic") Location location){
        this.location = location;
    }
}