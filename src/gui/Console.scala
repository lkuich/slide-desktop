package gui

import java.awt.{BorderLayout, Insets}
import javax.swing.{ScrollPaneConstants, JScrollPane, JTextArea, JFrame}

class Console {
    val consoleFrame: JFrame = new JFrame
    val consoleTextField: JTextArea = new JTextArea()
    consoleFrame.setTitle("Output")
    consoleFrame.setBounds(100, 100, 400, 200)

    consoleTextField.setEditable(false)
    consoleTextField.setMargin(new Insets(10, 10, 10, 10))
    consoleTextField.setAlignmentX(0)
    consoleFrame.getContentPane.add(consoleTextField, BorderLayout.CENTER)
    consoleTextField.setCaretPosition(0)

    val scroll: JScrollPane = new JScrollPane(consoleTextField,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
    consoleFrame.getContentPane.add(scroll)
}
