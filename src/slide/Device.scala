package slide

import javax.swing.ImageIcon

class Device(var icon: ImageIcon, val text: Array[String]) {
    var ip: String = text(0)
    var manufacturer: String = text(1)
    var model: String = text(2)
}
