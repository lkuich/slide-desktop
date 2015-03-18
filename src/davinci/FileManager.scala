package davinci

import java.io.{FileOutputStream, File}
import java.net.{URLConnection, URL}
import java.nio.channels.{Channels, ReadableByteChannel}

object FileManager {

    def downloadFile(dlsite: String, path: String): Unit = {
        val url: URL = new URL(dlsite)
        val file: File = new File(path)

        if (isConnected(url)) {
            // download file
            try {
                val rbc: ReadableByteChannel = Channels.newChannel(url.openStream())
                val fos: FileOutputStream = new FileOutputStream(file)

                fos.getChannel.transferFrom(rbc, 0, java.lang.Long.MAX_VALUE)
                fos.close();
            } catch {
                case e: Exception =>
                    println("error")
            }
        }
        println("Test")
    }

    def isConnected(site: URL): Boolean = {
        try {
            // test connection
            val conn: URLConnection = site.openConnection()
            conn.setConnectTimeout(5000)
            conn.getContent

            true
        } catch {
            case e: Exception => false
        }
    }

    // val prompt(): Unit = {}
}
