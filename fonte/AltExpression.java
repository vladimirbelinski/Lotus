import java.util.*;
import java.util.regex.*;

class AltExpression {
	public String value;
    private static final Map<String, Integer> precedence = mapPrecedence();

	public AltExpression(String value) throws LotusException {
        this.value = value;
		this.fix();
    }

	public String toString() {
        return this.value;
    }

	private void fix() {
		int index = 0;
		String[] output;
		String aux = this.value, tmp = "";
		TreeMap<Integer, String> tokens = new TreeMap<Integer, String>();
		Matcher notEmptyMatcher, opGroupMatcher, wholeOpMatcher, ufpMatcher, strMatcher, varNameMatcher;

		ufpMatcher = Interpreter.ufpPattern.matcher(aux);
		strMatcher = Interpreter.strPattern.matcher(aux);
		opGroupMatcher = Interpreter.opGroupPattern.matcher(aux);
		wholeOpMatcher = Interpreter.wholeOpPattern.matcher(aux);
		varNameMatcher = Interpreter.varNamePattern.matcher(aux);
		notEmptyMatcher = Interpreter.strNotEmptyPattern.matcher(aux);

		while (notEmptyMatcher.find()) {
			// System.out.println();
			// System.out.println("[" + aux + "]");

			if (opGroupMatcher.find()) {
				tmp = opGroupMatcher.group();
				aux = opGroupMatcher.replaceFirst(this.fixSignals(tmp));
			}
			else if (wholeOpMatcher.find()) {
				tmp = wholeOpMatcher.group();
				index = wholeOpMatcher.start();

				tokens.put(index, tmp);
				aux = wholeOpMatcher.replaceFirst(this.replacement(tmp));
			}
			else if (strMatcher.find()) {
				tmp = strMatcher.group();
				index = strMatcher.start();

				tokens.put(index, tmp);
				aux = strMatcher.replaceFirst(this.replacement(tmp));
			}
			else if (ufpMatcher.find()) {
				tmp = ufpMatcher.group();
				index = ufpMatcher.start();

				tokens.put(index, tmp);
				aux = ufpMatcher.replaceFirst(this.replacement(tmp));
			}
			else if (varNameMatcher.find()) {
				tmp = varNameMatcher.group();
				index = varNameMatcher.start();

				tokens.put(index, tmp);
				aux = varNameMatcher.replaceFirst(this.replacement(tmp));
			}

			// System.out.println("~~~~~ tmp: [" + tmp + "] @ " + index);
			// System.out.println();

			ufpMatcher = Interpreter.ufpPattern.matcher(aux);
			strMatcher = Interpreter.strPattern.matcher(aux);
			opGroupMatcher = Interpreter.opGroupPattern.matcher(aux);
			wholeOpMatcher = Interpreter.wholeOpPattern.matcher(aux);
			varNameMatcher = Interpreter.varNamePattern.matcher(aux);
			notEmptyMatcher = Interpreter.strNotEmptyPattern.matcher(aux);
		}

		output = new String[tokens.size()];
		tokens.values().toArray(output);

		// return output;

		System.out.println(">>>> Tokens:");
		for (String s: output) {
			System.out.println("[" + s + "]");
		}
	}

	private String replacement(String token) {
		int i, max = token.length();
		String repl = "";

		for (i = 0; i < max; i++) {
			repl += " ";
		}

		return repl;
	}

	private String fixSignals(String value) {
        String aux;
        do {
            aux = value.replaceAll("\\+( )*\\-|\\-( )*\\+", "-").replaceAll("\\-( )*\\-", "+").replaceAll("\\+( )*\\+", "+");

            if (!value.equals(aux)) {
                value = aux;
                aux = "";
            }
        } while (aux.isEmpty());

		return value;
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
