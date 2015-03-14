package connections;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.EOFException;
import java.io.IOException;
import davinci.Const;
import davinci.UnknownCommandException;
import enums.DeviceMessageType;
import enums.PositioningMode;
import davinci.Master;
import gui.ErrorMessage;
import gui.Home;

public abstract class BaseDeviceConnection
    extends Master
    implements DeviceConnection
{
    private Robot rb;
    private int cursorX = 0;
    private int cursorY = 0;
    private boolean running = true;
    private boolean shift = false;

    private boolean fingerDown = false;

    @Override
    public boolean start()
    {
        try
        {
            rb = new Robot();
        } catch (AWTException e1)
        {
            e1.printStackTrace();
        }

        boolean firstRun = true;
        while (running)
        {
            if (firstRun)
            {
                // TODO: Send connection request and password if present

                // TODO: Scale upon first connect
                //getSettings().setScale(new int[1]);

                // TODO: Send screen resolution
            }

            try
            {
                this.handleMessage(this.getNextMessage());
            } catch (EOFException e)
            {
                e.printStackTrace();
                this.close();
                return false;
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
                this.close();
                return false;
            } catch (IOException e)
            {
                e.printStackTrace();
                this.close();
                return false;
            } catch (UnknownCommandException e)
            {
                e.printStackTrace();
            }
            firstRun = false;
        }

        return false;
    }

    protected void handleMessage(short[] message)
        throws UnknownCommandException
    {
        /**
         * x coordinate or command
         */
        short m1 = message[Const.X()];

        /**
         * y coordinate
         */
        short m2 = message[Const.Y()];

        switch (DeviceMessageType.fromId(m1))
        {
            case FINGER_DOWN:
                this.onFingerDown();
                break;

            case FINGER_TAP:
                this.onFingerTap();
                break;

            case DOUBLE_DOWN:
                this.onDoubleDown();
                break;

            case DOUBLE_DOWN_PEN:
                this.onDoubleDownPen();
                break;

            case DOUBLE_UP:
                this.onDoubleUp();
                break;

            case LONG_HOLD:
                this.onLongHold();
                break;

            case DOUBLE_TAP:
                this.onDoubleTap();
                break;

            case ZOOM_IN:
                this.onZoomIn();
                break;

            case ZOOM_OUT:
                this.onZoomOut();
                break;

            case SCROLL_UP:
                this.onScrollUp();
                break;

            case SCROLL_DOWN:
                this.onScrollDown();
                break;

            case SCROLL_LEFT:
                this.onScrollLeft();
                break;

            case SCROLL_RIGHT:
                this.onScrollRight();
                break;

            case KEYBOARD:
                this.onKeyboard(message[1]);
                break;

            case CUT:
                this.onCut();
                break;

            case COPY:
                this.onCopy();
                break;

            case PASTE:
                this.onPaste();
                break;

            case RELATIVE:
                this.onRelative(message[1], message[2]);
                break;

            case ABSOLUTE:
                this.onAbsolute(message[1], message[2], message[3], message[4]);
                break;

            case MOVE_CURSOR:
                this.onMoveCursor(m1, m2);
                break;

            case CLOSE:
                this.onClose();
                break;
        }
    }

    public static int[] calculateFactor(
        int deviceX,
        int deviceY)
    {
        // TODO: Rewrite the damn scaling
        // Example:
        // Monitor is 1900 x 1600

        // Phone is 720 x 1280

        // Take 1900 / 720 = result X

        // Take 1600 / 1280 = result Y

        int[] output = new int[1];
        /*double dbX;
        double dbY;
        if (Global.screenX > deviceX)
        {
            // Monitor resolution is higher than device (more likely)
            dbX = Global.screenX / deviceX;
        } else if (deviceX < Global.screenX)
        {
            // Device resolution is higher than Monitor
            dbX = deviceX / Global.screenX;
        } else
        {
            // equal
            dbX = deviceX / Global.screenX;
        }

        if (Global.screenY > deviceY)
        {
            // Monitor resolution is higher than device (more likely)
            dbY = Global.screenY; // deviceY;
        } else if (deviceY < Global.screenY)
        {
            // Device resolution is higher than Monitor
            dbY = deviceY / Global.screenY;
        } else
        {
            // equal
            dbY = deviceY / Global.screenY;
        }

        // round the factor
        output[0] = (int) Math.round(dbX * 10) / 10;
        output[1] = (int) Math.round(dbY * 10) / 10;*/
        return output;
    }

    protected void onMoveCursor(
        short m1,
        short m2)
    {
        /*if (getSettings().getPositioningMode() == PositioningMode.ABSOLUTE)
        {
            rb.mouseMove((int) m1 * getSettings().getScale()[Const.X], (int) m2 * getSettings().getScale()[Const.Y]);
            //rb.mouseMove((int) (m1 * getSettings().getSensitivity()), (int) (m2 * getSettings().getSensitivity()));
            // TODO: different resolutions
        } else // Relative mode
        {
            double movX = cursorX + m1 * getSettings().getSensitivity();
            double movY = cursorY + m2 * getSettings().getSensitivity();

            //TODO: Find a way to prevent mouse from going out of screen bounds
            rb.mouseMove((int) movX, (int) movY);
        }*/

        if (getSettings().getPositioningMode() == PositioningMode.RELATIVE)
        {
            double movX = cursorX + m1 * getSettings().getSensitivity();
            double movY = cursorY + m2 * getSettings().getSensitivity();

            //TODO: Find a way to prevent mouse from going out of screen bounds
            rb.mouseMove((int) movX, (int) movY);
        }
    }

    protected void onClose()
    {
        running = false;
    }

    protected void onAbsolute(
        short x,
        short y,
        short sensitivity,
        short version)
    {
        if (isUptoDate(version))
        {
            getSettings().setPositioningMode(PositioningMode.ABSOLUTE);
            getSettings().setSensitivity((double) (sensitivity / 10));
            getSettings().setScale(x, y);
        }
    }

    protected void onRelative(short sensitivity, short version)
    {
        if (isUptoDate(version))
        {
            getSettings().setPositioningMode(PositioningMode.RELATIVE);
            getSettings().setSensitivity((double) (sensitivity / 10));
        }
    }

    protected boolean isUptoDate(final short version)
    {
        if (Const.MIN_VERSION() > version)
        {
            close();
            final ErrorMessage err = new ErrorMessage(Home.getInstance().getFrame(), "Error", "The client is out of date. Please upgrade it.");
            err.showDialog();
            return false;
        } else
        {
            return true;
        }
    }

    protected void onPaste()
    {
        rb.keyPress(KeyEvent.VK_CONTROL);
        rb.keyPress(KeyEvent.VK_V);
        rb.keyRelease(KeyEvent.VK_V);
        rb.keyRelease(KeyEvent.VK_CONTROL);
    }

    protected void onCopy()
    {
        rb.keyPress(KeyEvent.VK_CONTROL);
        rb.keyPress(KeyEvent.VK_C);
        rb.keyRelease(KeyEvent.VK_C);
        rb.keyRelease(KeyEvent.VK_CONTROL);
    }

    protected void onCut()
    {
        rb.keyPress(KeyEvent.VK_CONTROL);
        rb.keyPress(KeyEvent.VK_X);
        rb.keyRelease(KeyEvent.VK_X);
        rb.keyRelease(KeyEvent.VK_CONTROL);
    }

    protected void onKeyboard(short m2)
    {
        // TODO: Move these arrays into a utility class.
        final int[] keyCodes =
            {
                7,
                8,
                9,
                10,
                11,
                12,
                13,
                14,
                15,
                16,
                29,
                30,
                31,
                32,
                33,
                34,
                35,
                36,
                37,
                38,
                39,
                40,
                41,
                42,
                43,
                44,
                45,
                46,
                47,
                48,
                49,
                50,
                51,
                52,
                53,
                54,
                55,
                56,
                62,
                66,
                67,
                68,
                69,
                70,
                71,
                72,
                73,
                74,
                75,
                76
            };

        final int[] keys =
            {
                KeyEvent.VK_0,
                KeyEvent.VK_1,
                KeyEvent.VK_2,
                KeyEvent.VK_3,
                KeyEvent.VK_4,
                KeyEvent.VK_5,
                KeyEvent.VK_6,
                KeyEvent.VK_7,
                KeyEvent.VK_8,
                KeyEvent.VK_9,

                KeyEvent.VK_A,
                KeyEvent.VK_B,
                KeyEvent.VK_C,
                KeyEvent.VK_D,
                KeyEvent.VK_E,
                KeyEvent.VK_F,
                KeyEvent.VK_G,
                KeyEvent.VK_H,
                KeyEvent.VK_I,
                KeyEvent.VK_J,
                KeyEvent.VK_K,
                KeyEvent.VK_L,
                KeyEvent.VK_M,
                KeyEvent.VK_N,
                KeyEvent.VK_O,
                KeyEvent.VK_P,
                KeyEvent.VK_Q,
                KeyEvent.VK_R,
                KeyEvent.VK_S,
                KeyEvent.VK_T,
                KeyEvent.VK_U,
                KeyEvent.VK_V,
                KeyEvent.VK_W,
                KeyEvent.VK_X,
                KeyEvent.VK_Y,
                KeyEvent.VK_Z,

                KeyEvent.VK_COMMA,
                KeyEvent.VK_PERIOD,

                KeyEvent.VK_SPACE,
                KeyEvent.VK_ENTER,
                KeyEvent.VK_BACK_SPACE,

                KeyEvent.VK_DEAD_GRAVE,
                KeyEvent.VK_SUBTRACT,
                KeyEvent.VK_EQUALS,
                KeyEvent.VK_OPEN_BRACKET,
                KeyEvent.VK_CLOSE_BRACKET,
                KeyEvent.VK_BACK_SLASH,
                KeyEvent.VK_SEMICOLON,
                KeyEvent.VK_QUOTE,
                KeyEvent.VK_SLASH
            };

        final int keyCode = getIndex(keyCodes, m2);
        if (keyCode != -1)
        {
            if (m2 != 59) // 59 is shift keycode
            {
                if (m2 == 100)
                {
                    // SHIFT
                    rb.keyPress(KeyEvent.VK_SHIFT);
                    shift = true;
                } else
                {
                    rb.keyPress(keys[keyCode]);
                    rb.keyRelease(keys[keyCode]);
                    if (shift)
                    {
                        rb.keyRelease(KeyEvent.VK_SHIFT);
                    }
                }
            }
        }
    }

    public static int getIndex(
        int array[],
        int value)
    {
        int index = 0;
        try
        {
            for (int i = 0; i < array.length; i++)
            {
                if (array[i] == value)
                {
                    index = i;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e)
        {
            index = -1;
        }

        return index;
    }

    protected void onScrollRight()
    {
        rb.keyPress(KeyEvent.VK_SHIFT);
        try
        {
            Thread.sleep(10);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        onScrollDown();
        rb.keyRelease(KeyEvent.VK_SHIFT);
    }

    protected void onScrollLeft()
    {
        rb.keyPress(KeyEvent.VK_SHIFT);
        try
        {
            Thread.sleep(10);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        onScrollUp();
        rb.keyRelease(KeyEvent.VK_SHIFT);
    }

    protected void onScrollDown()
    {
        rb.mouseWheel(getSettings().getScrollAxis());
    }

    protected void onScrollUp()
    {
        rb.mouseWheel(getSettings().getScrollAxis() * -1);
    }

    protected void onZoomOut()
    {
        rb.keyPress(KeyEvent.VK_CONTROL);
        try
        {
            Thread.sleep(10);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        rb.mouseWheel(-1);
        rb.keyRelease(KeyEvent.VK_CONTROL);
    }

    protected void onZoomIn()
    {
        rb.keyPress(KeyEvent.VK_CONTROL);
        try
        {
            Thread.sleep(10);
        } catch (InterruptedException e1)
        {
            e1.printStackTrace();
        }

        rb.mouseWheel(1);
        rb.keyRelease(KeyEvent.VK_CONTROL);
    }

    protected void onDoubleTap()
    {
        onFingerTap();
        onFingerTap();
    }

    protected void onLongHold()
    {
        rb.mousePress(InputEvent.BUTTON3_MASK);
        rb.mouseRelease(InputEvent.BUTTON3_MASK);
    }

    protected void onDoubleUp()
    {
        rb.mouseRelease(InputEvent.BUTTON1_MASK);
        this.fingerDown = false;
    }

    protected void onDoubleDownPen()
    {
        onFingerDown();
        onDoubleDown();
    }

    protected void onDoubleDown()
    {
        rb.mousePress(InputEvent.BUTTON1_MASK);
    }

    protected void onFingerTap()
    {
        onDoubleDown();
        rb.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    protected void onFingerDown()
    {
        final Point p = MouseInfo.getPointerInfo().getLocation();
        this.cursorX = p.x;
        this.cursorY = p.y;
        this.fingerDown = true;
    }

    public boolean getRunning()
    {
        return this.running;
    }
}
