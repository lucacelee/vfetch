public abstract class BusDevice {
    
    // Declare attributes

    protected int bus;
    protected int device;
    protected int function;
    protected int vendorId;
    protected int deviceId;
    protected int classCode;
    protected String vendorName;
    protected String deviceName;
    protected String className;
    protected String address;
    protected static int count;
    protected BusType typeOfBus;

    public BusType busType() {
        return typeOfBus;
    }

    // Getters for address and numerical IDs - these methods come from pciInfo.java

    public String getAddress() {
        return address; 
    }
    public int getBus() { 
        return bus; 
    }
    public int getDevice() {
        return device; 
    }
    public int getFunction() {
        return function; 
    }
    public int getVendorId() {
        return vendorId; 
    }
    public int getDeviceId() {
        return deviceId; 
    }
    public int getClassCode() {
        return classCode; 
    }

    // Getters for names, these come from idLookup.java.

    public String getVendorName() {
        return vendorName; 
    }
    public String getDeviceName() {
        return deviceName; 
    }
    public String getClassName() {
        return className; 
    }
}
