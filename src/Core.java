public record Core(int core, double load) {
    @Override
    public final String toString() {
        return String.format("The load across core %d is %.2f%%", core, load);
    }
}