package util;

import interfaces.Savable;

/*
 A helper class to represent a range of integers
 */
public class Range implements Savable {
    private final long min, max;

    public Range(long min, long max) {
        this.max = max;
        this.min = min;
    }

    public long getMin() { return this.min; }
    public long getMax() { return this.max; }

    public boolean IsInside(long number) {
        return (min <= number && number <= max);
    }

    public boolean valid() { return this.min >= 0 && this.min < this.max; }

    @Override
    public String toString() {
        return "[" + min + ", " + max + "]";
    }

    public static Savable toObject(String line) {
        String[] data = line.split(Config.RANGE_CSV_DELIMITER);
        return new Range(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
    }

    @Override
    public String toFile() {
        return min + Config.RANGE_CSV_DELIMITER + max;
    }
}
