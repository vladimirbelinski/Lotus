package fonte;
/*******************************************************************************
Name: DoubleVar.java
Authors: Acácia dos Campos da Terra - terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com

Description: Class DoubleVar of Lotus, a programming language based on Java.
             Implements the functionality specified by the Variable class
             for the Double type.
*******************************************************************************/
class DoubleVar extends Variable<Double> {

    public DoubleVar(Double value) {
        super(value);
    }

    public void setValue(Variable other) {
        this.value = other.toDouble();
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

    public Variable plus(Variable other) {
        if (other instanceof DoubleVar) {
            return new DoubleVar(this.value + other.toDouble());
        }
        else if (other instanceof IntVar) {
            return this.plus(other.toDoubleVar());
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().plus(other);
        }
        else {
            return this.toStringVar().plus(other);
        }
    }

    public Variable minus(Variable other) {
        if (other instanceof DoubleVar) {
            return new DoubleVar(this.value - other.toDouble());
        }
        else if (other instanceof IntVar) {
            return this.minus(other.toDoubleVar());
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().minus(other);
        }
        else {
            return this.toStringVar().minus(other);
        }
    }

    public Variable times(Variable other) {
        if (other instanceof DoubleVar) {
            return new DoubleVar(this.value * other.toDouble());
        }
        else if (other instanceof IntVar) {
            return this.times(other.toDoubleVar());
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().times(other);
        }
        else {
            return this.toStringVar().times(other);
        }
    }

    public Variable divided(Variable other) throws LotusException {
        if (other instanceof DoubleVar) {
            if (!other.value.equals(0.0)) {
                return new DoubleVar(this.value / other.toDouble());
            }
            else {
                throw new LotusException("divisionByZero", this + " / " + other);
            }
        }
        else if (other instanceof IntVar) {
            return this.divided(other.toDoubleVar());
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().divided(other);
        }
        else {
            return this.toStringVar().divided(other);
        }
    }

    public Variable mod(Variable other) throws LotusException {
        if (other instanceof DoubleVar) {
            if (!other.value.equals(0.0)) {
                return new DoubleVar(0.0);
            }
            else {
                throw new LotusException("divisionByZero", this + " % " + other);
            }
        }
        else if (other instanceof IntVar) {
            if (!other.equals(0)) {
                return new DoubleVar(this.value % other.toInt());
            }
            else {
                throw new LotusException("divisionByZero", this + " % " + other);
            }
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().divided(other);
        }
        else {
            return this.toStringVar().divided(other);
        }
    }

    public Variable pow(Variable other) {
        if (other instanceof DoubleVar) {
            return new DoubleVar(Math.pow(this.value, other.toDouble()));
        }
        else if (other instanceof IntVar) {
            return this.pow(other.toDoubleVar());
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().pow(other);
        }
        else {
            return this.toStringVar().pow(other);
        }
    }

    public Variable equals(Variable other) {
        if (other instanceof DoubleVar) {
            return new BoolVar(this.value.equals(other.toDouble()));
        }
        else if (other instanceof IntVar) {
            return this.equals(other.toDoubleVar());
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().equals(other);
        }
        else {
            return this.toStringVar().equals(other);
        }
    }

    public Variable lessThan(Variable other) {
        if (other instanceof DoubleVar) {
            return new BoolVar(this.value.compareTo(other.toDouble()) < 0);
        }
        else if (other instanceof IntVar) {
            return this.lessThan(other.toDoubleVar());
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().lessThan(other);
        }
        else {
            return this.toStringVar().lessThan(other);
        }
    }

    public Variable lessEquals(Variable other) {
        if (other instanceof DoubleVar) {
            return this.lessThan(other).or(this.equals(other));
        }
        else if (other instanceof DoubleVar) {
            return this.lessEquals(other.toDoubleVar());
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().lessEquals(other);
        }
        else {
            return this.toStringVar().lessEquals(other);
        }
    }

    public Variable greaterThan(Variable other) {
        if (other instanceof DoubleVar) {
            return new BoolVar(this.value.compareTo(other.toDouble()) > 0);
        }
        else if (other instanceof IntVar) {
            return this.greaterThan(other.toDoubleVar());
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().greaterThan(other);
        }
        else {
            return this.toStringVar().greaterThan(other);
        }
    }

    public Variable greaterEquals(Variable other) {
        if (other instanceof DoubleVar) {
            return this.greaterThan(other).or(this.equals(other));
        }
        else if (other instanceof IntVar) {
            return this.greaterEquals(other.toDoubleVar());
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().greaterEquals(other);
        }
        else {
            return this.toStringVar().greaterEquals(other);
        }
    }
}
