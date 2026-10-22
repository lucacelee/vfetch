public class Trial{

    private int cores = cpuInfo.getCount();
    private long[] busyStarts = new long[cores];
    private long[] idleStarts = new long[cores];
    private long[] busyFinishes = new long[cores];
    private long[] idleFinishes = new long[cores];
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
            busyStarts[i] = cpuInfo.getBusy(i);
            idleStarts[i] = cpuInfo.getIdle(i);
        }

        try {
            Thread.sleep(milliseconds); //Pauses for milliseconds÷1000 seconds
        } catch (Exception e) {
            // Wait and do nothing
        }

        for(int i = 0; i < cores; i++){
            busyFinishes[i] = cpuInfo.getBusy(i);
            idleFinishes[i] = cpuInfo.getIdle(i);

            long busyDiff = busyFinishes[i] - busyStarts[i];
            long idleDiff = idleFinishes[i] - idleStarts[i];

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
        long busyDiff = busyFinishes[core] - busyStarts[core];
        long idleDiff = idleFinishes[core] - idleStarts[core];
        long totalDiff = busyDiff + idleDiff;
        double averageLoad = ((double) busyDiff / totalDiff) * 100;
        return averageLoad;
    }
}