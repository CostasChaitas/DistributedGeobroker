// Code adapted from Geobroker project : https://github.com/MoeweX/geobroker

package com.chaitas.masterthesis.Messages.ExternalMessages.Payloads;

import com.chaitas.masterthesis.Messages.ExternalMessages.Spatial.Location;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class PINGREQPayload extends AbstractPayload {

    public Location location;

    public PINGREQPayload(@NotNull @JsonProperty("location") Location location){
        this.location = location;
    }
}