package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import connections.network.NetworkDeviceManager;
import connections.usb.Adb;
import connections.usb.UsbDeviceManager;
import davinci.Const;
import davinci.Master;
import enums.ConnectionMode;

public class Home
{
    private static final Object instance = new Object();

    private DeviceField deviceField;
    private JPanel devicePanel;

    private JFrame frame;

    private UsbDeviceManager usbMan;
    private NetworkDeviceManager networkMan;

    private ErrorMessage errorMessage;

    protected Home()
    {
        initialize();
    }

    public static Home getInstance()
    {
        return Holder.INSTANCE;
    }

    public void show()
    {
        getFrame().setVisible(true);
    }

    private void initialize()
    {
        errorMessage = new ErrorMessage(getFrame(), "Connection Error");

        // Initialize interface components
        this.frame = new JFrame();
        getFrame().setTitle("Slide");
        getFrame().setResizable(false);
        getFrame().setBounds(100, 100, 250, 210);
        getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //final JPanel start_panel = new JPanel();
        this.devicePanel = new JPanel();
        getDevicePanel().setVisible(false);
        getDevicePanel().setLayout(null); // Absolute, should avoid using this!!
        getFrame().getContentPane().add(getDevicePanel(), BorderLayout.CENTER);
/*
        this.initPanel = new JPanel();
        getDevicePanel().setLayout(null); // Absolute, should avoid using this!!
        getFrame().getContentPane().add(this.initPanel, BorderLayout.CENTER);*/

        final int middle_x = 250; // (getFrame().getBounds().width / 2);
        final int middle_y = 100; // (getFrame().getBounds().height / 2);

        this.deviceField = new DeviceField(this.devicePanel, middle_x, middle_y);

        /* -------------------------------------------------- */

        // Start managers
        usbMan = new UsbDeviceManager();
        networkMan = new NetworkDeviceManager();

        getDeviceField().setMouseListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (Master.hasConnection(ConnectionMode.USB) || Master.multipleConnections())
                    {
                        try
                        {
                            usbMan.connect();
                        } catch (final Exception e1)
                        {
                            errorMessage.setMessage("Could not connect over USB.\nCheck if your device is listed by pressing Alt+A");
                            errorMessage.showDialog();
                            e1.printStackTrace();
                        }
                    } else if (Master.hasConnection(ConnectionMode.WIFI))
                    {
                        try
                        {
                            networkMan.connect(networkMan.getDevice().ip(), Const.NET_PORT());
                        } catch (final IOException e2)
                        {
                            errorMessage.setMessage("Could not connect over LAN.");
                            errorMessage.showDialog();
                        }
                    }
                }
            });

        // Scan connections.network for devices and automatically add them to the HviewList
        networkMan.startBackgroundScanner();
        // Scan for USB devices and automatically add them to the HviewList
        usbMan.startBackgroundScanner();



        /* -------------------------------------------------- */

        //networkMan.stopBackgroundScanner();
        //networkDeviceListListener.stopListening();

        //usbMan.stopBackgroundScanner();
        //usbDeviceListListener.stopListening();

        //TODO: REMOVE THIS just for testing purposes
        //TEST_USB_CONNECT(usbMan);
        //TODO: REMOVE THIS just for testing purposes
        //TEST_WIFI_CONNECT(networkMan, "192.168.1.23", Const.PORT);

    }

    public void showErrorPrompt(final String title, final String message)
    {
        errorMessage.setTitle(title);
        errorMessage.setMessage(message);
        errorMessage.showDialog();
    }

    public void showPrompt(final String message)
    {
        JOptionPane.showMessageDialog(getFrame(), message);
    }

    // Getters
    public DeviceField getDeviceField()
    {
        return deviceField;
    }

    public JPanel getDevicePanel()
    {
        return this.devicePanel;
    }

    public JFrame getFrame()
    {
        return frame;
    }


    private static class Holder
    {
        static final Home INSTANCE = new Home();
    }
}
