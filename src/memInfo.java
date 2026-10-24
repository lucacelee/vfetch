import java.nio.file.Path;

/**
 * Information about memory, read from /proc/meminfo. All sizes are in GiB
 * (1 GiB = 1048576 KiB), which is the unit Linux uses in that file.
 */
public class memInfo
{
    private static final Path MEMINFO = sysFile.path("/proc/meminfo");

    /** Total installed memory in GiB, or -1 if it cannot be read. */
    public static double getTotal()
    {
       long total = field("MemTotal");
       if (total < 0) {
           return -1;
       } else {
        return (double) total / 1048576;
       }
    }

    /** Memory that programs could use right now (free plus easily reclaimed), in GiB. */
    public static double getAvailable()
    {
         long available = field("MemAvailable");
       if (available < 0) {
           return -1;
       } else {
        return (double) available / 1048576;
       }
    }

    /** Memory in use in GiB: total minus available. */
    public static double getUsed()
    {
        double total = getTotal();
        double available = getAvailable();
        if (total == -1 || available == -1) {
            return -1;
        }
        double used = total - available;
        return used;
    }

    /** The value in GiB of a named line of /proc/meminfo such as "SwapTotal", or -1. */
    public static long field(String name)
    {
        for (String line : sysFile.lines(MEMINFO)) {
            if (line.startsWith(name + ":")) {
                String[] part = line.substring(name.length() + 1).strip().split("\\s+");
                try {
                    return Long.parseLong(part[0]);
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }
    /** Gets both % used and % unused for memory */
    public static void displayPercentage()
    {
        double total = getTotal();
        double used = getUsed();
        if (total > 0) {
            double usedPercent = (double) used / total * 100;
            double unusedPercent = 100 - usedPercent;
            System.out.printf("Your current RAM usage is: %.2f%%%n", usedPercent);
            System.out.printf("You currently have %.2f%% of RAM free %n", unusedPercent);
            if (usedPercent >= 95) {
                System.out.println("WARNING: You are using an extremely high amount of RAM, consider upgrading or closing applications");
            } else  if (usedPercent >= 85) {
                System.out.println("You are using a high amount of RAM, this may cause some performance issues");
            } else if (usedPercent >= 60) {
                System.out.println("You are using a moderate amount of RAM");
            } else {
                System.out.println("You have plenty of RAM available");
            }

        } else {
            System.out.println("Error: Unable to read RAM information");
        }
    }
}
