class Variable<T> {
    public T value;

    public Variable(T value) {
        this.value = value;
    }

    public Double toDouble() {
        if (this.value instanceof Double) {
            return ((Double)this.value).doubleValue();
        }
        else if (this.value instanceof Integer) {
            return ((Integer)this.value).doubleValue();
        }
        else return 0.0;
    }

    public Integer toInteger() {
        if (this.value instanceof Double) {
            return ((Double)this.value).intValue();
        }
        else if (this.value instanceof Integer) {
            return ((Integer)this.value).intValue();
        }
        else return 0;
    }

    public String toString() {
        return (String)this.value;
    }
}
