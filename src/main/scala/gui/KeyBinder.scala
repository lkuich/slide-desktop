package gui

import java.awt.event.{KeyEvent, KeyListener}
import scala.collection.mutable.HashMap

abstract class KeyBinder(val keyCodes: Int*) extends KeyListener {
    private val keyMap: HashMap[Int, Boolean] = new HashMap[Int, Boolean]

    override def keyTyped(e: KeyEvent): Unit = {}

    override def keyPressed(e: KeyEvent): Unit = {
        keyMap.put(e.getKeyCode, true)
        if (getKeysDown)
            onKeysDown()
    }

    override def keyReleased(e: KeyEvent): Unit =
        keyMap.remove(e.getKeyCode)

    private def getKeysDown: Boolean = {
        this.keyCodes.foreach(key =>
            if (keyMap.contains(key)) {
                if (!keyMap.get(key).get)
                    return false
            }
            else
                return false
        )
        keyMap.clear()

        true
    }

    def onKeysDown(): Unit
}
