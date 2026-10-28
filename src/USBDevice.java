import java.io.IOException;
import java.util.ArrayList;

public class USBDevice extends BusDevice
{
    private static int count = usbInfo.getCount();

    public static void usbInfo() throws Exception
    {
        ArrayList<ArrayList<String>> USBDevices = new ArrayList<>();

        idLookup usb = idLookup.usb();
        vfetch.println("USB devices: " + usbInfo.getCount());                     
        for (int i = 0; i < usbInfo.getCount(); i++) {
            int vendor = usbInfo.getVendorId(i);
            int product = usbInfo.getProductId(i);
            vfetch.printf("  Bus %03d Device %03d: ID %04x:%04x  %s %s%n", usbInfo.getBusNumber(i),
                              usbInfo.getDeviceNumber(i), vendor, product,
                              usb.vendorName(vendor), usb.deviceName(vendor, product));

            ArrayList<String> device = new ArrayList<>();
            device.add(String.valueOf(usbInfo.getBusNumber(i)));
            device.add(String.valueOf(usbInfo.getDeviceNumber(i)));
            device.add(String.valueOf(vendor));
            device.add(String.valueOf(product));

            //2D arraylist containing the usb data
            USBDevices.add(device);
        }
    }

    public USBDevice(int bus, int device, int vendorID, int productID, int deviceID, int classCode, String vendorName, String deviceName, String className)
    {
        this.bus = bus;
        this.device = device;
        this.function = -1;
        this.vendorId = vendorID;
        this.deviceId = deviceID;
        this.classCode = classCode;
        this.vendorName = vendorName;
        this.deviceName = deviceName;
        this.className = className;
        this.address = null;
        this.typeOfBus = "usb";
    }

    public static ArrayList<USBDevice> mapDevices() throws IOException{
        idLookup usb = idLookup.usb();
        ArrayList<USBDevice> list = new ArrayList<USBDevice>();
        vfetch.println("USB devices: " + usbInfo.getCount());  

        for(int i = 0; i < count; i++){
            int bus = usbInfo.getBusNumber(i);
            int device = usbInfo.getDeviceNumber(i);
            int function = -1;
            int vendorID = usbInfo.getVendorId(i);
            int deviceID = usbInfo.getProductId(i);
            int classCode = usbInfo.getClassCode(i);
            String vendorName = usb.vendorName(vendorID);
            String deviceName = usbInfo.getProduct(i);
            String className = usb.deviceName(vendorID, deviceID);

            list.add(new USBDevice(bus, device, function, vendorID, deviceID, classCode, vendorName, deviceName, className));

            vfetch.printf("  Bus %03d Device %03d: ID %04x:%04x  %s %s%n", usbInfo.getBusNumber(i),
                              usbInfo.getDeviceNumber(i), vendorID, deviceID,
                              vendorName, deviceName);
        }
        return list;
    }
}