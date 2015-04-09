class IntVar extends Variable<Integer> {

    public IntVar(Integer value) {
        super(value);
    }

    public int toInt() {
        return this.value;
    }

    public double toDouble() {
        return this.value.doubleValue();
    }

    public String toString() {
        return this.value.toString();
    }
}
