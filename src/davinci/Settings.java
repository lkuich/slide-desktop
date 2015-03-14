package davinci;

import enums.ConnectionMode;
import enums.PositioningMode;

public class Settings
{
    private double sensitivity;
    private int[] scale;
    private ConnectionMode connectionMode;
    private PositioningMode positioningMode;
    private int scrollAxis;

    public Settings(final double sensitivity, final int[] scale, final ConnectionMode mode,
        final PositioningMode positioningMode)
    {
        this.sensitivity = sensitivity;
        this.scale = scale;
        this.connectionMode = mode;
        this.positioningMode = positioningMode;
        this.scrollAxis = -1;
    }

    public void setScale(final int scaleX, final int scaleY)
    {
        this.scale[Const.X] = scaleX;
        this.scale[Const.Y] = scaleY;
    }

    public void setSensitivity(final double sensitivity)
    {
        this.sensitivity = sensitivity;
    }

    public void setConnectionMode(final ConnectionMode mode)
    {
        this.connectionMode = mode;
    }

    public void setPositioningMode(final PositioningMode mode)
    {
        this.positioningMode = mode;
    }

    public void setScrollAxis(final int axis)
    {
        this.scrollAxis = axis;
    }

    public int[] getScale()
    {
        return this.scale;
    }

    public double getSensitivity()
    {
        return this.sensitivity;
    }

    public ConnectionMode getConnectionMode()
    {
        return this.connectionMode;
    }

    public PositioningMode getPositioningMode()
    {
        return this.positioningMode;
    }

    public int getScrollAxis()
    {
        return this.scrollAxis;
    }
}
