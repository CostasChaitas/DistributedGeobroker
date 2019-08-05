package com.chaitas.masterthesis.commons.payloads;

import com.chaitas.masterthesis.commons.message.Topic;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class UNSUBSCRIBEPayload extends AbstractPayload {

    public Topic topic;

    public UNSUBSCRIBEPayload(@NotNull @JsonProperty("topic") Topic topic){
        this.topic = topic;
    }
}