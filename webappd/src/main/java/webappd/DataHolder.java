package webappd;

import lombok.Synchronized;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class DataHolder {
    final static String PORT_URI = "/kcsapi/api_port/port";
    final static String START_URI = "/kcsapi/api_start2/getData";

    private static DataHolder INSTANCE = new DataHolder();

    private byte[] compressedStartJSON = null;
    private String portJSON = null;

    private DataHolder() {
    }

    @Synchronized
    String getPortJSON() {
        return this.portJSON;
    }

    @Synchronized
    void setPortJSON(String jsonStr) {
        this.portJSON = jsonStr;
    }

    @Synchronized
    String getStartJSON() {
        if (this.compressedStartJSON == null) {
            return null;
        }
        try {
            return this.gzDecompress(this.compressedStartJSON);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Synchronized
    void setStartJSON(String jsonStr) {
        try {
            this.compressedStartJSON = this.gzCompress(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] gzCompress(String str) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (OutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(str.getBytes());
            return out.toByteArray();
        }
    }

    private String gzDecompress(byte[] data) throws IOException {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                GZIPInputStream zin = new GZIPInputStream(in);
        ) {
            byte[] buf = new byte[8192];
            int read;
            while ((read = zin.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
            return out.toString();
        }
    }

    static DataHolder getInstance() {
        return INSTANCE;
    }
}