class DoubleVar extends Variable<Double> {

    public DoubleVar(Double value) {
        super(value);
    }

    public Variable inverted() {
        return new DoubleVar(-this.value);
    }

    public Double toDouble() {
        return this.value;
    }

    public Integer toInt() {
        return new Integer(this.value.intValue());
    }

    public Boolean toBool() {
        if (this.equals(0.0)) {
            return new Boolean(false);
        }
        else {
            return new Boolean(true);
        }
    }

    public Variable equals(Variable other) {
        return new BoolVar(this.value.equals(other.toDouble()));
    }
}
