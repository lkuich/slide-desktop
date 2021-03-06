package connections

import java.io.IOException

trait DeviceConnection {
    @throws[IOException]
    def connect(): Boolean

    def close(): Unit

    @throws[IOException]
    @throws[ClassNotFoundException]
    def nextMessage(): Array[Short]

    @throws[IOException]
    def start(): Boolean
}
