public class Trial{

    private int cores = cpuInfo.getCount();
    private long[] busy1 = new long[cores];
    private long[] idle1 = new long[cores];
    private long[] busy2 = new long[cores];
    private long[] idle2 = new long[cores];
    private long totalBusyDiff = 0;
    private long totalIdleDiff = 0;

    public static void main(String[] args) throws Exception{

        //display no. of cores and CPU model using mark's methods.
        System.out.println(cpuInfo.getCount());
        System.out.println(cpuInfo.getModel());

        // Create a new trial 
        Trial myTrial = new Trial(1000);

        System.out.printf("The average load across all cores is %.2f%%%n", myTrial.getCpuLoad());
        System.out.printf("Core 1: %.2f%%, Core 2: %.2f%%, Core 3: %.2f%%, Core 4: %.2f%%%n", myTrial.getCoreLoad(0), myTrial.getCoreLoad(1), myTrial.getCoreLoad(2), myTrial.getCoreLoad(3));
    }

    Trial (int milliseconds) { // The trial is inside of the constructor, so that it doesn't have to be rerun every time you want to access each core individually

        /* mark's methods here get the busy and idle time of the
        CPU since boot, so to get the load - we need to measure
        the busy time and idle time one second apart and use
        their differences to find out what % of the CPU was busy
        and what % of the CPU was idle during that second. Then the 
        load is the busy time divided by the total time as a %. I did
        the load per core in a for loop and then the average load at
        the end */

        for(int i = 0; i < cores; i++){
            busy1[i] = cpuInfo.getBusy(i);
            idle1[i] = cpuInfo.getIdle(i);
        }

        try {
            Thread.sleep(milliseconds); //Pauses for milliseconds÷1000 seconds
        } catch (Exception e) {
            // Wait and do nothing
        }

        for(int i = 0; i < cores; i++){
            busy2[i] = cpuInfo.getBusy(i);
            idle2[i] = cpuInfo.getIdle(i);

            long busyDiff = busy2[i] - busy1[i];
            long idleDiff = idle2[i] - idle1[i];

            totalBusyDiff += busyDiff;
            totalIdleDiff += idleDiff;
        }
    }

    public double getCpuLoad() throws Exception {
        long totalOverallDiff = totalBusyDiff + totalIdleDiff;
        double averageLoad = ((double) totalBusyDiff / totalOverallDiff) * 100;
        return averageLoad;
    }

    public double getCoreLoad (int core) {
        if (core < 0 || core >= cores) {
            return -1.0;
        }
        long busyDiff = busy2[core] - busy1[core];
        long idleDiff = idle2[core] - idle1[core];
        long totalDiff = busyDiff + idleDiff;
        double averageLoad = ((double) busyDiff / totalDiff) * 100;
        return averageLoad;
    }
}