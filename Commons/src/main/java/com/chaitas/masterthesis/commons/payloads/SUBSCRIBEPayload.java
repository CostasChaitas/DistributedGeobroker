// Code adapted from Geobroker project : https://github.com/MoeweX/geobroker

package com.chaitas.masterthesis.commons.payloads;

import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.spatial.Geofence;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class SUBSCRIBEPayload extends AbstractPayload {

    public Topic topic;
    public Geofence geofence;

    public SUBSCRIBEPayload(@NotNull @JsonProperty("topic") Topic topic,
                          @NotNull @JsonProperty("geofence") Geofence geofence){
        this.topic = topic;
        this.geofence = geofence;
    }

    public Topic getTopic() {
        return topic;
    }

    public Geofence getGeofence() {
        return geofence;
    }
}
