package gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ErrorMessage
    extends JOptionPane
{
    private JFrame frame;
    private String title;
    private String message;

    public ErrorMessage(final JFrame frame)
    {
        this.frame = frame;
        this.title = "";
        this.message = "";
    }

    public ErrorMessage(final JFrame frame, final String title)
    {
        this.frame = frame;
        this.title = title;
        this.message = "";
    }

    public ErrorMessage(final JFrame frame, final String title, final String message)
    {
        this.frame = frame;
        this.title = title;
        this.message = message;
    }

    public void showDialog()
    {
        showMessageDialog(
            this.frame,
            this.message,
            this.title,
            JOptionPane.ERROR_MESSAGE);
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }

    public void setMessage(final String message)
    {
        this.message = message;
    }
}
