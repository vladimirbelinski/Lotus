import java.io.*;
import java.util.*;
import java.util.regex.*;

class Expression {
    public String value;
    private static final Map<String, Integer> precedence = mapPrecedence();

    public Expression(String value) throws LotusException {
        Matcher stringMatcher = Interpreter.strPattern.matcher(value);

        if (stringMatcher.find()) {
            this.value = value.trim();
        }
        else {
            this.value = value.replaceAll("", " ").replaceAll("( )+", " ").trim();
            this.fixSignals();
            this.fixOperands();
            this.fixSpaces();
        }
    }

    public String toString() {
        return this.value;
    }

    private void fixSignals() {
        String aux;
        do {
            aux = this.value.replaceAll("\\+( )+\\-|\\-( )+\\+", "-").replaceAll("\\-( )+\\-", "+").replaceAll("\\+( )+\\+", "+");

            if (!this.value.equals(aux)) {
                this.value = aux;
                aux = "";
            }
        } while (aux.isEmpty());
    }

    private void fixOperands() {
        this.value = this.value.replaceAll("\\| \\|", "\\|\\|");
        this.value = this.value.replaceAll("\\& \\&", "\\&\\&");

        this.value = this.value.replaceAll("\\< \\=", "\\<\\=");
        this.value = this.value.replaceAll("\\= \\=", "\\=\\=");
        this.value = this.value.replaceAll("\\> \\=", "\\>\\=");
        this.value = this.value.replaceAll("\\! \\=", "\\!\\=");
    }

    private void fixSpaces() {
        int i, max;
        boolean next;
        String t = new String("");
        Matcher wholeOpMatcher, numBuildMatcher;
        String[] tokens = this.value.split(" ");
        ArrayList<String> ts = new ArrayList<String>();

        // tratar strings aqui!

        for (i = 0; i < tokens.length; i++) {
            wholeOpMatcher = Interpreter.wholeOpPattern.matcher(tokens[i]);

            if (isSignal(tokens, i)) {
                t = tokens[i];
            }
            // if it's not a signal, it's an operation.
            // So we just add it to the ouput
            else if (wholeOpMatcher.matches()) {
                ts.add(tokens[i]);
            }
            else {
                // building a number/variable...
                numBuildMatcher = Interpreter.numBuildPattern.matcher(tokens[i]);
                while (i < tokens.length && numBuildMatcher.matches()) {
                    t += tokens[i];
                    i++;

                    if (i < tokens.length) {
                        numBuildMatcher = Interpreter.numBuildPattern.matcher(tokens[i]);
                    }
                }
                if (!t.isEmpty()) {
                    ts.add(t);
                    t = "";
                    i--;
                }
            }
        }

        // building the resulting string from the ArrayList
        t = "";
        max = ts.size();
        for (i = 0; i < max; i++) {
            t += ts.get(i);
            if (i < max - 1) t += " ";
        }

        this.value = t;
    }

    private static boolean isSignal(String[] tokens, int i) {
        boolean result = false;
        Matcher numBuildMatcher, signMatcher, opBeforeMatcher;

        // (if the current token is + or -) && (the next token is a
        // number) && (either we are looking at the first token ||
        // we are looking at a token that comes right after a '(' ||
        // we are looking at a token that comes right after another
        // token that is not a + or -)...
        // then, it's a signal, not an operation :)
        signMatcher = Interpreter.signPattern.matcher(tokens[i]);
        if (signMatcher.matches() && i + 1 < tokens.length) {
            numBuildMatcher = Interpreter.numBuildPattern.matcher(tokens[i + 1]);

            if (numBuildMatcher.matches()) {
                if (i == 0) {
                    result = true;
                }
                else if (i - 1 >= 0) {
                    // as we treated all the cases with duplicated +/- signs,
                    // is there's any operation token right before a +/-, it
                    // is obviously not a + or - and thus this operator is indeed
                    // a number sign. Because if it was a normal operator, the
                    // previous token would be a number.
                    opBeforeMatcher = Interpreter.wholeOpPattern.matcher(tokens[i - 1]);

                    if (opBeforeMatcher.matches()) {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    // our implementation of Dijkstra's Shunting Yard algorithm
	public String toPostfix() throws LotusException {
        int i, pt, ps;
        String rpn = new String("");
        Matcher wholeOpMatcher, parenMatcher;
        String[] tokens = this.value.split(" ");
        Stack<String> op = new Stack<String>();

        for (String t: tokens) {
            wholeOpMatcher = Interpreter.wholeOpPattern.matcher(t);

            if (t.equals("(")) {
                op.push(t);
            }
            else if (t.equals(")")) {
                // pop everything until you find the '('
                while (!op.peek().equals("(")) {
                    rpn += op.pop() + " ";
                }

                op.pop(); // discard the "("
            }
            else if (wholeOpMatcher.matches()) {
                while (!op.isEmpty()) {

					pt = precedence.get(t);
                    ps = precedence.get(op.peek());

                    if ((!t.equals("^") && pt <= ps) || (t.equals("^") && pt < ps)) {
                        rpn += op.pop() + " ";
                    }
                    else break;
                }

                op.push(t);
            }
            else {
                rpn += t + " ";
            }
        }

        while (!op.isEmpty()) {
            parenMatcher = Interpreter.parenPattern.matcher(op.peek());

            if (parenMatcher.matches()) {
                throw new LotusException("missingParen", this.value);
            }

            rpn += op.pop() + " ";
        }

        return rpn;
    }

    private static Map<String, Integer> mapPrecedence() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("^", 3);

        result.put("*", 2);
        result.put("/", 2);
        result.put("%", 2);

        result.put("+", 1);
        result.put("-", 1);

        result.put("<", 0);
        result.put(">", 0);
        result.put("<=", 0);
        result.put("==", 0);
        result.put(">=", 0);
        result.put("!=", 0);

        result.put("!", 0);
        result.put("&&", 0);
        result.put("||", 0);

        result.put("(", -1);
        result.put(")", -1);

        return Collections.unmodifiableMap(result);
    }
}
