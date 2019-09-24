// Code adapted from Geobroker project : https://github.com/MoeweX/geobroker

package com.chaitas.distributed.geobroker.Storage

import akka.actor.ActorRef
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.Topic
import com.chaitas.distributed.geobroker.Messages.ExternalMessages.Spatial.Geofence
import org.apache.commons.lang3.tuple.ImmutablePair


class Subscription(val subscriptionId: ImmutablePair<String, String>, val topic: Topic, var geofence: Geofence, var wsClientActor: ActorRef) {

    override fun toString(): String {
        return "Subscription{" +
                "id=" + subscriptionId.toString() +
                "topic=" + topic +
                ", geofence=" + geofence +
                '}'
    }

}
