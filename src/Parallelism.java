import java.util.Random;

public class Parallelism{
    private static Trial trial;

    static private void startTrial(int millis) {
        // Run the Trial while the threads are executing, as opposed to after
        trial = new Trial(millis);
    }

    /*This is the CPU heavy workload we agreed on, it has
    two matrices that store longs, multiplies index[x][y] in
    matrix A with index[x][y] in matrix b and then stores
    the result of the multiplication as index[x][y] in matrix C.
    
    when we split this workload into different threads, we will split
    the workload evenly among the threads eg. if the matrix is 1000x1000
    and we have 4 threads, thread 1 will handle rows 0-249, thread 2 will 
    handle rows 250-499, etc. */

    public static void cpuHeavyWorkload(long[][] A, long[][] B, long[][] C, int startRow, int endRow, int matrixSize){
        for(int i = startRow; i < endRow; i++){
            for(int j = 0; j < matrixSize; j++){
                long result = 0;
                for(int k = 0; k < matrixSize; k++){
                    result += (A[i][k] * B[k][j]);
                }
                C[i][j]= result;
            }
        }
    }
    
    public static double runMatrixMultiplication(int matrixSize, int numThreads){
        long[][] A = new long[matrixSize][matrixSize];
        long[][] B = new long[matrixSize][matrixSize];
        long[][] C = new long[matrixSize][matrixSize];

        Random random = new Random();
        for(int i = 0; i < matrixSize; i++){
            for(int j = 0; j < matrixSize; j++){
                A[i][j] = random.nextInt(0, 101);
                B[i][j] = random.nextInt(0,101);
            }
        }

        Thread[] threads = new Thread[numThreads];
        int rowsPerThread = matrixSize / numThreads;

        long startTime = System.nanoTime();
        Thread benchmarkThread = new Thread(() -> startTrial(500)); // Sample over 500ms
        benchmarkThread.start();

        for(int i = 0; i < numThreads; i++){
            int startRow = i * rowsPerThread;
            int endRow = (i + 1) * rowsPerThread;
         
            threads[i] = new Thread(() -> cpuHeavyWorkload(A, B, C, startRow, endRow, matrixSize));
            threads[i].start();
        }


        for(int i = 0; i < numThreads; i++){
            try{
                threads[i].join(); //this waits for each thread to finish before moving on to the next one.
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        try {
            benchmarkThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000000.0; //returns time in seconds.
    }

    //creating a method that calculates f; the fraction of the workload that is serial, 
    //based on the baseline time and the time it takes to run the workload with n threads.
    //Manipulating formula for speedup = 1 / (f + (1-f)/n) gives us f = (n - speedup) / ((n-1)*speedup)

    public static double calculateF(double baselineTime, double parallelTime, int numThreads) {
        double speedup = baselineTime / parallelTime;
        double f = (numThreads - speedup) / ((numThreads - 1) * speedup);
        return f;
    }

    public static double calculateAmdahlsSpeedup(int n, double f){
        return (double) n / (1 + ((n - 1) * f));
    }

    public static Trial trial() {
        return trial;
    }
}