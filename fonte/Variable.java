import java.util.*;

abstract class Variable<T> {
    public T value;

    public Variable(T value) {
        this.value = value;
    }

    public Class type() {
        return this.value.getClass();
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String toString() {
        return this.value.toString();
    }

    public boolean setValue(Variable v) throws LotusException {
        if (v != null) {
            if (v instanceof BoolVar) {
                ((BoolVar)this).setValue((Boolean)v.value);
            }
            else if (v instanceof IntVar) {
    			((IntVar)this).setValue((Integer)v.value);
    		}
            else if (v instanceof DoubleVar) {
    			((DoubleVar)this).setValue((Double)v.value);
    		}
            else if (v instanceof StringVar) {
    			((StringVar)this).setValue((String)v.value);
    		}
    		else {
    			throw new LotusException("Cannot assign a " + v.getClass() + " value to a " + this.getClass() + " variable");
    		}
        }
        else {
            throw new LotusException("Assignment of null");
        }

        return true;
    }

    public abstract void invert();
    public abstract int toInt();
    public abstract boolean toBool();
    public abstract double toDouble();
    public abstract boolean equals(Object value);


    public static final String declRegex = "(let)( )+((\\w)+((,( )*(\\w)+)( )*)*)( )*:( )*(\\w)+;";
    // public static final String nameRegex = "(\\w)+";
    public static final String nameRegex = "(?!\\d)\\w+";
}
