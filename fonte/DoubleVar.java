class DoubleVar extends Variable<Double> {

    public DoubleVar(Double value) {
        super(value);
    }

    public double toDouble() {
        return this.value;
    }

    public int toInt() {
        return this.value.intValue();
    }

    public boolean toBool() {
        if (this.equals(0.0)) {
            return false;
        }
        else {
            return true;
        }
    }

    public String toString() {
        return this.value.toString();
    }
}
