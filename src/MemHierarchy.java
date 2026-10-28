
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class MemHierarchy {

    public static void caching() throws IOException {
        System.out.println("----------------------------------Working set table----------------------------------");
        System.out.println("Set size   Stride 1    Stride 2    Stride 4    Stride 8    Stride 16   Stride 32   Stride 64   Stride 128");
        System.out.println(" (KiB)");
        Random random = new Random();
        int[] estimating = new int[1024 * 1024 * 16];
        for (int i = 0; i < estimating.length; i++){
            estimating[i] = random.nextInt();
        }
        //creates an 64Mib array that larger than the L3 cache and fills it with random numbers to prevent optimization
        int trialSize = 10000;
        for(int workingSet = 1024; workingSet <= estimating.length; workingSet *= 2){

            if ((workingSet * 4) /1024 > 10000) {
                System.out.print((workingSet * 4) /1024 + "      ");
            } else if ((workingSet * 4) /1024 > 1000) {
                System.out.print((workingSet * 4) /1024 + "       ");
            } else if ((workingSet * 4) /1024 > 100) {
                System.out.print((workingSet * 4) /1024 + "        ");
            } else if ((workingSet * 4) /1024 > 10) {
                System.out.print((workingSet * 4) /1024 + "         ");
            } else {
                System.out.print((workingSet * 4) /1024 + "          ");
            }
           //Prints the working set sizes in Kib (else if is for formatting the table)

            for (int stride = 1; stride <= 128; stride *= 2){
                 //strides choose how many integers are skipped during the adding process
                long sum = 0;
                long accesses = 0;
                long startTime = System.nanoTime();

                for (int trial = 0; trial < trialSize; trial++) {
                    for (int i = 0; i < workingSet; i+= stride) {
                        sum += estimating[i];
                        accesses++;
                    }
                    //adds all of the random numbers and records the time it takes to do so
                }
                long endTime = System.nanoTime();        
                double timePerAccess = (endTime - startTime) / (double) accesses; //returns nanoseconds per access as an average for all accesses
                if (timePerAccess > 10) {
                    System.out.printf("%.2f ns    ", timePerAccess);
                } else {
                System.out.printf("%.2f  ns    ", timePerAccess);
                }
            }
            if (trialSize > 50){
                 trialSize = Math.max(50, trialSize/2);
                //This just reduces the amount or trials ran so that larger working sets are not doing 1000 trials each
                //otherwise this would lead to later working sets having almost a billion accesses
            }
            System.out.println();
        }
        System.out.println();
        for (int i = 0; i < 4; i++) {
            Path cacheDir = Path.of("/sys/devices/system/cpu/cpu0/cache/index" + i);
            if (Files.exists(cacheDir)){
            String level = Files.readString(cacheDir.resolve("level")).trim();
            String type = Files.readString(cacheDir.resolve("type")).trim();
            String size = Files.readString(cacheDir.resolve("size")).trim();
            String lineSize = Files.readString(cacheDir.resolve("coherency_line_size")).trim();

            System.out.printf("L%s Cache (%s)%n" + "  Cache size      : %s%n" + "  Line Size : %s bytes%n%n", level, type, size, lineSize);
            }  else {
                System.out.println("Unable to read cache info for index" + i);
            }
        } // This returns values for caches L1, L2 and L3 reading sizes and line sizes for comparison sake with the table
    }
}