package com.chaitas.masterthesis.commons.payloads;

import com.chaitas.masterthesis.commons.ReasonCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class PUBACKPayload extends AbstractPayload {

    public ReasonCode reasonCode;

    public PUBACKPayload(@NotNull @JsonProperty("reasonCode") ReasonCode reasonCode){
        this.reasonCode = reasonCode;
    }
}