// Code adapted from Geobroker project : https://github.com/MoeweX/geobroker

package com.chaitas.masterthesis.commons.payloads;

import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.payloads.AbstractPayload;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class UNSUBACKPayload extends AbstractPayload {

    public ReasonCode reasonCode;

    public UNSUBACKPayload(@NotNull @JsonProperty("reasonCode") ReasonCode reasonCode){
        this.reasonCode = reasonCode;
    }
}