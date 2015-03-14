package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;

public abstract class KeyBinder implements KeyListener
{
    private Hashtable<Integer, Boolean> keyMap;

    private int[] keyCodes;

    public KeyBinder(final int... keyCodes)
    {
        this.keyMap = new Hashtable<>();
        this.keyCodes = keyCodes;
    }

    @Override
    public void keyTyped(final KeyEvent e) { }

    @Override
    public void keyPressed(final KeyEvent e)
    {
        getKeyMap().put(e.getKeyCode(), true);

        if (getKeysDown())
        {
            onKeysDown();
        }
    }

    @Override
    public void keyReleased(final KeyEvent e)
    {
        getKeyMap().remove(e.getKeyCode(), true);
    }

    private Hashtable<Integer, Boolean> getKeyMap()
    {
        return this.keyMap;
    }

    public abstract void onKeysDown();

    public boolean getKeysDown()
    {
        for (final int key : this.keyCodes)
        {
            if (getKeyMap().containsKey(key))
            {
                if (!getKeyMap().get(key))
                {
                    return false;
                }
            } else {
                return false;
            }
        }

        getKeyMap().clear();
        return true;
    }
}
