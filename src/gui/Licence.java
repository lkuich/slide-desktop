package gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Licence
{
    private JFrame licenceFrame;

    public Licence()
    {
        final InputStream istream = getClass().getResourceAsStream("licence-gpl.txt");

        @SuppressWarnings("resource")
        final String licenseText = new Scanner(istream, "UTF-8").useDelimiter("\\A").next();

        this.licenceFrame = new JFrame();
        getFrame().setTitle("Licence");
        getFrame().setBounds(100, 100, 640, 800);

        final JTextArea textField = new JTextArea();
        textField.setEditable(false);
        textField.setMargin(new Insets(10, 10, 10, 10));
        textField.setAlignmentX(0);
        getFrame().getContentPane().add(textField, BorderLayout.CENTER);
        textField.setText(licenseText);
        textField.setCaretPosition(0);

        final JScrollPane scroll =
            new JScrollPane(
                textField,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        getFrame().getContentPane().add(scroll);
        getFrame().setVisible(true);
    }

    private JFrame getFrame()
    {
        return this.licenceFrame;
    }
}
