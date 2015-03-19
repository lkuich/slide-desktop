package gui

import java.awt.{BorderLayout, Insets}
import java.io.{BufferedReader, InputStreamReader}
import javax.swing._

import connections.usb.Adb
import davinci.Const

class Console extends JFrame {
    private val consoleTextField: JTextArea = new JTextArea()
    this.setTitle("Output")
    this.setBounds(100, 100, 400, 200)

    consoleTextField.setEditable(false)
    consoleTextField.setMargin(new Insets(10, 10, 10, 10))
    consoleTextField.setAlignmentX(0)
    this.getContentPane.add(consoleTextField, BorderLayout.CENTER)
    consoleTextField.setCaretPosition(0)

    val scroll: JScrollPane = new JScrollPane(consoleTextField,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
    this.getContentPane.add(scroll)

    def append(text: String): Unit = {
        if (consoleTextField.getText == "")
            consoleTextField.append(text)
        else
            consoleTextField.append("\n" + text)
    }

    def showConsole(): Unit = this.setVisible(true)

    def showAdbDevices(): Unit = {
        var deviceAvailable: Boolean = false
        var command: String = Const.ADB + " devices"

        if (Adb.isAdbFilePresent && !Adb.isAdbInstalled) {
            command = Adb.adbFilePath + " devices"
        }

        var consoleOut: String = null
        var stdInput: BufferedReader = null
        var stdError: BufferedReader = null
        if (Adb.isAdbFilePresent) {
            val pr: Process = Runtime.getRuntime.exec(command)

            stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream))
            stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream))

            while ( {
                consoleOut = stdInput.readLine(); consoleOut != null
            }) {
                if (consoleOut.contains("	device")) {
                    deviceAvailable = true
                }
                this.append(consoleOut)
            }

            var errorOut: String = null
            while ( {
                errorOut = stdError.readLine(); errorOut != null
            }) {
                this.append(errorOut)
            }
        } else {
            this.append("Error: ADB is not installed")
        }

        showConsole()
    }
}
