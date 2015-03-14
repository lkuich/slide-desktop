package connections.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import davinci.Device;
import gui.img.ImageIcons;

public class BroadcastManager
{
    private DatagramSocket serverSocketUDP = null;

    public BroadcastManager()
        throws SocketException
    {
        serverSocketUDP = new DatagramSocket(5000);
    }

    public boolean networkIsAvailable()
    {
        // TODO
        return true;
    }

    public Device search()
    {
        final byte[] receiveData = new byte[1024];

        try
        {
            final DatagramPacket receivePacket = new DatagramPacket(
                receiveData,
                receiveData.length);
            serverSocketUDP.setSoTimeout(1000);
            serverSocketUDP.receive(receivePacket);

            final String message =
                new String(receivePacket.getData()).trim();

            return new Device(ImageIcons.getInstance().getWifiIcon(), message.split(","));

        } catch (IOException e)
        {
            return null;
        }
    }
}