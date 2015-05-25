package gui

import java.awt.{BorderLayout, Insets}
import java.io.{BufferedReader, InputStreamReader}
import javax.swing._

import connections.usb.Adb
import slide.Const

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

    def runProcess(pr: Process): Unit = {

        var consoleOut: String = null
        var stdInput: BufferedReader = null
        var stdError: BufferedReader = null
        if (pr != null) {
            stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream))
            stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream))
            while ( {
                consoleOut = stdInput.readLine()
                consoleOut != null
            }) {
                this.append(consoleOut)
            }

            var errorOut: String = null
            while ( {
                errorOut = stdError.readLine()
                errorOut != null
            }) {
                this.append(errorOut)
            }
        }

        showConsole()
    }

    def runAdbProcess(pr: Process): Unit = {
        var deviceAvailable: Boolean = false

        var consoleOut: String = null
        var stdInput: BufferedReader = null
        var stdError: BufferedReader = null
        if (Adb.isAdbFilePresent && pr != null) {
            stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream))
            stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream))

            while ( {
                consoleOut = stdInput.readLine()
                consoleOut != null
            }) {
                if (consoleOut.contains("	device")) {
                    deviceAvailable = true
                }
                this.append(consoleOut)
            }

            var errorOut: String = null
            while ( {
                errorOut = stdError.readLine()
                errorOut != null
            }) {
                this.append(errorOut)
            }
        } else {
            this.append("Error: ADB is not installed")
        }

        showConsole()
    }
}
