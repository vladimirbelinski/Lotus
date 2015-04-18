import java.io.*;
import java.util.*;
import java.util.regex.*;

class Expression {
    public String value;

    public Expression(String value) throws LotusException {
        if (value.contains("\"")) {
            throw new LotusException("syntaxError", value);
        }
        this.value = value.replaceAll("", " ").replaceAll("( )+", " ").trim();
        this.fixSignals();
        this.fixSpaces();
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

    private void fixSpaces() {
        int max;
        String t = new String("");
        String[] tokens = this.value.split(" ");
        ArrayList<String> ts = new ArrayList<String>();
        Pattern helperPattern = Pattern.compile("\\w|\\.");
        Matcher wholeOpMatcher, prevWholeOpMatcher, signMatcher, helperMatcher;

        for (int i = 0; i < tokens.length; i++) {
            wholeOpMatcher = Interpreter.wholeOpPattern.matcher(tokens[i]);
            signMatcher = Interpreter.signPattern.matcher(tokens[i]);

            if (i == 0 && signMatcher.matches()) {
                t = tokens[i];
            }
            else if (wholeOpMatcher.matches()) {
                if (i - 1 >= 0 && i + 1 < tokens.length) {
                    signMatcher = Interpreter.signPattern.matcher(tokens[i]);
                    prevWholeOpMatcher = Interpreter.wholeOpPattern.matcher(tokens[i - 1]);
                    helperMatcher = helperPattern.matcher(tokens[i + 1]);

                    if (signMatcher.matches() && prevWholeOpMatcher.matches() && helperMatcher.matches()) {
                        t = tokens[i];
                    }
                    else {
                        ts.add(tokens[i]);
                    }
                }
                else {
                    ts.add(tokens[i]);
                }
            }
            else {
                helperMatcher = helperPattern.matcher(tokens[i]);
                while (i < tokens.length && helperMatcher.matches()) {
                    t += tokens[i];
                    i++;
                    helperMatcher = helperPattern.matcher(tokens[i]);
                }
                if (!t.isEmpty()) {
                    ts.add(t);
                    t = "";
                    i--;
                }
            }
        }

        t = "";
        max = ts.size();
        for (int i = 0; i < max; i++) {
            t += ts.get(i);
            if (i < max - 1) t += " ";
        }

        this.value = t;
    }

    public String toString() {
        return this.value;
    }

    /* Dijkstra's Shunting Yard algorithm. Taken from Rosetta Code
	 * It needs an expression with everything separated by spaces
	 * and that's why it calls the fixExp() monster method ;x
	 */
	public String infixToPostfix() {
        final String ops = "-+/%*^";
        StringBuilder sb = new StringBuilder();
        Stack<Integer> s = new Stack<>();
        String[] tokens = this.value.split(" ");

        for (String token: tokens) {
            char c = token.charAt(0);
            int idx = ops.indexOf(c);

            // check for operator
            if (idx != -1 && token.length() == 1) {
                if (s.isEmpty()) {
                    s.push(idx);
				}
                else {
                    while (!s.isEmpty()) {
                        int prec2 = s.peek() / 2;
                        int prec1 = idx / 2;
                        if (prec2 > prec1 || (prec2 == prec1 && c != '^'))
                            sb.append(ops.charAt(s.pop())).append(' ');
                        else break;
                    }
                    s.push(idx);
                }
            }
            else if (c == '(') {
                s.push(-2); // -2 stands for '('
            }
            else if (c == ')') {
                // until '(' on stack, pop operators.
                while (s.peek() != -2)
                    sb.append(ops.charAt(s.pop())).append(' ');
                s.pop();
            }
            else {
                sb.append(token).append(' ');
            }
        }
        while (!s.isEmpty()) {
            sb.append(ops.charAt(s.pop())).append(' ');
        }
        return sb.toString();
    }
}
