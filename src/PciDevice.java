import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PciDevice extends BusDevice{
    static int count = pciInfo.getCount();

    //Constructor for each device
    public PciDevice(int bus, int device, int function, int vendorId, int deviceId, int classCode, String vendorName, String deviceName, String className, String address){
            this.bus = bus;
            this.device = device;
            this.function = function;
            this.vendorId = vendorId;
            this.deviceId = deviceId;
            this.classCode = classCode;
            this.vendorName = vendorName;
            this.deviceName = deviceName;
            this.className = className;
            this.address = address;
            this.typeOfBus = "pci";
    }

    /* creating a method that takes each of the pci devices read by Linux, and
    turns them into objects of the PciDevice class and stores them in
    an ArrayList */
    public static List<PciDevice> mapDevices() throws IOException{
        idLookup pci = idLookup.pci();
        List<PciDevice> list = new ArrayList<>();

        for(int i = 0; i < count; i++){
            String address = pciInfo.getAddress(i);
            int bus = pciInfo.getBus(i);
            int device = pciInfo.getDevice(i);
            int function = pciInfo.getFunction(i);
            int vendorID = pciInfo.getVendorId(i);
            int deviceID = pciInfo.getDeviceId(i);
            int classCode = pciInfo.getClassCode(i);
            String vendorName = pci.vendorName(vendorID);
            String deviceName = pci.deviceName(vendorID, deviceID);
            String className = pci.className(classCode);

            list.add(new PciDevice(bus, device, function, vendorID, deviceID, classCode, vendorName, deviceName, className, address));
        }
        return list;
    }

    //I'm gonna try to sort the list twice, once numerically by bus,device, function
    // and then alphabetically by vendor name. The alphabetical one will be easier cause
    //arrays have a build in sort method. I found a java comparator interface which
    //lets you compare and sort integers with multiple fields (bus, device, function)
    //https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html

    public static void sortByAddress(List<PciDevice> list){
        list.sort(Comparator.comparingInt(PciDevice::getBus).thenComparingInt(PciDevice::getDevice).thenComparingInt(PciDevice::getFunction));
    }

    //this is the alphabetical one

    public static void sortByVendorName(List<PciDevice> list){
        list.sort(Comparator.comparing(PciDevice::getVendorName, String.CASE_INSENSITIVE_ORDER));
    }
    //the String.CASE_INSENSITIVE_ORDER just makes it so that the alphabetical sorting ignores case



    //Summary of devices per vendor

    public static void devicesPerVendor(List<PciDevice> list){
        List<String> vendorNames = new ArrayList<>(); //list to store vendor names
        List<Integer> vendorCount = new ArrayList<>(); //list to store amount of devices per vendor
        //list one will look like [Intel, nvidia...]
        //list two will look like [4, 2...] , meaning intel has 4 pci devices and nvidia has 2.
        //index i of list one will correspond to index i of list two

        for(PciDevice device : list){
            String currentVendor = device.getVendorName();

            int index = vendorNames.indexOf(currentVendor); //searches vendorNames for currentVendor and stores that index
            //if it can't find current vendor, then it returns -1. 

            if(index != -1){ //if the vendor already exists in vendorNames, ie. it had been encountered already
                int count = vendorCount.get(index); //gets the number of devices that currentVendor has
                vendorCount.set(index, count + 1); //increments the counter by 1
            }
            else{ //if the vendor isnt yet in VendorNames ie. this vendor is being encountered for the first time
                vendorNames.add(currentVendor);
                vendorCount.add(1);
            }
        }

    System.out.println("\n--- PCI Summary: Devices Per Vendor ---");
    for (int i = 0; i < vendorNames.size(); i++) {
        String name = vendorNames.get(i);
        int count = vendorCount.get(i);
        System.out.printf("  %-35s : %d devices%n", name, count);
    }

}
}