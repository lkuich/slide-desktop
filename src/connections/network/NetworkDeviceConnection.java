package connections.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import connections.BaseDeviceConnection;

public class NetworkDeviceConnection
    extends BaseDeviceConnection
{

    private String remoteHost;
    private int remotePort;

    private ObjectInputStream input;
    private Socket connection;

    public NetworkDeviceConnection(
        final String remoteHost,
        final int remotePort)
    {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public boolean connect()
        throws IOException
    {
        final InetSocketAddress inetAddress =
            new InetSocketAddress(getRemoteHost(), getRemotePort());

        this.connection = new Socket();
        getConnection().connect(inetAddress, 2000); // 2000 ms timeout
        getConnection().setTcpNoDelay(true);

        input = new ObjectInputStream(getConnection().getInputStream());

        //NetworkDeviceManager.updateUiAfterConnection(); // TODO: update UI

        return this.start();
    }

    @Override
    public short[] getNextMessage()
        throws IOException,
        ClassNotFoundException
    {
        /*final short[] message =
                (short[]) this.input.readObject();*/
        
        /* TODO: The below code will check the incoming objects type,
        we can use this to differentiate between commands (byte), and input (short[])
         */
        
        /*short[] message = {1, 2};
        final Object msg = this.input.readObject();
    	if (msg instanceof Byte)
        	System.out.println("byte");
        else
        	System.out.println(msg.getClass());*/

        return (short[]) getInput().readObject();
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
    public String getRemoteHost()
    {
        return this.remoteHost;
    }

    public int getRemotePort()
    {
        return this.remotePort;
    }

    public Socket getConnection()
    {
        return this.connection;
    }

    public ObjectInputStream getInput()
    {
        return this.input;
    }

    // Setters
    public void setRemotePort(final int remotePort)
    {
        this.remotePort = remotePort;
    }
}