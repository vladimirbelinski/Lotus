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

    public void setValue(Variable v) {
        if (v instanceof IntVar) {
			((IntVar)this).setValue((Integer)v.value);
		}
        else if (v instanceof DoubleVar) {
			((DoubleVar)this).setValue((Double)v.value);
		}
        else if (v instanceof StringVar) {
			((StringVar)this).setValue((String)v.value);
		}
		else {
			System.out.println("Assignment of incompatible types");
		    // throw Exception?
		}
    }

    public abstract int toInt();
    public abstract boolean toBool();
    public abstract double toDouble();
    public abstract boolean equals(Object value);

    public boolean assign(String name, String exp) {
        exp = exp.replace(";", "");

        Expression assign = new Expression(exp);
        Variable result = assign.solve();

        System.out.println("result: " + result);

        return Lotus.lotus.setVar(name, result);
    }

    // remember the Arrays!
    public static String[] fixDecl(String line) {
        // removes all trailing spaces
        line = line.trim();
        if (!line.matches(declRegex)) {
            System.out.println("Syntax error!");
            return null; // throw Exception
        }

        int i;
        String var = new String("");
        String[] t = line.replace("let", "").split("");
        ArrayList<String> tokens = new ArrayList<String>();

        for (i = 0; i < t.length && !t[i].equals(":"); i++) {
            if (t[i].matches("\\w")) {
                var += t[i];
            }
            else if (var != "" && (t[i].equals(" ") || t[i].equals(","))) {
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
            System.out.println("Syntax error!");
            return null; // throw Exception
        }

        t = new String[tokens.size()];
        tokens.toArray(t);

        return t;
    }

    public static final String declRegex = "(let)( )+((\\w)+((,( )*(\\w)+)( )*)*)( )*:( )*(\\w)+;";
    public static final String nameRegex = "(\\w)+";
    public static final String typeRegex = "int|double|string|bool";
}
