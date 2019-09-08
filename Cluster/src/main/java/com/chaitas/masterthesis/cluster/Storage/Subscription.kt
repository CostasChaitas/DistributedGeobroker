package com.chaitas.masterthesis.cluster.Storage

import akka.actor.ActorRef
import com.chaitas.masterthesis.commons.message.Topic
import com.chaitas.masterthesis.commons.spatial.Geofence
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
