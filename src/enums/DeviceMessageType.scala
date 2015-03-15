package enums

import davinci.Const

object DeviceMessageType extends Enumeration {

    val t = new Tuple21(
        Const.DEVICE_MESSAGE_ID_BASE + 2,
        Const.DEVICE_MESSAGE_ID_BASE + 3,
        Const.DEVICE_MESSAGE_ID_BASE + 4,
        Const.DEVICE_MESSAGE_ID_BASE + 5,
        Const.DEVICE_MESSAGE_ID_BASE + 6,
        Const.DEVICE_MESSAGE_ID_BASE + 7,
        Const.DEVICE_MESSAGE_ID_BASE + 8,
        Const.DEVICE_MESSAGE_ID_BASE + 9,
        Const.DEVICE_MESSAGE_ID_BASE + 10,
        Const.DEVICE_MESSAGE_ID_BASE + 11,
        Const.DEVICE_MESSAGE_ID_BASE + 12,
        Const.DEVICE_MESSAGE_ID_BASE + 13,
        Const.DEVICE_MESSAGE_ID_BASE + 14,
        Const.DEVICE_MESSAGE_ID_BASE + 15,
        Const.DEVICE_MESSAGE_ID_BASE + 16,
        Const.DEVICE_MESSAGE_ID_BASE + 17,
        Const.DEVICE_MESSAGE_ID_BASE + 18,
        Const.DEVICE_MESSAGE_ID_BASE + 19,
        Const.DEVICE_MESSAGE_ID_BASE + 20,
        Const.DEVICE_MESSAGE_ID_BASE + 21,
        Const.DEVICE_MESSAGE_ID_BASE + 22
    )

    def CLOSE: Integer = t._1
    def FINGER_DOWN: Integer = t._2
    val FINGER_TAP: = null
    
    val DOUBLE_DOWN: = null
    
    val DOUBLE_UP: = null
    
    val LONG_HOLD: = null
    
    val DOUBLE_TAP: = null
    
    val KEYBOARD: = null
    
    val ABSOLUTE: = null
    
    val RELATIVE: = null
    
    val ZOOM_IN: = null
    
    val ZOOM_OUT: = null
    
    val SCROLL_DOWN: = null
    
    val SCROLL_UP: = null
    
    val SCROLL_LEFT: = null
    
    val SCROLL_RIGHT: = null
    
    val CUT: = null
    
    val COPY: = null
    
    val PASTE: = null
    
    val SCREEN_SHOT: = null
    
    val DOUBLE_DOWN_PEN: = null
    
    val MOVE_CURSOR: = null;
}
