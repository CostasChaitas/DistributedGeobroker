// Code adapted from Geobroker project : https://github.com/MoeweX/geobroker

package com.chaitas.masterthesis.Messages.ExternalMessages.Payloads;

import com.chaitas.masterthesis.Messages.ExternalMessages.ReasonCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class PUBACKPayload extends AbstractPayload {

    public ReasonCode reasonCode;

    public PUBACKPayload(@NotNull @JsonProperty("reasonCode") ReasonCode reasonCode){
        this.reasonCode = reasonCode;
    }
}