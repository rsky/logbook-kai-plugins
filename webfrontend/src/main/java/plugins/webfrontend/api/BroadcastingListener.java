package plugins.webfrontend.api;

import logbook.api.APIListenerSpi;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;
import plugins.webfrontend.server.KcApiBroadcaster;

import javax.json.JsonObject;

public class BroadcastingListener implements APIListenerSpi {
    @Override
    public void accept(JsonObject jsonObject, RequestMetaData requestMetaData, ResponseMetaData responseMetaData) {
        KcApiBroadcaster.getInstance().broadcast(jsonObject.toString());
    }
}
