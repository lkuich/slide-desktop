package gui.img;

import java.net.URL;
import javax.swing.ImageIcon;
import enums.ConnectionMode;

public class ImageIcons
{
    private static ImageIcons instance = null;

    private ImageIcon usbIcon;
    private ImageIcon wifiIcon;

    private ImageIcons()
    {
        usbIcon = new ImageIcon(getImagePath(ConnectionMode.USB));
        wifiIcon = new ImageIcon(getImagePath(ConnectionMode.WIFI));
    }

    public static synchronized ImageIcons getInstance()
    {
        if (instance == null)
        {
            instance = new ImageIcons();
        }
        return instance;
    }

    public ImageIcon getUsbIcon()
    {
        return this.usbIcon;
    }

    public ImageIcon getWifiIcon()
    {
        return this.wifiIcon;
    }

    private URL getImagePath(final ConnectionMode mode)
    {
        if (mode == ConnectionMode.USB)
        {
            return this.getClass().getResource("usb.png");
        } else if (mode == ConnectionMode.WIFI)
        {
            return this.getClass().getResource("wifi.png");
        } else {
            return null;
        }
    }
}
