package connections

import java.awt.{MouseInfo, Point, Robot}
import java.awt.event.{InputEvent, KeyEvent}

import davinci.{Const, Settings, UnknownCommandException}
import enums.{DeviceMessageType, PositioningMode}
import gui.{ErrorMessage, Home}

abstract class BaseDeviceConnection_S extends DeviceConnection {

    private var rb: Robot = null
    private var cursorX: Int = 0
    private var cursorY: Int = 0
    private var running: Boolean = true
    private var shift: Boolean = false
    private var fingerDown: Boolean = false

    def calculateFactor(deviceX: Int, deviceY: Int): Array[Int] = {
        new Array[Int](1)
    }

    def getIndex(array: Array[Int], value: Int): Int = {
        var index: Int = 0
        try {
            var i: Int = 0
            for (i <- 1 to array.length) {
                if (array(i) == value) {
                    index = i
                }
            }
        } catch {
            case e: ArrayIndexOutOfBoundsException => index = -1
        }

        index
    }

    def start(): Boolean = {
        try {
            rb = new Robot
        } catch {
            case Exception => return false
        }

        var firstRun: Boolean = true
        while (running) {
            try {
                this.handleMessage(this.nextMessage())
            }
            catch {
                case e: Exception =>
                    this.close()
                    return false
            }
            firstRun = false
        }

        false
    }

    @throws(classOf[UnknownCommandException])
    protected def handleMessage(message: Array[Short]) {
        val m1: Short = message(Const.X)
        val m2: Short = message(Const.Y)
        DeviceMessageType.fromId(m1) match {
            case FINGER_DOWN =>
                this.onFingerDown()
            case FINGER_TAP =>
                this.onFingerTap()
            case DOUBLE_DOWN =>
                this.onDoubleDown()
            case DOUBLE_DOWN_PEN =>
                this.onDoubleDownPen()
            case DOUBLE_UP =>
                this.onDoubleUp()
            case LONG_HOLD =>
                this.onLongHold()
            case DOUBLE_TAP =>
                this.onDoubleTap()
            case ZOOM_IN =>
                this.onZoomIn()
            case ZOOM_OUT =>
                this.onZoomOut()
            case SCROLL_UP =>
                this.onScrollUp()
            case SCROLL_DOWN =>
                this.onScrollDown()
            case SCROLL_LEFT =>
                this.onScrollLeft()
            case SCROLL_RIGHT =>
                this.onScrollRight()
            case KEYBOARD =>
                this.onKeyboard(message(1))
            case CUT =>
                this.onCut()
            case COPY =>
                this.onCopy()
            case PASTE =>
                this.onPaste()
            case RELATIVE =>
                this.onRelative(message(1), message(2))
            case ABSOLUTE =>
                this.onAbsolute(message(1), message(2), message(3), message(4))
            case MOVE_CURSOR =>
                this.onMoveCursor(m1, m2)
            case CLOSE =>
                this.onClose
        }
    }

    protected def onMoveCursor(m1: Short, m2: Short) {
        if (Settings.positioningMode eq PositioningMode.RELATIVE) {
            val movX: Double = cursorX + m1 * Settings.sensitivity
            val movY: Double = cursorY + m2 * Settings.sensitivity
            rb.mouseMove(movX.toInt, movY.toInt)
        }
    }

    protected def onClose {
        running = false
    }

    protected def onAbsolute(x: Short, y: Short, sensitivity: Short, version: Short) {
        if (isUptoDate(version)) {
            Settings.positioningMode_$eq(PositioningMode.ABSOLUTE)
            Settings.sensitivity_$eq((sensitivity / 10).toDouble)
            Settings.scale_$eq(x, y)
        }
    }

    protected def onRelative(sensitivity: Short, version: Short) {
        if (isUptoDate(version)) {
            Settings.positioningMode_$eq(PositioningMode.RELATIVE)
            Settings.sensitivity_$eq((sensitivity / 10).toDouble)
        }
    }

    protected def isUptoDate(version: Short): Boolean = {
        if (Const.MIN_VERSION > version) {
            this.close()

            val err: ErrorMessage = new ErrorMessage(Home.getInstance.getFrame, "Error", "The client is out of date. Please upgrade it.")
            err.showDialog()

            false
        }
        else {
            true
        }
    }

    protected def onPaste() {
        rb.keyPress(KeyEvent.VK_CONTROL)
        rb.keyPress(KeyEvent.VK_V)
        rb.keyRelease(KeyEvent.VK_V)
        rb.keyRelease(KeyEvent.VK_CONTROL)
    }

