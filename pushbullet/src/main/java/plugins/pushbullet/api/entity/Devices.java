package plugins.pushbullet.api.entity;

import lombok.Value;
import plugins.pushbullet.bean.Device;

import java.util.List;

@Value
public class Devices {
    List<Device> devices;
}
