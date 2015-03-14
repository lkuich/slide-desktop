package connections.usb;

import java.io.IOException;
import davinci.Device;
import davinci.Master;
import enums.ConnectionMode;
import gui.Home;
import gui.img.ImageIcons;

public class UsbDeviceManager
{
    private UsbDeviceConnection udc;

    private Device usbDevice;
    private boolean backgroundScannerRunning = true;

    public void connect()
        throws IOException
    {
        udc = new UsbDeviceConnection();
        udc.connect();
    }

    public void startBackgroundScanner()
    {
        final Thread t = new Thread(
            new Runnable()
            {
                @Override
                public void run()
                {
                    usbDevice = new Device(
                        ImageIcons.getInstance().getUsbIcon(), new String[] {"USB", "USB", "USB"});

                    int dcCount = 0; // Seconds no UDP signal is detected
                    while (backgroundScannerRunning)
                    {
                        try
                        {
                            Thread.sleep(1000);

                            if (Adb.usbAvailable()) // Will not show a window
                            {
                                dcCount = 0; // Reset disconnect

                                if (!Master.hasConnection(ConnectionMode.USB))
                                {
                                    Master.addConnection(ConnectionMode.USB);
                                    adjustGui(ConnectionMode.USB);
                                }
                            } else
                            {
                                dcCount++;
                                if (dcCount >= 2) // 2 second timeout
                                {
                                    // The USB Device has been disconnected
                                    if (usbDevice != null)
                                    {
                                        if (Master.hasConnection(ConnectionMode.USB))
                                        {
                                            Master.removeConnection(ConnectionMode.USB);
                                            if (Master.hasConnection(ConnectionMode.WIFI))
                                            {
                                                adjustGui(ConnectionMode.WIFI);
                                            } else
                                            {
                                                adjustGui(false);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });

        if (!Adb.isAdbAvailable()) {
            // ADB is unavailable
            Home.getInstance().showErrorPrompt("Error", "ADB not found.");
        } else {
            // Forward ADB
            try
            {
                Adb.startAdb();
            } catch (final IOException e)
            {
                e.printStackTrace();
            }
            t.start();
        }
    }

    public void stopBackgroundScanner()
    {
        backgroundScannerRunning = false;
    }


    // Getters
    public Device getDevice()
    {
        return this.usbDevice;
    }

    private void adjustGui(final ConnectionMode connectionMode)
    {
        Home.getInstance().getDeviceField().show(true);
        if (connectionMode == ConnectionMode.USB)
        {
            getDevice().icon_$eq(ImageIcons.getInstance().getUsbIcon());
            Home.getInstance().getDeviceField().setUi(getDevice());
        } else {
            getDevice().icon_$eq(ImageIcons.getInstance().getWifiIcon());
            Home.getInstance().getDeviceField().setUi(getDevice());
        }
    }

    private void adjustGui(final boolean hidden)
    {
        Home.getInstance().getDeviceField().show(hidden);
    }

    public boolean getBackgroundScannerRunning()
    {
        return this.backgroundScannerRunning;
    }

    public boolean isConnected()
    {
        return udc != null && udc.getConnection() != null && udc.getConnection().isConnected();
    }
}
