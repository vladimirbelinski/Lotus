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

    public void assign(String name, String exp) throws LotusException {
        Variable result = null;
        Expression assign = null;

        if (exp.matches("\\\".+\\\"( )*;")) {
            exp = exp.replaceFirst("\\\"", "");
            exp = exp.replaceFirst("\\\"( )*;", "");

            try {
                Lotus.interpreter.setVar(name, exp);
            } catch (LotusException e) {
                throw e;
            }
        }
        else if (exp.matches("(true|false)( )*;")){
            exp = exp.replaceFirst("( )*;", "");

            try {
                Lotus.interpreter.setVar(name, Boolean.parseBoolean(exp));
            } catch (LotusException e) {
                throw e;
            }
        }
        else {
            assign = new Expression(exp);
            result = assign.solve();

            try {
                Lotus.interpreter.setVar(name, result);
            } catch (LotusException e) {
                throw e;
            }
        }
    }

    // remember the Arrays!
    public static String[] fixDecl(String line) throws LotusException {
        int i;
        String var = new String("");
        String[] t = line.replace("let ", "").replaceAll(" ", "").split("");
        ArrayList<String> tokens = new ArrayList<String>();

        for (i = 0; i < t.length && !t[i].equals(":"); i++) {
            if (var.isEmpty() && t[i].matches("([^\\w]|\\d)")) {
                throw new LotusException("Variable names cannot start with numbers or special characters: '" + t[i] + "'");
            }
            else if (t[i].matches("\\w")) {
                var += t[i];
            }
            else if (!var.isEmpty() && (t[i].equals(" ") || t[i].equals(","))) {
                tokens.add(var);
                var = "";
            }
        }
        // the last var left before ":"
        if (var != "") {
            tokens.add(var);
            var = "";
        }

        // type
        while (i < t.length) {
            if (t[i].matches("\\w")) {
                var += t[i];
            }
            i++;
        }

        if (var.matches(typeRegex)) {
            tokens.add(var);
        }
        else {
            throw new LotusException("Syntax error!");
        }

        t = new String[tokens.size()];
        tokens.toArray(t);

        return t;
    }

    public static final String declRegex = "(let)( )+((\\w)+((,( )*(\\w)+)( )*)*)( )*:( )*(\\w)+;";
    // public static final String nameRegex = "(\\w)+";
    public static final String nameRegex = "(?!\\d)\\w+";
    public static final String typeRegex = "int|double|string|bool";
}
