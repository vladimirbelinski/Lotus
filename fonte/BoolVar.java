class BoolVar extends Variable<Boolean> {

    public BoolVar(Boolean value) {
        super(value);
    }

    public void invert() {
        this.value = (!this.value);
    }

    public boolean toBool() {
        return this.value;
    }

    public int toInt() {
        if (this.toBool() == true) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public double toDouble() {
        if (this.toBool() == true) {
            return 1.0;
        }
        else {
            return 0.0;
        }
    }

    public boolean equals(Object value) {
        return this.value.equals(value);
    }
}
