class DoubleVar extends Variable<Double> {

    public DoubleVar(Double value) {
        super(value);
    }

    public int toInt() {
        return this.value.intValue();
    }

    public double toDouble() {
        return this.value;
    }

    public String toString() {
        return this.value.toString();
    }
}
