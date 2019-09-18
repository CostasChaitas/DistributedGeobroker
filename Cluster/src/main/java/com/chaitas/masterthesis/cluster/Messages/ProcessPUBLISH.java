package com.chaitas.masterthesis.cluster.Messages;

import akka.actor.ActorRef;
<<<<<<< HEAD
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
=======
import com.chaitas.masterthesis.commons.message.ExternalMessage;
>>>>>>> master
import com.chaitas.masterthesis.commons.spatial.Location;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ProcessPUBLISH {
<<<<<<< HEAD

    public InternalServerMessage message;
    public ActorRef wsClientActor;
    public Location wsClientLocation;
    public int tileId;

    public ProcessPUBLISH(@NotNull @JsonProperty("message") InternalServerMessage message,
                          @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor,
                          @NotNull @JsonProperty("wsClientLocation") Location wsClientLocation,
                          @NotNull @JsonProperty("tileId") int tileId){
        this.message = message;
        this.wsClientActor = wsClientActor;
        this.wsClientLocation = wsClientLocation;
        this.tileId = tileId;
    }
}

=======
    
    public ExternalMessage message;
    public ActorRef wsClientActor;
    public Location clientLocation;

    public ProcessPUBLISH(@NotNull @JsonProperty("message") ExternalMessage message,
                          @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor,
                          @JsonProperty("clientLocation") Location clientLocation){
        this.message = message;
        this.wsClientActor = wsClientActor;
        this.clientLocation = clientLocation;
    }
}
>>>>>>> master
