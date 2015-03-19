package gui

import java.awt.{BorderLayout, Insets}
import java.io.InputStream
import java.util.Scanner
import javax.swing.{JFrame, JScrollPane, JTextArea, ScrollPaneConstants}

object Licence extends JFrame {
    val istream: InputStream = getClass.getResourceAsStream("licence-gpl.txt")
    val licenseText: String = new Scanner(istream, "UTF-8").useDelimiter("\\A").next

    this.setTitle("Licence")
    this.setBounds(100, 100, 640, 800)

    val textField: JTextArea = new JTextArea
    textField.setEditable(false)
    textField.setMargin(new Insets(10, 10, 10, 10))
    textField.setAlignmentX(0)
    textField.setText(licenseText)
    textField.setCaretPosition(0)

    this.add(textField, BorderLayout.CENTER)

    val scroll: JScrollPane = new JScrollPane(textField,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
    this.getContentPane.add(scroll)

    def showLicense(): Unit = this.setVisible(true)
}
