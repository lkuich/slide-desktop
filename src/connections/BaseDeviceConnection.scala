package connections

import java.awt.{MouseInfo, Point, Robot}
import java.awt.event.{InputEvent, KeyEvent}
import java.net.Socket

import davinci.{Const, Settings, UnknownCommandException}
import enums.{DeviceMessageType, PositioningMode}
import gui.{ErrorMessage, Frame}

abstract class BaseDeviceConnection extends DeviceConnection {

    private val rb: Robot = new Robot
    private var cursorX: Int = 0
    private var cursorY: Int = 0
    private var running: Boolean = true
    private var shift: Boolean = false
    private var fingerDown: Boolean = false

    private val _socket: Socket = new Socket
    def socket: Socket = _socket

    /* TODO: */
    def calculateFactor(deviceX: Int, deviceY: Int): Array[Int] = {
        new Array[Int](1)
    }

    def getIndex(array: Array[Int], value: Int): Int = {
        var index: Integer = 0
        try {
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

    override def start(): Boolean = {
        var firstRun: Boolean = true
        while (running) {
            try {
                this.handleMessage(this.nextMessage())
            } catch {
                case e: Exception =>
                    this.close()
                    e.printStackTrace()
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
            case DeviceMessageType.FINGER_DOWN => this.onFingerDown()
            case DeviceMessageType.FINGER_TAP => this.onFingerTap()
            case DeviceMessageType.DOUBLE_DOWN => this.onDoubleDown()
            case DeviceMessageType.DOUBLE_DOWN_PEN => this.onDoubleDownPen()
            case DeviceMessageType.DOUBLE_UP => this.onDoubleUp()
            case DeviceMessageType.LONG_HOLD => this.onLongHold()
            case DeviceMessageType.DOUBLE_TAP => this.onDoubleTap()
            case DeviceMessageType.ZOOM_IN => this.onZoomIn()
            case DeviceMessageType.ZOOM_OUT => this.onZoomOut()
            case DeviceMessageType.SCROLL_UP => this.onScrollUp()
            case DeviceMessageType.SCROLL_DOWN => this.onScrollDown()
            case DeviceMessageType.SCROLL_LEFT => this.onScrollLeft()
            case DeviceMessageType.SCROLL_RIGHT => this.onScrollRight()
            case DeviceMessageType.KEYBOARD => this.onKeyboard(message(1))
            case DeviceMessageType.CUT => this.onCut()
            case DeviceMessageType.COPY => this.onCopy()
            case DeviceMessageType.PASTE => this.onPaste()
            case DeviceMessageType.RELATIVE => this.onRelative(message(1), message(2))
            case DeviceMessageType.ABSOLUTE => this.onAbsolute(message(1), message(2), message(3), message(4))
            case DeviceMessageType.MOVE_CURSOR => this.onMoveCursor(m1, m2)
            case DeviceMessageType.CLOSE => this.stopRunning()
        }
    }

    protected def stopRunning(): Unit = running = false

    protected def onMoveCursor(m1: Short, m2: Short) {
        Settings.positioningMode match {
            case PositioningMode.RELATIVE =>
                val movX: Double = cursorX + m1 * Settings.sensitivity
                val movY: Double = cursorY + m2 * Settings.sensitivity
                rb.mouseMove(movX.toInt, movY.toInt)
        }
    }

    protected def onAbsolute(x: Short, y: Short, sensitivity: Short, version: Short) {
        if (isUptoDate(version)) {
            Settings.positioningMode = PositioningMode.ABSOLUTE
            Settings.sensitivity = (sensitivity / 10).toDouble
            Settings.scale_=(x, y)
        }
    }

    protected def onRelative(sensitivity: Short, version: Short) {
        if (isUptoDate(version)) {
            Settings.positioningMode = PositioningMode.RELATIVE
            Settings.sensitivity = (sensitivity / 10).toDouble
        }
    }

    protected def isUptoDate(version: Short): Boolean = {
        if (Const.MIN_VERSION > version) {
            this.close()

            val err: ErrorMessage = new ErrorMessage(Frame, "Error", "The client is out of date. Please upgrade it.")
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

        val keyCode: Integer = getIndex(keyCodes, m2)
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

        Thread.sleep(10)
        onScrollDown()
        rb.keyRelease(KeyEvent.VK_SHIFT)
    }

    protected def onScrollLeft(): Unit = {
        rb.keyPress(KeyEvent.VK_SHIFT)

        Thread.sleep(10)
        onScrollUp()
        rb.keyRelease(KeyEvent.VK_SHIFT)
    }

    protected def onScrollDown(): Unit = rb.mouseWheel(Settings.scrollAxis)

    protected def onScrollUp(): Unit = rb.mouseWheel(Settings.scrollAxis * -1)

    protected def onZoomOut(): Unit = {
        rb.keyPress(KeyEvent.VK_CONTROL)

        Thread.sleep(10)
        rb.mouseWheel(-1)
        rb.keyRelease(KeyEvent.VK_CONTROL)
    }

    protected def onZoomIn(): Unit = {
        rb.keyPress(KeyEvent.VK_CONTROL)

        Thread.sleep(10)
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
