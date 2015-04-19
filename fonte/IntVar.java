class IntVar extends Variable<Integer> {

    public IntVar(Integer value) {
        super(value);
    }

    public Variable inverted() {
        return new IntVar(-this.value);
    }

    public Integer toInt() {
        return this.value;
    }

    public Boolean toBool() {
        if (this.value.equals(0)) {
            return new Boolean(false);
        }
        else {
            return new Boolean(true);
        }
    }

    public Double toDouble() {
        return new Double(this.value.doubleValue());
    }

    public Variable equals(Variable other) {
        return new BoolVar(this.value.equals(other.toInt()));
    }
}
