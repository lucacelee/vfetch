
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class MemHierarchy {
    private static ArrayList<ArrayList<AccessTime>> accessTimeMatrix = new ArrayList<ArrayList<AccessTime>>();
    private static ArrayList<Cache> caches = new ArrayList<Cache>();

    public static void caching() throws IOException {
        vfetch.println("----------------------------------Working set table----------------------------------");
        vfetch.println("Set size   Stride 1    Stride 2    Stride 4    Stride 8    Stride 16   Stride 32   Stride 64   Stride 128  Stride 256");
        vfetch.println(" (KiB)");
        Random random = new Random();
        int[] estimating = new int[1024 * 1024 * 16];
        for (int i = 0; i < estimating.length; i++){
            estimating[i] = random.nextInt();
        }
        //creates an 64MiB array of data and fills it with random numbers to prevent optimization
        int maxTrialSize = 10000000; //Roughly we want about 10 million accesses per trial so this is to prevent it going over constantly 
        long sum = 0;
        for(int workingSet = 1024; workingSet <= estimating.length; workingSet *= 2){

            if ((workingSet * 4) /1024 > 10000) {
                vfetch.print((workingSet * 4) /1024 + "      ");
            } else if ((workingSet * 4) /1024 > 1000) {
                vfetch.print((workingSet * 4) /1024 + "       ");
            } else if ((workingSet * 4) /1024 > 100) {
                vfetch.print((workingSet * 4) /1024 + "        ");
            } else if ((workingSet * 4) /1024 > 10) {
                vfetch.print((workingSet * 4) /1024 + "         ");
            } else {
                vfetch.print((workingSet * 4) /1024 + "          ");
            }
           //Prints the working set sizes in Kib (else if is for formatting the table)
            ArrayList<AccessTime> accessTimes = new ArrayList<>();
            for (int stride = 1; stride <= 256; stride *= 2){
                 //strides choose how many integers are skipped during the adding process
                 // 1 Stride is equal to 4 bytes as we are moving in ints
                 // This also means the max size we will move by is 1024 bytes each stride


                long accesses = 0;
                int accessPerTrial = (workingSet + stride - 1) / stride;
                int currentTrialSize = Math.max(1, maxTrialSize / accessPerTrial );
                // This continues the notion of us wanting roughly 10 million accesses per Trial
               
                for (int i = 0; i < workingSet; i+= stride) {
                        sum += estimating[i];
                    } 
                    // warm up trial to prepare the compiler for the main trial
                    // otherwise it could skew the table results
                
                long startTime = System.nanoTime();
                for (int trial = 0; trial < currentTrialSize; trial++) {
                    for (int i = 0; i < workingSet; i+= stride) {
                        sum += estimating[i];
                        accesses++;
                    }
                }
                long endTime = System.nanoTime(); 
                // adds all of the random numbers and records the time it takes to do so
                // we are looking for large jumps between access times which can indicate line sizes (rows) or cache sizes (columns)       
                double timePerAccess = (endTime - startTime) / (double) accesses; 
                //returns nanoseconds per access as an average for all accesses for a more clearer result
                if (timePerAccess > 10) {
                    vfetch.printf("%.2f ns    ", timePerAccess);
                } else {
                vfetch.printf("%.2f  ns    ", timePerAccess);
                }
                // again just formatting the table if averages are over 10ns
                accessTimes.add(new AccessTime(stride, workingSet, timePerAccess));
            }
            
            vfetch.println();
            
            accessTimeMatrix.add(accessTimes);
        }
        vfetch.println();
        vfetch.println("final sum:" + sum); //small check to make sure sum is used and the compiler is happy
        vfetch.println();
        for (int i = 0; i < 4; i++) {
            Path cacheDir = Path.of("/sys/devices/system/cpu/cpu0/cache/index" + i);
            if (Files.exists(cacheDir)){
            String level = Files.readString(cacheDir.resolve("level")).trim();
            String type = Files.readString(cacheDir.resolve("type")).trim();
            String size = Files.readString(cacheDir.resolve("size")).trim();
            String lineSize = Files.readString(cacheDir.resolve("coherency_line_size")).trim();

            vfetch.printf("L%s Cache (%s)%n" + "  Cache size      : %s%n" + "  Line Size : %s bytes%n%n", level, type, size, lineSize);
            caches.add(new Cache(level, type, size, lineSize));
            }  else {
                vfetch.println("Unable to read cache info for index" + i);
            }
        } // This returns values for caches L1, L2 and L3 reading sizes and line sizes for comparison sake with the table
    }

    public static ArrayList<ArrayList<AccessTime>> getAccessTimeMatrix() {
        return accessTimeMatrix;
    }

    public static ArrayList<Cache> getCaches() {
        return caches;
    }
}