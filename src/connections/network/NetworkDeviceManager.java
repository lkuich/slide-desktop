package connections.network;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import davinci.Device;
import davinci.Master;
import enums.ConnectionMode;
import gui.Home;
import gui.img.ImageIcons;

public class NetworkDeviceManager
{
    private NetworkDeviceConnection udc;

    private boolean backgroundScannerRunning = true;
    private Device networkDevice;

    public NetworkDeviceManager()
    {
    }

    public void connect(
        String ip,
        int port)
        throws IOException
    {
        udc = new NetworkDeviceConnection(ip, port);
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
                    int dcCount = 0; // Seconds no UDP signal is detected
                    final BroadcastManager udpDiscovery;
                    try
                    {
                        udpDiscovery = new BroadcastManager();
                    } catch (final SocketException e)
                    {
                        // Show error prompt
                        Home.getInstance().showErrorPrompt("Error",
                            "Another instance of Slide is already running.");

                        System.exit(1);
                        return;
                    }
                    while (backgroundScannerRunning)
                    {
                        if (!udpDiscovery.networkIsAvailable())
                        {
                            stopBackgroundScanner(); // Stop background scanner and break execution
                        }

                        networkDevice = udpDiscovery.search();
                        if (networkDevice != null)
                        {
                            dcCount = 0; // Reset disconnect
                            if (!Master.hasConnection(ConnectionMode.WIFI))
                            {
                                Master.addConnection(ConnectionMode.WIFI);
                                if (Master.multipleConnections())
                                {
                                    adjustGui(ConnectionMode.USB);
                                } else {
                                    adjustGui(ConnectionMode.WIFI);
                                }
                            }
                        } else
                        {
                            dcCount++;
                            if (dcCount >= 4) // 4 second timeout
                            {
                                if (Home.getInstance().getDeviceField() != null)
                                {
                                    if (Master.hasConnection(ConnectionMode.WIFI))
                                    {
                                        Master.removeConnection(ConnectionMode.WIFI);
                                        if (Master.hasConnection(ConnectionMode.USB))
                                        {
                                            adjustGui(ConnectionMode.USB);
                                        } else {
                                            adjustGui(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        t.start();
    }

    public void stopBackgroundScanner()
    {
        backgroundScannerRunning = false;
    }

    public Device getDevice()
    {
        return this.networkDevice;
    }

    public boolean networkInterfaceIsAvailable()
        throws SocketException
    {
        final Enumeration<NetworkInterface> networkInterfaces =
            NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements())
        {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface.isUp() && !networkInterface.isLoopback())
            {
                return true;
            }
        }
        return false;
    }

    // Getters
    public boolean getBackgroundScannerRunning()
    {
        return this.backgroundScannerRunning;
    }

    private void adjustGui(final ConnectionMode connectionMode)
    {
        Home.getInstance().getDeviceField().show(true);
        if (connectionMode == ConnectionMode.USB)
        {
            if (getDevice() != null)
            {
                getDevice().icon_$eq(ImageIcons.getInstance().getUsbIcon());
                Home.getInstance().getDeviceField().setUi(getDevice());
            }
        } else {
            getDevice().icon_$eq(ImageIcons.getInstance().getWifiIcon());
            Home.getInstance().getDeviceField().setUi(getDevice());
        }
    }

    private void adjustGui(final boolean hidden)
    {
        Home.getInstance().getDeviceField().show(hidden);
    }

    public boolean isConnected()
    {
        return udc != null && udc.getConnection() != null && udc.getConnection().isConnected();
    }
}
