package webappd

import lombok.Synchronized
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

internal class DataHolder private constructor() {

    private var compressedStartJSON: ByteArray? = null

    @get:Synchronized("portLock")
    @set:Synchronized("portLock")
    var portJSON: String? = null

    var startJSON: String?
        @Synchronized("startLock")
        get() {
            if (this.compressedStartJSON == null) {
                return null
            }
            try {
                return this.gzDecompress(this.compressedStartJSON!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
        @Synchronized("startLock")
        set(jsonStr) = try {
            this.compressedStartJSON = this.gzCompress(jsonStr!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    @Throws(IOException::class)
    private fun gzCompress(str: String): ByteArray {
        ByteArrayOutputStream().use { out ->
            GZIPOutputStream(out).use { gzip ->
                gzip.write(str.toByteArray(StandardCharsets.UTF_8))
                gzip.close() // GZIPOutputStream must be closed before getting the compression result!
                return out.toByteArray()
            }
        }
    }

    @Throws(IOException::class)
    private fun gzDecompress(data: ByteArray): String {
        ByteArrayOutputStream().use { out ->
            GZIPInputStream(ByteArrayInputStream(data)).use { gzip ->
                val buf = ByteArray(8192)
                var read = 0
                while ({read = gzip.read(buf); read}() != -1) {
                    out.write(buf, 0, read)
                }
                return String(out.toByteArray(), StandardCharsets.UTF_8)
            }
        }
    }

    companion object {
        const val PORT_URI = "/kcsapi/api_port/port"
        const val START_URI = "/kcsapi/api_start2/getData"

        val instance = DataHolder()
    }
}
