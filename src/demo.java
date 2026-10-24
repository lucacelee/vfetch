
import java.util.List;

/**
 * A short example that shows how to call each provided class. It prints raw
 * numbers and a few names. It is NOT a solution to the assignment: you still
 * have to measure load, store the devices, sort them, analyse them and present
 * the results.
 *
 * Build:  javac *.java
 * Run:    java demo        (run it from the folder that contains the ids folder)
 */
public class demo
{
    public static void main(String[] args) throws Exception
    {

        //Requirement 1: Processor

        System.out.println();
        System.out.println("model:  " + cpuInfo.getModel());
        System.out.println("cores:  " + cpuInfo.getCount());
        System.out.println();

        Trial trial = new Trial(1000);
        System.out.printf("The average load across all cores is %.2f%%%n", trial.getCpuLoad());
        for(int i = 0; i < cpuInfo.getCount(); i++){
            System.out.printf("The load across core %d is %.2f%%%n", i, trial.getCoreLoad(i));
        }
        System.out.println();

        //Requirement 2: Memory

        
        System.out.println("memory: total=" + memInfo.getTotal() + " KiB, used=" + memInfo.getUsed() + " KiB");
        long percentInUse = (memInfo.getUsed() / memInfo.getTotal()) * 100;
        System.out.println("Percentage in use: " + percentInUse + "%");
        System.out.println();

        //Requirement 3 + 6: Pci devices and analysis

        List<PciDevice> pciDevices = PciDevice.mapDevices();
        System.out.printf("PCI DEVICES LIST (Total: %d devices)%n", pciInfo.getCount());

        //PCI devices, sorted by numerical address order bus, device, function

        PciDevice.sortByAddress(pciDevices);
        System.out.println("----SORTED NUMERICALLY BY ADDRESS - BUS, DEVICE, FUNCTION-----");
        System.out.printf("%-20s %-35s%n", "Device Address", "Device Name");
        System.out.println("-------------------------------------------------------------");
        for(PciDevice dev : pciDevices){
            System.out.printf("%-20s %-35s%n", dev.getAddress(), dev.getDeviceName());
        }
        System.out.println();

        //PCI devices sorted in alphabetical order in reference to vendor name
        PciDevice.sortByVendorName(pciDevices);
        System.out.println("------SORTED ALPHABETICALLY BY VENDOR NAME-----");
        System.out.printf("%-35s %-20s%n", "Vendor Name", "Device Address");
        System.out.println("-------------------------------------------------------------");
        for(PciDevice dev : pciDevices){
            System.out.printf("%-35s %-20s%n", dev.getVendorName(), dev.getAddress());
        }

        System.out.println();
        //Summary: I chose the example one of listing the number of devices per vendor
        System.out.println("----SUMMARY OF NUMBER OF DEVICES PER VENDOR----\n");
        PciDevice.devicesPerVendor(pciDevices);
        System.out.println();
        //I kept the print statement for this one in the PciDevices class file because it was 
        //so much easier because all the variables were local to that class

        // idLookup pci = idLookup.pci();
        // System.out.println("PCI devices: " + pciInfo.getCount());
        // for (int i = 0; i < pciInfo.getCount(); i++) {
        //     int vendor = pciInfo.getVendorId(i);
        //     int device = pciInfo.getDeviceId(i);
        //     System.out.printf("  %s  %04x:%04x  %s %s%n", pciInfo.getAddress(i), vendor, device,
        //                       pci.vendorName(vendor), pci.deviceName(vendor, device));
        // }

        //Requirement 4: USB devices

        idLookup usb = idLookup.usb();
        System.out.println("USB devices: " + usbInfo.getCount());
        for (int i = 0; i < usbInfo.getCount(); i++) {
            int vendor = usbInfo.getVendorId(i);
            int product = usbInfo.getProductId(i);
            System.out.printf("  Bus %03d Device %03d: ID %04x:%04x  %s %s%n", usbInfo.getBusNumber(i),
                              usbInfo.getDeviceNumber(i), vendor, product,
                              usb.vendorName(vendor), usb.deviceName(vendor, product));
        }
    }
}
