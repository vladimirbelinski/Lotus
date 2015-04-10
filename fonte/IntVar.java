class IntVar extends Variable<Integer> {

    public IntVar(Integer value) {
        super(value);
    }

    public int toInt() {
        return this.value;
    }

    public boolean toBool() {
        if (this.value == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    public double toDouble() {
        return this.value.doubleValue();
    }

    public String toString() {
        return this.value.toString();
    }

    public boolean equals(Object value) {
        return this.value.equals(value);
    }
}
