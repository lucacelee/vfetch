import java.util.ArrayList;

public class USBDevice extends BusDevice
{
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

    public USBDevice(int bus, int device, int vendorID, int productID)
    {
        this.bus = bus;
        this.device = device;
        this.vendorId = vendorID;
        this.deviceId = productID;
    }
    
    public static ArrayList<USBDevice> mapUSBDevices() throws Exception
    {
        ArrayList<USBDevice> USBDevices = new ArrayList<>();

        idLookup usb = idLookup.usb();
        vfetch.println("USB devices: " + usbInfo.getCount());                     
        for (int i = 0; i < usbInfo.getCount(); i++) {
            int vendor = usbInfo.getVendorId(i);
            int product = usbInfo.getProductId(i);
            vfetch.printf("  Bus %03d Device %03d: ID %04x:%04x  %s %s%n", usbInfo.getBusNumber(i),
                              usbInfo.getDeviceNumber(i), vendor, product,
                              usb.vendorName(vendor), usb.deviceName(vendor, product));

            USBDevice device = new USBDevice(usbInfo.getBusNumber(i), usbInfo.getDeviceNumber(i), vendor, product);

            USBDevices.add(device);
        }

        return USBDevices;
    }
}