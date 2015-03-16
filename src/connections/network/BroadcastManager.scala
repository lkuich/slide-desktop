package connections.network

import java.io.IOException
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
            case e: IOException => null
        }
    }

    def networkIsAvailable: Boolean = true
}
