package connections.bluetooth;

import java.io.IOException;

import connections.BaseDeviceConnection;

public class BluetoothDeviceConnection
    extends BaseDeviceConnection
{

    @Override
    public boolean connect()
        throws IOException
    {
        return false;
    }

    @Override
    public void close()
    {

    }

    @Override
    public short[] getNextMessage()
        throws IOException, ClassNotFoundException
    {
        return new short[0];
    }
}