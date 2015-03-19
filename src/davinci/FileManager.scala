package davinci

import java.io.{File, FileOutputStream}
import java.net.{URL, URLConnection}
import java.nio.channels.{Channels, ReadableByteChannel}

class FileManager {

    var currentFile: String = ""
    var numberOfDownloads: Int = 0

    def downloadFile(dlsite: String, path: String): Unit = {
        val url: URL = new URL(dlsite)
        val file: File = new File(path)

        if (isConnected(url)) {
            currentFile = path
            onDownloadStart()

            new Thread(new Runnable {
                override def run(): Unit = {
                    try {
                        val rbc: ReadableByteChannel = Channels.newChannel(url.openStream())
                        val fos: FileOutputStream = new FileOutputStream(file)

                        fos.getChannel.transferFrom(rbc, 0, java.lang.Long.MAX_VALUE)
                        fos.close()

                        numberOfDownloads += 1
                        onDownloadFinished()
                    } catch {
                        case e: Exception =>
                            println("Error: Could not download ADB, please run as Administrator")
                    }
                }
            }).start()
        }
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

    def onDownloadStart(): Unit = {}

    def onDownloadFinished(): Unit = {}

    // var onDownloadStart: () => Unit = null
    // var onDownloadFinished: () => Unit = null
}
