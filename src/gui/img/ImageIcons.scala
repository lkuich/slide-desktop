package gui.img

import java.net.URL
import javax.swing.ImageIcon
import enums.ConnectionMode

object ImageIcons {
    var usbIcon: ImageIcon = new ImageIcon(getImagePath(ConnectionMode.USB))
    var wifiIcon: ImageIcon = new ImageIcon(getImagePath(ConnectionMode.WIFI))

    private def getImagePath(mode: ConnectionMode): URL = {
        if (mode == ConnectionMode.USB) {
            this.getClass.getResource("usb.png")
        }
        else if (mode == ConnectionMode.WIFI) {
            this.getClass.getResource("wifi.png")
        } else {
            new URL("")
        }
    }
}