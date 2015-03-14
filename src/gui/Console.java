package gui;

import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Console
{
    public JFrame consoleFrame;
    public JTextArea consoleTextField;

    public Console()
    {
        consoleFrame = new JFrame();
        consoleFrame.setTitle("Output");
        consoleFrame.setBounds(100, 100, 400, 200);

        consoleTextField = new JTextArea();
        consoleTextField.setEditable(false);
        consoleTextField.setMargin(new Insets(10, 10, 10, 10));
        consoleTextField.setAlignmentX(0);
        consoleFrame.getContentPane().add(consoleTextField, BorderLayout.CENTER);
        consoleTextField.setCaretPosition(0);

        JScrollPane scroll =
            new JScrollPane(
                consoleTextField,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        consoleFrame.getContentPane().add(scroll);
    }
}