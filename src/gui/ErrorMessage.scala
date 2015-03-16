package gui

import javax.swing.{JFrame, JOptionPane}

class ErrorMessage(val frame: JFrame, var title: String, var message: String) {
    def this(frame: JFrame, title: String) = this(frame, title, "")

    def showDialog(): Unit = {
        JOptionPane.showMessageDialog(this.frame, this.message, this.title, JOptionPane.ERROR_MESSAGE)
    }
}
