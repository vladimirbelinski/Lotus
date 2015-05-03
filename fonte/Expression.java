/*******************************************************************************
Name: Expression.java
Authors: Acácia dos Campos da Terra - terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com

Description: Class Expression of Lotus, a programming language based on Java.
             Responsible for treating all expressions supported by Lotus.
*******************************************************************************/
import java.util.*;
import java.util.regex.*;

class Expression {
	public String original, value;
	public static final Character SEP = 31; // 31, 96 to test
    private static final Map<String, Integer> precedence = mapPrecedence();

	public Expression(String value) throws LotusException {
		this.original = value;
        this.value = value;
		// System.out.println("Expression 1: " + this.value);
		this.fixSpaces();
		// System.out.println("Expression 2: " + this.value);
		this.fixSignals();
		// System.out.println("Expression 3: " + this.value);
    }

	public String toString() {
        return this.value;
    }

	private void fixSpaces() throws LotusException {
		int index = 0;
		String[] output;
		String aux = this.value, tmp = "", fixed = "";
		TreeMap<Integer, String> tokens = new TreeMap<Integer, String>();
		Matcher notEmptyM, opGroupM, wholeOpM, ufpM, strM, varNameM, invalidFpM;

		invalidFpM = Interpreter.invalidFpP.matcher(aux);
		if (invalidFpM.find()) {
			throw new LotusException("invalidExp", aux);
		}

		ufpM = Interpreter.jufpP.matcher(aux);
		strM = Interpreter.strP.matcher(aux);
		opGroupM = Interpreter.opGroupP.matcher(aux);
		wholeOpM = Interpreter.wholeOpP.matcher(aux);
		varNameM = Interpreter.varNameP.matcher(aux);
		notEmptyM = Interpreter.strNotEmptyP.matcher(aux);

		while (notEmptyM.find()) {

			if (opGroupM.find()) {
				tmp = opGroupM.group();
				aux = opGroupM.replaceFirst(this.fixRepSign(tmp));
			}
			else if (wholeOpM.find()) {
				tmp = wholeOpM.group();
				index = wholeOpM.start();

				tokens.put(index, tmp);
				aux = wholeOpM.replaceFirst(this.spacenize(tmp));
			}
			else if (strM.find()) {
				tmp = strM.group();
				index = strM.start();

				tokens.put(index, tmp);
				aux = strM.replaceFirst(this.spacenize(tmp));
			}
			else if (ufpM.find()) {
				tmp = ufpM.group();
				index = ufpM.start();

				tokens.put(index, tmp);
				aux = ufpM.replaceFirst(this.spacenize(tmp));
			}
			else if (varNameM.find()) {
				tmp = varNameM.group();
				index = varNameM.start();

				tokens.put(index, tmp);
				aux = varNameM.replaceFirst(this.spacenize(tmp));
			}
			else {
				throw new LotusException("invalidExp", this.value);
			}

			ufpM = Interpreter.jufpP.matcher(aux);
			strM = Interpreter.strP.matcher(aux);
			opGroupM = Interpreter.opGroupP.matcher(aux);
			wholeOpM = Interpreter.wholeOpP.matcher(aux);
			varNameM = Interpreter.varNameP.matcher(aux);
			notEmptyM = Interpreter.strNotEmptyP.matcher(aux);
		}

		output = new String[tokens.size()];
		tokens.values().toArray(output);

		for (String s: output) {
			fixed += s + SEP;
		}

		this.value = fixed;
	}

	public String spacenize(String token) {
		int i, max = token.length();
		String repl = "";

		for (i = 0; i < max; i++) {
			repl += " ";
		}

		return repl;
	}

	private String fixRepSign(String value) {
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

	private void fixSignals() {
        int i, max;
        boolean next;
        String t = new String("");
        Matcher wholeOpM, numBuildM;
        ArrayList<String> ts = new ArrayList<String>();
        String[] tokens = this.value.split(SEP.toString());

        for (i = 0; i < tokens.length; i++) {
            wholeOpM = Interpreter.wholeOpP.matcher(tokens[i]);

            if (this.isSignal(tokens, i)) {
				ts.add(tokens[i] + tokens[i + 1]);
				i++;
            }
            else {
                ts.add(tokens[i]);
            }
        }

        // building the resulting string from the ArrayList
        t = "";
        max = ts.size();
        for (i = 0; i < max; i++) {
            t += ts.get(i);
            if (i < max - 1) t += SEP;
        }

        this.value = t;
    }

	private boolean isSignal(String[] tokens, int i) {
        boolean result = false;
        Matcher numBuildM, signM, opBeforeM;

        // (if the current token is + or -) && (the next token is a
        // number) && (either we are looking at the first token ||
        // we are looking at a token that comes right after a '(' ||
        // we are looking at a token that comes right after another
        // token that is not a + or -)...
        // then, it's a signal, not an operation :)
        signM = Interpreter.signP.matcher(tokens[i]);
        if (signM.matches() && i + 1 < tokens.length) {
            numBuildM = Interpreter.ufpP.matcher(tokens[i + 1]);

            if (numBuildM.matches()) {
                if (i == 0) {
					if (i + 2 < tokens.length && tokens[i + 2].equals("^")) {
						result = false;
					}
					else {
						result = true;
					}
                }
                else if (i - 1 >= 0) {
                    // as we treated all the cases with duplicated +/- signs,
                    // is there's any operation token right before a +/-, it
                    // is obviously not a + or - and thus this operator is indeed
                    // a number sign. Because if it was a normal operator, the
                    // previous token would be a number.
                    opBeforeM = Interpreter.wholeOpP.matcher(tokens[i - 1]);

                    if (opBeforeM.matches()) {
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
        Matcher wholeOpM, parenM;
        String[] tokens = this.value.split(SEP.toString());
        Stack<String> op = new Stack<String>();

        for (String t: tokens) {
            wholeOpM = Interpreter.wholeOpP.matcher(t);

            if (t.equals("(")) {
                op.push(t);
            }
            else if (t.equals(")")) {
                // pop everything until you find the '('
                while (!op.peek().equals("(")) {
                    rpn += op.pop() + SEP;
                }

                op.pop(); // discard the "("
            }
            else if (wholeOpM.matches()) {
                while (!op.isEmpty()) {

					pt = precedence.get(t);
                    ps = precedence.get(op.peek());

                    if ((!t.equals("^") && pt <= ps) || (t.equals("^") && pt < ps)) {
                        rpn += op.pop() + SEP;
                    }
                    else break;
                }

                op.push(t);
            }
            else {
                rpn += t + SEP;
            }
        }

        while (!op.isEmpty()) {
            parenM = Interpreter.parenP.matcher(op.peek());

            if (parenM.matches()) {
                throw new LotusException("missingParen", this.original);
            }

            rpn += op.pop() + SEP;
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
