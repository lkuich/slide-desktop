package slide

import enums.{ConnectionMode, PositioningMode}

object Settings {
    private val _scale: Array[Short] = new Array[Short](1)
    var sensitivity: Double = 1.0
    var connectionMode: ConnectionMode = ConnectionMode.WIFI
    var positioningMode: PositioningMode = PositioningMode.RELATIVE

    var scrollAxis: Integer = -1

    def scale_=(scaleX: Short, scaleY: Short): Unit = {
        _scale(0) = scaleX
        _scale(1) = scaleY
    }
}
