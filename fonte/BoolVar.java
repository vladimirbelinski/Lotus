class BoolVar extends Variable<Boolean> {

    public BoolVar(Boolean value) {
        super(value);
    }

    public Variable inverted() {
        return new BoolVar(!this.value);
    }

    public Boolean toBool() {
        return this.value;
    }

    public Integer toInt() {
        if (this.toBool().equals(true)) {
            return new Integer(1);
        }
        else {
            return new Integer(0);
        }
    }

    public Double toDouble() {
        if (this.toBool().equals(true)) {
            return new Double(1.0);
        }
        else {
            return new Double(0.0);
        }
    }

    public Variable equals(Variable other) {
        return new BoolVar(this.value.equals(other.toBool()));
    }
}
