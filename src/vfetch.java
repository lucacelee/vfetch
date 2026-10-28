
import java.util.ArrayList;
import java.util.List;

/**
 * A short example that shows how to call each provided class. It prints raw
 * numbers and a few names. It is NOT a solution to the assignment: you still
 * have to measure load, store the devices, sort them, analyse them and present
 * the results.
 *
 * Build:  javac *.java
 * Run:    java vfetch       (run it from the folder that contains the ids folder)
 */
public class vfetch {
    private static boolean writeToFile = false;
    private static boolean printToConsole = true;
    private static boolean printHelpMessage = false;

    private static String fileName = "Hardware Details.html";
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {          // This helps determine whether we want to print out a file or not
            processArguments(args);
        }

        if (printHelpMessage) {
            printHelp();
            return;
        }
        
        //Requirement 1: Processor

        println();
        println("model:  " + CpuInfo.getModel());
        println("cores:  " + CpuInfo.getCount());
        println();

        Trial trial = new Trial(1000);
        printf("The average load across all cores is %.2f%%%n", trial.getCpuLoad());

        List<Core> cores = new ArrayList<Core>();       // This unifies the code between the CLI and HTML formatter
        for(int i = 0; i < CpuInfo.getCount(); i++){
            Core c = new Core(i, trial.getCoreLoad(i));
            println(c.toString());
            cores.add(c);
        }

        //Requirement 2: Memory
        
        println();
        println("------RAM INFORMATION------");
        if (memInfo.getTotal() == -1 || memInfo.getUsed() == -1) {
            println("ERROR: Unable to read RAM information from your machine");
        } else {
            printf("Your machine currently has %.2f GiB of RAM in total%n", memInfo.getTotal());
            printf("Your are now currently using %.2f GiB of RAM%n", memInfo.getUsed()); 
        }
        memInfo.displayPercentage();
        println();

        //Requirement 3 + 6: Pci devices and analysis

        List<PciDevice> pciDevices = PciDevice.mapDevices();
        printf("PCI DEVICES LIST (Total: %d devices)%n", pciInfo.getCount());

        //PCI devices, sorted by numerical address order bus, device, function

        PciDevice.sortByAddress(pciDevices);
        println("----SORTED NUMERICALLY BY ADDRESS - BUS, DEVICE, FUNCTION-----");
        printf("%-20s %-35s%n", "Device Address", "Device Name");
        println("-------------------------------------------------------------");
        for(PciDevice dev : pciDevices){
            printf("%-20s %-35s%n", dev.getAddress(), dev.getDeviceName());
        }
        println();

        //PCI devices sorted in alphabetical order in reference to vendor name
        PciDevice.sortByVendorName(pciDevices);
        println("------SORTED ALPHABETICALLY BY VENDOR NAME-----");
        printf("%-35s %-20s%n", "Vendor Name", "Device Address");
        println("-------------------------------------------------------------");
        for(PciDevice dev : pciDevices){
            printf("%-35s %-20s%n", dev.getVendorName(), dev.getAddress());
        }

        println();
        //Summary: I chose the example one of listing the number of devices per vendor
        println("----SUMMARY OF NUMBER OF DEVICES PER VENDOR----\n");
        PciDevice.devicesPerVendor(pciDevices);
        println();
        //I kept the print statement for this one in the PciDevices class file because it was 
        //so much easier because all the variables were local to that class

        // idLookup pci = idLookup.pci();
        // println("PCI devices: " + pciInfo.getCount());
        // for (int i = 0; i < pciInfo.getCount(); i++) {
        //     int vendor = pciInfo.getVendorId(i);
        //     int device = pciInfo.getDeviceId(i);
        //     printf("  %s  %04x:%04x  %s %s%n", pciInfo.getAddress(i), vendor, device,
        //                       pci.vendorName(vendor), pci.deviceName(vendor, device));
        // }

        //Requirement 4: USB devices
        ArrayList<ArrayList<String>> USBDevices = new ArrayList<>();

