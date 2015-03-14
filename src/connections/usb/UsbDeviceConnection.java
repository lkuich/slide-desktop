package connections.usb;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.imageio.ImageIO;
import connections.BaseDeviceConnection;
import davinci.Const;
import enums.DeviceMessageType;
import davinci.UnknownCommandException;

public class UsbDeviceConnection
    extends BaseDeviceConnection
{
    private Socket connection = null;
    private ObjectInputStream input = null;

    @Override
    public boolean connect()
        throws IOException
    {
        try
        {
            final InetSocketAddress inetAddress =
                new InetSocketAddress("localhost", Const.USB_PORT());

            this.connection = new Socket();
            getConnection().connect(inetAddress, 2000);
            getConnection().setTcpNoDelay(true);
            getConnection().setKeepAlive(true);

            /*this.input =
                new BufferedReader(new InputStreamReader(getConnection().getInputStream()));*/
            this.input = new ObjectInputStream(getConnection().getInputStream());
        } catch (final EOFException e)
        {
            e.printStackTrace();
        }

        // update UI, but not from this class/package, only interact with the UI from the gui
        // package

        return this.start();
    }

    @Override
    public short[] getNextMessage()
        throws IOException, ClassNotFoundException
    {
        return (short[]) getInput().readObject();
    }

    @Override
    protected void handleMessage(short[] message)
    {
        if (DeviceMessageType.fromId(message[0]) == DeviceMessageType.SCREEN_SHOT)
        {
            this.onScreenShot();
        } else
        {
            try
            {
                super.handleMessage(message); // TODO: Calling handleMessage twice??
            } catch (UnknownCommandException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void onScreenShot()
    {
        try
        {
            BufferedImage image =
                new Robot().createScreenCapture(
                    new Rectangle(
                        Toolkit
                            .getDefaultToolkit()
                            .getScreenSize()));
            try
            {
                ImageIO.write(image, "PNG", getConnection().getOutputStream());
            } catch (IOException e)
            {
                // TODO: Handle this instead of wrapping and re-throwing.
                throw new RuntimeException("Failed to write screen shot.", e);
            }
        } catch (AWTException e)
        {
            e.printStackTrace();
        }
    }

    public void close()
    {
        try
        {
            onClose();

            if (getInput() != null)
            {
                getInput().close();
            }

            if (getConnection() != null)
            {
                getConnection().close();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Getters
    public Socket getConnection()
    {
        return this.connection;
    }

    public ObjectInputStream getInput()
    {
        return this.input;
    }
}