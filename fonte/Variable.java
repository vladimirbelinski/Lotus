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

    public abstract int toInt();
    public abstract boolean toBool();
    public abstract double toDouble();
    public abstract String toString();

    public String assignment(String line) {
        line = line.replace(";", "");
        Expression assign = new Expression(line);
        String result = assign.solve();
        System.out.println("** [" + assign.value + "]");
        System.out.println("= " + result);
        return result;
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
    public static final String typeRegex = "int|double|string|bool";
}
