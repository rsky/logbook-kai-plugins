package pushbullet.service;

import lombok.Value;
import pushbullet.bean.Device;

import java.util.List;

@Value
public class DeviceListResult {
    private List<Device> devices;
}
