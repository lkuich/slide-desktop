package connections

import java.util

import enums.ConnectionMode

object ConnectionManager {
    val connections: util.HashSet[ConnectionMode] = new util.HashSet[ConnectionMode]()

    def hasConnection(connection: ConnectionMode): Boolean = {
        connections.contains(connection)
    }

    def multipleConnections: Boolean = connections.size > 1

    def addConnection(newConnectionStatus: ConnectionMode): Unit = connections.add(newConnectionStatus)

    def removeConnection(connectionStatus: ConnectionMode): Unit = {
        if (connections.contains(connectionStatus)) {
            connections.remove(connectionStatus)
        }
    }
}
