package com.chaitas.masterthesis.cluster.Messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class GeoMatching {

    public ProcessPUBLISH processPUBLISH;
    public ProcessSUBSCRIBE processSUBSCRIBE;

    public GeoMatching(
            @NotNull @JsonProperty("processPUBLISH") ProcessPUBLISH processPUBLISH,
            @NotNull @JsonProperty("processSUBSCRIBE") ProcessSUBSCRIBE processSUBSCRIBE
    ){
        this.processPUBLISH = processPUBLISH;
        this.processSUBSCRIBE = processSUBSCRIBE;
    }

}
