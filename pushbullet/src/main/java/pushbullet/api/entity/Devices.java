package pushbullet.api.entity;

import lombok.Value;
import pushbullet.bean.Device;

import java.util.List;

@Value
public class Devices {
    private List<Device> devices;
}
