package plugins.slack.api.entity;

import lombok.Value;
import plugins.slack.bean.Device;

import java.util.List;

@Value
public class Devices {
    List<Device> devices;
}
