package davinci;

public class UnknownCommandException
    extends Exception
{
    public UnknownCommandException()
    {
        super("Unknown command");
    }
}