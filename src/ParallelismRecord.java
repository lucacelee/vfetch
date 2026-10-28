import java.util.ArrayList;
import java.util.HashMap;

public record ParallelismRecord(double baselineTime, int coresAvailable, HashMap<Integer, Double> timeTaken, HashMap<Integer, Double> speedup, HashMap<Integer, Double> amdahlSpeedups, ArrayList<Core> coreLoads, double cpuLoad) {
    // In the HashMap, the first should be the amount of cores, and then an entry consisting of time taken and speedup
}
