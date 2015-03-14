package connections;

import java.io.IOException;

public interface DeviceConnection
{
    boolean connect()
        throws IOException;

    void close();

    short[] getNextMessage()
        throws IOException,
        ClassNotFoundException;

    boolean start()
        throws IOException;
}