        idLookup usb = idLookup.usb();
        System.out.println("USB devices: " + usbInfo.getCount());                     
        for (int i = 0; i < usbInfo.getCount(); i++) {
            int vendor = usbInfo.getVendorId(i);
            int product = usbInfo.getProductId(i);
            System.out.printf("  Bus %03d Device %03d: ID %04x:%04x  %s %s%n", usbInfo.getBusNumber(i),
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
        ArrayList<USBDevice> usbDevices = USBDevice.mapDevices();
        println();

        //Requirement 5: Load and parellelism
        println("-------------LOAD AND PARALLELISM--------------");
        double baselineTime = Parallelism.runMatrixMultiplication(1000, 1);
        double parallelTime = Parallelism.runMatrixMultiplication(1000, 4);
        double f = Parallelism.calculateF(baselineTime, parallelTime, 4);

        int coresAvailable = CpuInfo.getCount();
        println("Number of cores available: " + coresAvailable);

        int[] threadCounts = {1, 2, 4, coresAvailable};
         printf("%-25s | %-20s | %-20s | %-20s%n", "Number of threads", "Execution Time(s)", "Measured Speedup", "Amdahl's Limit");
        
        for (int thread : threadCounts){

        double duration = Parallelism.runMatrixMultiplication(1000, thread);
            if(thread == 1){
                baselineTime = duration;
            }
          
            double speedup = baselineTime / duration;
            double amdahlSpeedup = Parallelism.calculateAmdahlsSpeedup(thread, f);
            printf("%-25d | %-20.4f | %-20.4f | %-20.4f%n", thread, duration, speedup, amdahlSpeedup);
        }

        println("\n--- Sampling Core Load using Trial.java ---");
        Trial liveTrial = new Trial(1000); // Sample over 1000ms
        for (int i = 0; i < coresAvailable; i++) {
            printf("  Core %d Load: %.2f%%%n", i, liveTrial.getCoreLoad(i));
        }
        printf("  Average Load across all cores: %.2f%%%n", liveTrial.getCpuLoad());
        


        if (writeToFile) {
            HtmlFormatter formatter = new HtmlFormatter(
                "System Information Breakdown",
                pciDevices,
                usbDevices,
                trial.getCpuLoad(),
                cores,
                new Ram(memInfo.getTotal(), memInfo.getUsed(), memInfo.getAvailable())
            );
            formatter.formatHTML();
            DocumentWriter.write(fileName, formatter.htmlDocument());
        }
    }

    private static void processArguments(String[] args) {
        String previousCommand = "";
        for (int a = 0; a < args.length; a++) {
            if (args[a].startsWith("-")) {
                if (!args[a].startsWith("--")) {
                    String argument = args[a].substring(1);
                    char[] parameters = argument.toCharArray();
                    for (char p : parameters) {
                        switch (p) {
                            case 'o':
                                writeToFile = true;
                                previousCommand = "output";
                                break;
                            case 's':
                                printToConsole = false;
                                previousCommand = "silence";
                                break;
                            case 'h':
                                printHelpMessage = true;
                                previousCommand = "help";
                                break;
                        }
                    }
                } else {
                    String flag = args[a].substring(2);
                    switch (flag) {
                        case "output":
                            writeToFile = true;
                            previousCommand = "output";
                            break;
                        case "silence":
                            printToConsole = false;
                            previousCommand = "silence";
                            break;
                        case "help":
                            printHelpMessage = true;
                            previousCommand = "help";
                            break;
                    }
                }
            } else {
                if (writeToFile && previousCommand == "output") {
                    fileName = args[a];
                } 
            }
        }
    }

    private static void printHelp() {
        System.out.println("""
                vfetch - a program that gives you your system hardware details
                Usage: vfetch [options] [output_file_name]
                    options:
                        -h --help       Print this message
                        -s --silent     Execute without printing anything
                        -o --output     Create a HTML document, where
                                        [output_file_name] is its name.
                                        By default it's "Hardware Details.html".
                """);
    }

    public static void println(String output) {
        if (!printToConsole) return;
        System.out.println(output);
    }

    public static void println() {
        if (!printToConsole) return;
        System.out.println();
    }

    public static void printf(String formatString, Object... arguments) {
        if (!printToConsole) return;
       System.out.printf(formatString, arguments);
    }
}
