package davinci;

import java.awt.EventQueue;
import java.util.HashSet;
import enums.ConnectionMode;
import enums.PositioningMode;
import gui.Home;

public class Master
{
    private static HashSet<ConnectionMode> connections;

    private static Settings settings;

    public static void main(final String[] args)
    {
        // Initialize app settings
        // AppSettings.configure();

        connections = new HashSet<>();

        EventQueue.invokeLater(
            new Runnable()
            {
                public void run()
                {
                    try
                    {
                        // Loading.getInstance().runLoader();

                        // We're going to duplicate this in Master class
                        Home.getInstance().show();

                        settings = new Settings(1, new int[1], ConnectionMode.WIFI,
                            PositioningMode.RELATIVE);

                        // Set settings
                        settings.setSensitivity(1);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
    }

    public static Settings getSettings()
    {
        return settings;
    }

    public static synchronized HashSet<ConnectionMode> getConnections()
    {
        return connections;
    }

    public static synchronized boolean hasConnection(ConnectionMode connection)
    {
        return connections.contains(connection);
    }

    public static synchronized boolean multipleConnections()
    {
        return connections.size() > 1;
    }

    public static synchronized void addConnection(final ConnectionMode newConnectionStatus)
    {
        connections.add(newConnectionStatus);
    }

    public static synchronized void removeConnection(final ConnectionMode connectionStatus)
    {
        if (connections.contains(connectionStatus))
        {
            connections.remove(connectionStatus);
        }
    }
}