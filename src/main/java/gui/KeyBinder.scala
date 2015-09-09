package gui

import java.awt.event.{KeyEvent, KeyListener}
import java.util

abstract class KeyBinder(val keyCodes: Int*) extends KeyListener {
    private val keyMap: util.Hashtable[Int, Boolean] = new util.Hashtable[Int, Boolean]

    override def keyTyped(e: KeyEvent): Unit = {}

    override def keyPressed(e: KeyEvent): Unit = {
        keyMap.put(e.getKeyCode, true)
        if (getKeysDown) {
            onKeysDown()
        }
    }

    override def keyReleased(e: KeyEvent): Unit =
        keyMap.remove(e.getKeyCode, true)

    private def getKeysDown: Boolean = {
        for (key <- this.keyCodes) {
            if (keyMap.containsKey(key)) {
                if (!keyMap.get(key))
                    return false
            }
            else
                return false
        }
        keyMap.clear()

        true
    }

    def onKeysDown(): Unit
}
