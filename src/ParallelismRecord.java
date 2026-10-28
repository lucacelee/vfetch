import java.util.ArrayList;
import java.util.HashMap;

public record ParallelismRecord(double baselineTime, int coresAvailable, HashMap<Integer, Double> timeTaken, HashMap<Integer, Double> speedup, HashMap<Integer, Double> amdahlSpeedups, ArrayList<Core> coreLoads, double cpuLoad) { }
// There are 3 hash maps that let you look up the time taken to execute the task, the speedup and the predicted speedub based on the amount of cores used.