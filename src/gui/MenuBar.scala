package gui

import javax.swing._
import java.awt.event.{InputEvent, ActionEvent, ActionListener, KeyEvent}

/** Class for creating MenuBar **/
class MenuBar extends JMenuBar with ActionListener {
    /** All items in the file menu */
    private val fileMenuItems: Array[String] = Array[String]("File", "Exit")

    /** All items in the file */
    private val adbMenuItems: Array[String] = Array[String]("ADB", "Restart", "Show devices")

    /** All items in the help menu */
    private val helpMenuItems: Array[String] = Array[String]("Help", "About")

    {
        /** File menu */
        val fileMenu: JMenu = new JMenu(fileMenuItems(0))
        this.add(fileMenu)
        /** Exit submenu */
        val exitMenuItem: JMenuItem = new JMenuItem(fileMenuItems(1))
        exitMenuItem.setMnemonic(KeyEvent.VK_E)
        exitMenuItem.setActionCommand(fileMenuItems(1))
        exitMenuItem.addActionListener(this)

        /** Adb menu */
        val adbMenu: JMenu = new JMenu(adbMenuItems(0))
        this.add(adbMenu)
        /** Adb Restart submenu */
        val restartAdbMenu: JMenuItem = new JMenuItem(adbMenuItems(1))
        restartAdbMenu.setMnemonic(KeyEvent.VK_R)
        restartAdbMenu.setActionCommand(adbMenuItems(1))
        restartAdbMenu.addActionListener(this)
        /** Adb Show Devices submenu */
        val adbShowDevicesMenu: JMenuItem = new JMenuItem(adbMenuItems(2))
        adbShowDevicesMenu.setMnemonic(KeyEvent.VK_S)
        adbShowDevicesMenu.setActionCommand(adbMenuItems(2))
        adbShowDevicesMenu.addActionListener(this)

        /** Help Menu*/
        val helpMenu: JMenu = new JMenu(helpMenuItems(0))
        this.add(helpMenu)
        /** About submenu */
        val aboutMenuItem: JMenuItem = new JMenuItem(helpMenuItems(1))
        aboutMenuItem.setMnemonic(KeyEvent.VK_A)
        aboutMenuItem.setActionCommand(helpMenuItems(1))
        aboutMenuItem.addActionListener(this)

        fileMenu.add(exitMenuItem)

        adbMenu.add(restartAdbMenu)
        adbMenu.add(adbShowDevicesMenu)

        helpMenu.add(aboutMenuItem)
    }

    /** Functions to be defined in implementing class */
    def showAdb(): Unit = {}

    def restartAdb(): Unit = {}

    /**
     * When a menu item is clicked.
     * @param e Event
     */
    def actionPerformed(e: ActionEvent) {
        if (e.getActionCommand == fileMenuItems(1)) { // Exit
            System.exit(0)
        }
        else if (e.getActionCommand == adbMenuItems(1)) { // Restart ADB
            restartAdb()
        }
        else if (e.getActionCommand == adbMenuItems(2)) { // Show ADB devices
            showAdb()
        }
        else if (e.getActionCommand == helpMenuItems(1)) {
            JOptionPane.showMessageDialog(this, "© Slide 2015 - Loren Kuich\nhttp://www.slide-app.com", "About", JOptionPane.PLAIN_MESSAGE)
        }
    }
}