    protected def onCopy() {
        rb.keyPress(KeyEvent.VK_CONTROL)
        rb.keyPress(KeyEvent.VK_C)
        rb.keyRelease(KeyEvent.VK_C)
        rb.keyRelease(KeyEvent.VK_CONTROL)
    }

    protected def onCut() {
        rb.keyPress(KeyEvent.VK_CONTROL)
        rb.keyPress(KeyEvent.VK_X)
        rb.keyRelease(KeyEvent.VK_X)
        rb.keyRelease(KeyEvent.VK_CONTROL)
    }

    protected def onKeyboard(m2: Short) {
        val keyCodes: Array[Int] = Array(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 29, 30, 31, 32, 33,
            34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55,
            56, 62, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76)

        val keys: Array[Int] = Array(KeyEvent.VK_0, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3,
            KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9,
            KeyEvent.VK_A, KeyEvent.VK_B, KeyEvent.VK_C, KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_F,
            KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_I, KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L,
            KeyEvent.VK_M, KeyEvent.VK_N, KeyEvent.VK_O, KeyEvent.VK_P, KeyEvent.VK_Q, KeyEvent.VK_R,
            KeyEvent.VK_S, KeyEvent.VK_T, KeyEvent.VK_U, KeyEvent.VK_V, KeyEvent.VK_W, KeyEvent.VK_X,
            KeyEvent.VK_Y, KeyEvent.VK_Z, KeyEvent.VK_COMMA, KeyEvent.VK_PERIOD, KeyEvent.VK_SPACE,
            KeyEvent.VK_ENTER, KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_SUBTRACT,
            KeyEvent.VK_EQUALS, KeyEvent.VK_OPEN_BRACKET, KeyEvent.VK_CLOSE_BRACKET, KeyEvent.VK_BACK_SLASH,
            KeyEvent.VK_SEMICOLON, KeyEvent.VK_QUOTE, KeyEvent.VK_SLASH)

        val keyCode: Int = BaseDeviceConnection.getIndex(keyCodes, m2)
        if (keyCode != -1) {
            if (m2 != 59) {
                if (m2 == 100) {
                    rb.keyPress(KeyEvent.VK_SHIFT)
                    shift = true
                }
                else {
                    rb.keyPress(keys(keyCode))
                    rb.keyRelease(keys(keyCode))
                    if (shift) {
                        rb.keyRelease(KeyEvent.VK_SHIFT)
                    }
                }
            }
        }
    }

    protected def onScrollRight(): Unit = {
        rb.keyPress(KeyEvent.VK_SHIFT)
        try {
            Thread.sleep(10)
        }
        onScrollDown()
        rb.keyRelease(KeyEvent.VK_SHIFT)
    }

    protected def onScrollLeft(): Unit = {
        rb.keyPress(KeyEvent.VK_SHIFT)
        try {
            Thread.sleep(10)
        }
        onScrollUp()
        rb.keyRelease(KeyEvent.VK_SHIFT)
    }

    protected def onScrollDown(): Unit = rb.mouseWheel(Settings.scrollAxis)

    protected def onScrollUp(): Unit = rb.mouseWheel(Settings.scrollAxis * -1)

    protected def onZoomOut(): Unit = {
        rb.keyPress(KeyEvent.VK_CONTROL)
        try {
            Thread.sleep(10)
        }
        rb.mouseWheel(-1)
        rb.keyRelease(KeyEvent.VK_CONTROL)
    }

    protected def onZoomIn(): Unit = {
        rb.keyPress(KeyEvent.VK_CONTROL)
        try {
            Thread.sleep(10)
        }
        rb.mouseWheel(1)
        rb.keyRelease(KeyEvent.VK_CONTROL)
    }

    protected def onDoubleTap(): Unit = {
        onFingerTap()
        onFingerTap()
    }

    protected def onLongHold(): Unit = {
        rb.mousePress(InputEvent.BUTTON3_MASK)
        rb.mouseRelease(InputEvent.BUTTON3_MASK)
    }

    protected def onDoubleUp(): Unit = {
        rb.mouseRelease(InputEvent.BUTTON1_MASK)
        this.fingerDown = false
    }

    protected def onDoubleDownPen(): Unit = {
        onFingerDown()
        onDoubleDown()
    }

    protected def onDoubleDown(): Unit =
        rb.mousePress(InputEvent.BUTTON1_MASK)

    protected def onFingerTap(): Unit = {
        onDoubleDown()
        rb.mouseRelease(InputEvent.BUTTON1_MASK)
    }

    protected def onFingerDown(): Unit = {
        val p: Point = MouseInfo.getPointerInfo.getLocation
        this.cursorX = p.x
        this.cursorY = p.y
        this.fingerDown = true
    }
}
