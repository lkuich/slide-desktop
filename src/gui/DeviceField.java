package gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import connections.usb.Adb;
import davinci.Device;

public class DeviceField
{
    private ActionListener actionListener;

    private JPanel masterPanel;

    private int posX = 0;
    private int posY = 0;

    private Rectangle bounds;

    private JLabel lblNoDevicesDetected;

    private JButton connectButton;

    private JLabel imageLabel;
    private JLabel ipAddressLabel;
    private JLabel nameLabel;
    private JLabel manuLabel;

    private String ip;
    private String name;
    private String manufacturer;

    public DeviceField(final JPanel panel, final int posX, final int posY)
    {
        this.ip = "localhost";
        this.name = "";
        this.manufacturer = "";

        this.posX = posX;
        this.posY = posY;

        this.masterPanel = panel;
        //this.masterPanel.setBounds(100, 100, 250, 200);
        this.masterPanel.setVisible(true);

        this.lblNoDevicesDetected = new JLabel(
            "<html><center>No devices detected<br/><br/>Scanning for devices...</center></html>");

        this.lblNoDevicesDetected.setHorizontalAlignment(SwingConstants.CENTER);
        this.bounds = new Rectangle(
            SwingConstants.CENTER,
            SwingConstants.VERTICAL,
            posX,
            posY);
        this.lblNoDevicesDetected.setBounds(this.bounds);

        this.connectButton = new JButton("Connect");
        this.nameLabel = new JLabel();
        this.manuLabel = new JLabel();
        this.ipAddressLabel = new JLabel();

        getMasterPanel().add(lblNoDevicesDetected);
        getMasterPanel().add(manuLabel);
        getMasterPanel().add(nameLabel);
        getMasterPanel().add(ipAddressLabel);
        getMasterPanel().add(connectButton);

        final KeyBinder alta = new KeyBinder(KeyEvent.VK_ALT, KeyEvent.VK_A)
        {
            @Override
            public void onKeysDown()
            {
                Adb.showAdbDevices();
            }
        };

        final KeyBinder altl = new KeyBinder(KeyEvent.VK_ALT, KeyEvent.VK_L)
        {
            @Override
            public void onKeysDown()
            {
                new Licence();
            }
        };

        connectButton.addKeyListener(alta);
        connectButton.addKeyListener(altl);
    }

    public void setUi(final Device d)
    {
        if (this.imageLabel != null)
        {
            getMasterPanel().remove(this.imageLabel);
        }

        final ImageIcon icon = d.icon();
        if (!d.ip().equals("USB"))
        {
            this.ip = d.ip();
            this.name = d.model();
            this.manufacturer = d.manufacturer();
        }

        /*
        if (Master.hasConnection(AvailableConnections.USB) || Master.multipleConnections())
        {
            this.icon = d.getImage();
        } else
        {
            this.icon = d.getImage();
            if (!d.getIp().equals("USB"))
            {
                this.ip = d.getIp();
                this.name = d.getDeviceModel();
                this.manufacturer = d.getManufacturer();
            }
        }*/

        this.imageLabel = new JLabel(icon);
        getMasterPanel().add(imageLabel);

        setComponents();
    }

    private void setComponents()
    {
        //imageLabel.setIcon(this.icon);
        //imageLabel.setBackground(Color.white);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBounds(this.bounds);
        imageLabel.setBounds(
            SwingConstants.CENTER,
            SwingConstants.VERTICAL,
            posX,
            posY);

        nameLabel.setText(this.name);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setBackground(Color.white);
        //this.bounds.setSize(100, 20);
        //this.bounds.setLocation((int) this.bounds.getX(), (int) this.bounds.getY() + (int)this.bounds.getHeight());
        nameLabel.setBounds(
            SwingConstants.CENTER,
            SwingConstants.VERTICAL,
            posX,
            posY + 80);

        manuLabel.setText(manufacturer);
        manuLabel.setBackground(Color.white);
        manuLabel.setHorizontalAlignment(SwingConstants.CENTER);
        //this.bounds.setLocation((int) this.bounds.getX(), (int) this.bounds.getY() + (int)this.bounds.getHeight());
        manuLabel.setBounds(
            SwingConstants.CENTER,
            SwingConstants.VERTICAL,
            posX,
            posY + 110);

        ipAddressLabel.setText(ip);
        ipAddressLabel.setBackground(Color.white);
        ipAddressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        //this.bounds.setLocation((int) this.bounds.getX(), (int) this.bounds.getY() + (int)this.bounds.getHeight());
        ipAddressLabel.setBounds(
            SwingConstants.CENTER,
            SwingConstants.VERTICAL,
            posX,
            posY + 140);

        connectButton.setHorizontalAlignment(SwingConstants.CENTER);
        connectButton.setBounds(78, 140, 100, 30);

        setListeners();

        getMasterPanel().updateUI();
    }

    public void setListeners()
    {
        if (connectButton.getActionListeners().length <= 0)
        {
            connectButton.addActionListener(getActionListener());
        }
    }

    private JPanel getMasterPanel()
    {
        return this.masterPanel;
    }

    public void setMouseListener(final ActionListener mouseListener)
    {
        this.actionListener = mouseListener;
    }

    public void show(final boolean visibility)
    {
        this.lblNoDevicesDetected.setVisible(!visibility);

        if (this.imageLabel != null)
        {
            this.imageLabel.setVisible(visibility);
        }
        if (this.nameLabel != null)
        {
            this.nameLabel.setVisible(visibility);
            this.nameLabel.setText("");
        }
        if (this.manuLabel != null)
        {
            this.manuLabel.setVisible(visibility);
            this.manuLabel.setText("");
        }
        if (this.ipAddressLabel != null)
        {
            this.ipAddressLabel.setVisible(visibility);
            this.ipAddressLabel.setText("localhost");
        }
        if (this.connectButton != null)
        {
            this.connectButton.setVisible(visibility);
        }
    }

    public ActionListener getActionListener()
    {
        return this.actionListener;
    }
}
