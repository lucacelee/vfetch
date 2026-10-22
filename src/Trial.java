public class Trial{
    public static void main(String[] args) throws Exception{

        //display no. of cores and CPU model using mark's methods.
        System.out.println(cpuInfo.getCount());
        System.out.println(cpuInfo.getModel());

        Trial myTrial = new Trial();
        myTrial.calculateCpuLoad();
    }

    public double calculateCpuLoad() throws Exception {

        /*mark's methods here get the busy and idle time of the
        CPU since boot, so to get the load - we need to measure
        the busy time and idle time one second apart and use
        their differences to find out what % of the CPU was busy
        and what % of the CPU was idle during that second. Then the 
        load is the busy time divided by the total time as a %. I did
        the load per core in a for loop and then the average load at
        the end*/

        int cores = cpuInfo.getCount();

        long[] busy1 = new long[cores];
        long[] idle1 = new long[cores];
        long[] busy2 = new long[cores];
        long[] idle2 = new long[cores];
        double load;
        double averageLoad;
        long busyDiff;
        long idleDiff;
        long totalDiff;
        long totalBusyDiff = 0;
        long totalIdleDiff = 0;



        for(int i = 0; i < cores; i++){
            busy1[i] = cpuInfo.getBusy(i);
            idle1[i] = cpuInfo.getIdle(i);
        }

        Thread.sleep(1000); //Pauses for one second

        for(int i = 0; i < cores; i++){
            busy2[i] = cpuInfo.getBusy(i);
            idle2[i] = cpuInfo.getIdle(i);

            busyDiff = busy2[i] - busy1[i];
            idleDiff = idle2[i] - idle1[i];
            totalDiff = busyDiff + idleDiff;

            load = (busyDiff / totalDiff) * 100;
            System.out.printf("The average cpu load for core %d is %.2f%%n", i, load);


            totalBusyDiff += busyDiff;
            totalIdleDiff += idleDiff;
        }

        long totalOverallDiff = totalBusyDiff + totalIdleDiff;
        averageLoad = ((double) totalBusyDiff / totalOverallDiff) * 100;
        System.out.printf("The average load across all cores is %.2f%%n", averageLoad);
        return averageLoad;
    }
}