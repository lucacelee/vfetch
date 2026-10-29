public record Core(int core, double load) {

    //This record fulfils the reporting CPU load per core requirement. When we run Trial.java, we will get the load 
    //per core and each logical core and its load will be stored in an instance of this record. 

    @Override
    public final String toString() {
        return String.format("The load across core %d is %.2f%%", core, load);
    }
}