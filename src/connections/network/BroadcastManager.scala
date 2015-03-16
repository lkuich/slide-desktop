package connections.network

import java.net.{DatagramPacket, DatagramSocket}

import davinci.Device
import gui.img.ImageIcons

class BroadcastManager extends DatagramSocket(5000) {

    this.setSoTimeout(1000)

    def search: Device = {
        val receiveData: Array[Byte] = new Array[Byte](1024)

        try {
            val receivePacket: DatagramPacket = new DatagramPacket(receiveData, receiveData.length)

            this.receive(receivePacket)

            val message: String = new String(receivePacket.getData).trim
            new Device(ImageIcons.wifiIcon, message.split(","))
        } catch {
            case e: Exception => null
        }
    }

    // TODO: Implement this later
    def networkIsAvailable: Boolean = true
}
