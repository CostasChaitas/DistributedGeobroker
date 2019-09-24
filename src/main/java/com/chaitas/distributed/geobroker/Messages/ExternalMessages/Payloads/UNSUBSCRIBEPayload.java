// Code adapted from Geobroker project : https://github.com/MoeweX/geobroker

package com.chaitas.distributed.geobroker.Messages.ExternalMessages.Payloads;

import com.chaitas.distributed.geobroker.Messages.ExternalMessages.Topic;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class UNSUBSCRIBEPayload extends AbstractPayload {

    public Topic topic;

    public UNSUBSCRIBEPayload(@NotNull @JsonProperty("topic") Topic topic){
        this.topic = topic;
    }
}