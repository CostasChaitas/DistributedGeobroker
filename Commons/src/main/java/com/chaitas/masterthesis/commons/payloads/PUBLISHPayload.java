package com.chaitas.masterthesis.commons.payloads;

import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.spatial.Geofence;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class PUBLISHPayload extends AbstractPayload {

    public Topic topic;
    public Geofence geofence;
    public String content;

    public PUBLISHPayload(@NotNull @JsonProperty("topic") Topic topic,
                          @NotNull @JsonProperty("geofence") Geofence geofence,
                          @NotNull @JsonProperty("content") String content){
        this.topic = topic;
        this.geofence = geofence;
        this.content = content;
    }

    public Topic getTopic() {
        return topic;
    }

    public Geofence getGeofence() {
        return geofence;
    }

    public String getContent() {
        return content;
    }
}
