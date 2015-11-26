package connections

import enums.ConnectionMode
import scala.collection.mutable.HashSet

object ConnectionManager {
    private val connections: HashSet[ConnectionMode] = new HashSet[ConnectionMode]()

    def hasConnection(connection: ConnectionMode): Boolean = connections.contains(connection)

    def multipleConnections: Boolean = connections.size > 1

    def addConnection(newConnectionStatus: ConnectionMode): Unit = connections.add(newConnectionStatus)

    def removeConnection(connectionStatus: ConnectionMode): Unit = connections.remove(connectionStatus)
}
