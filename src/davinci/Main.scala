package davinci

import java.awt.EventQueue
import gui.Frame

object Main {
    def main(args: Array[String]): Unit = {
        EventQueue.invokeLater( new Runnable {
            override def run(): Unit = {
                Frame.showHome()
            }
        })
    }
}