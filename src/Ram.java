public record Ram(double total, double used, double free) {
    public double usedPercentage() {
        return used / total;
    }
    public double freePercentage() {
        return free / total;
    }
    public double totalPercentage() {
        return usedPercentage() + freePercentage();
    }
}