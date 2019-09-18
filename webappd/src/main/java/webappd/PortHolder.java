package webappd;

import lombok.Synchronized;

class PortHolder {
    final static String PORT_URI = "/kcsapi/api_port/port";

    private static PortHolder INSTANCE = new PortHolder();

    private String portJSON = null;

    private PortHolder() {
    }

    @Synchronized
    String getJSON() {
        return this.portJSON;
    }

    @Synchronized
    void setJSON(String json) {
        this.portJSON = json;
    }

    static PortHolder getInstance() {
        return INSTANCE;
    }
}
