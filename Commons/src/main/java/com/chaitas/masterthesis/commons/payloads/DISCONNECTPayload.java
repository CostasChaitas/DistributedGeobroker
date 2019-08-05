package com.chaitas.masterthesis.commons.payloads;

import com.chaitas.masterthesis.commons.ReasonCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class DISCONNECTPayload extends AbstractPayload {

    public ReasonCode reasonCode;

    public DISCONNECTPayload(@NotNull @JsonProperty("reasonCode") ReasonCode reasonCode){
        this.reasonCode = reasonCode;
    }
}