import java.util.*;
import java.io.*;

class Expression {
    public String value;

    public Expression(String value) {
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

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].matches(opRegex)) {
                if (tokens[i].matches("[+-]") &&
                    i - 1 >= 0 && tokens[i - 1].matches(opRegex) &&
                    i + 1 < tokens.length && tokens[i + 1].matches("\\w|\\."))
                {
                    t = tokens[i];
                }
                else {
                    ts.add(tokens[i]);
                }
            }
            else {
                while (i < tokens.length && tokens[i].matches("\\w|\\.")) {
                    t += tokens[i];
                    i++;
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

    public Variable solve() throws LotusException {
        Variable answ = null, num1 = null, num2 = null;
		String tokens = this.infixToPostfix();
		String t[] = tokens.split(" ");
		String front[], back[];
		int i = 0, offset = 0;
		String op;

        System.out.println();
        System.out.println("exp: [" + this.value + "]");
        System.out.println("tokens: [" + tokens + "]");
        System.out.println();

        if (t.length == 1) {
            try {
                answ = this.getOperand(t[0]);
            } catch (LotusException e) {
                throw e;
            }
        }

		while (t.length > 1) {
			// advance until you don't find an operation to perform
			while (i < t.length && !t[i].matches(opRegex)) {
				i++;
			}

			// then, the operation char is at i's position in the array
			op = t[i];
			// as it's postfix, the 2nd operand is right before op
            try {
                num2 = this.getOperand(t[i - 1]);
            } catch (LotusException e) {
                throw e;
            }

			/* now, if i > 1, then 1st operand is certainly 2 positions
			 * before op. Else, it doesn't exist lol
			 */
			if (i > 1) {
                try {
                    num1 = this.getOperand(t[i - 2]);
                } catch (LotusException e) {
                    throw e;
                }
            }
			else {
                num1 = null;
            }

            if (num1 == null && num2 == null) {
                answ = new StringVar("undefined"); // throw Exception?
                break;
            }

            if (num2 instanceof DoubleVar || (num1 != null && num1 instanceof DoubleVar)) {
                answ = new DoubleVar(0.0);
            }
            else {
                answ = new IntVar(0);
            }

			/* As I am overwriting my token vector, I gotta
			 * copy the parts that I'm not currently working with:
			 * the first (frontside) and the second (backside) parts,
             * with the result f the current operation in between.
             * Then, if my first operand doesn't exist, I gotta copy
             * the first part until this position (i) - 1. If it exists,
			 * until this position - 2. This way I discard
			 * the spots where the current operands were located
			 * and I leave one spot for the result of this operation
			 * in the middle of the new vector :)
			 */
			if (num1 == null) offset = i - 1;
			else offset = i - 2;

			front = Arrays.copyOfRange(t, 0, offset);
			back = Arrays.copyOfRange(t, i, t.length);
			t = this.merge(front, back);

			/* calculating where the result will be put
			 * same logic as the else above
			 */
			if (num1 == null) i -= 1;
			else i -= 2;

            try {
			    answ = this.calculate(num1, num2, op);
            } catch (LotusException e) {
                throw e;
            }
			t[i] = answ.toString();
		}

		return answ;
	}

    private Variable getOperand(String t) throws LotusException {
        Variable v = null;

        if (t.matches(intRegex)) {
            v = new IntVar(Integer.parseInt(t));
        }
        else if (t.matches(fpRegex)) {
            v = new DoubleVar(Double.parseDouble(t));
        }
        else if (t.charAt(0) == '-') {
            t = t.replace("-", "");
            if (t.matches(Variable.nameRegex)) {
                v = Lotus.interpreter.getVar(t);
                if (v != null) v.invert();
            }
        }
        else if (t.matches(Variable.nameRegex)){
            v = Lotus.interpreter.getVar(t);
        }
        else {
            throw new LotusException("Unknown symbol " + t);
        }

        return v;
    }

	private Variable calculate(Variable v1, Variable v2, String op) throws LotusException {
		boolean intOpns = true;
		Variable answ = null;

		if (v2 instanceof DoubleVar || (v1 != null && v1 instanceof DoubleVar)) {
			intOpns = false;
		}

		/* If it doesn't and the operation is a '-'
		 * simply return -v2 (it's a number sign).
		 * Else, return v2 to prevent a crash in
		 * the operations below
		 */
		if (v1 == null) {
            if (op.equals("-")) {
                if (intOpns) {
                    answ = new IntVar(0 - v2.toInt());
                }
                else {
                    answ = new DoubleVar(0.0 - v2.toDouble());
                }
            }
            else if (op.equals("+")) {
                answ = v2;
            }
            else if (op.matches(opRegex)) {
                throw new LotusException("Syntax error!");
            }
        }
		else {
    		switch (op) {
    			/* pow() returns double. I will implement
    			 * a binary exponentiation later...
    			 */
    			case "^":
    			answ = new DoubleVar(Math.pow(v1.toDouble(), v2.toDouble()));
    			break;

    			case "*":
    			if (intOpns) answ = new IntVar(v1.toInt() * v2.toInt());
    			else answ = new DoubleVar(v1.toDouble() * v2.toDouble());
    			break;

                case "%":
                if (v2 instanceof IntVar && !v2.equals(0) ||
                    v2 instanceof DoubleVar && !v2.equals(0.0))
                {
                    if (intOpns) answ = new IntVar(v1.toInt() % v2.toInt());
                    else {
                        throw new LotusException("Cannot calculate remainder of a division between non-integer operands");
                    }
                }
                else {
                    throw new LotusException("Division by zero");
                }
                break;

    			case "/":
    			/* if v2 is not zero, then I can properly divide...
    			 * I think this regex works as it should, identifying
    			 * integer zeroes and floating point zeroes, but needs
    			 * more testing (or a different, better way lol)
    			 */
    			if (v2 instanceof IntVar && !v2.equals(0) ||
                    v2 instanceof DoubleVar && !v2.equals(0.0))
                {
    				if (intOpns) {
    					answ = new IntVar(v1.toInt() / v2.toInt());
    				}
    				else {
    					answ = new DoubleVar(v1.toDouble() / v2.toDouble());
    				}
    			}
    			else {
                    throw new LotusException("Division by zero");
                }
    			break;

    			case "+":
    			if (intOpns) {
    				answ = new IntVar(v1.toInt() + v2.toInt());
    			}
    			else {
    				answ = new DoubleVar(v1.toDouble() + v2.toDouble());
    			}
    			break;

    			case "-":
    			if (intOpns) {
    				answ = new IntVar(v1.toInt() - v2.toInt());
    			}
    			else {
    				answ = new DoubleVar(v1.toDouble() - v2.toDouble());
    			}
    			break;

    			default:
                throw new LotusException("Unknown operation " + op);
    		}
        }

		return answ;
	}

	private String[] merge(String[] front, String[] back) {
		int i = 0;
		String[] ret = new String[front.length + back.length];

		for (String s: front) {
			ret[i++] = s;
		}
		for (String s: back) {
			ret[i++] = s;
		}

		return ret;
	}

	/* Dijkstra's Shunting Yard algorithm. Taken from Rosetta Code
	 * It needs an expression with everything separated by spaces
	 * and that's why it calls the fixExp() monster method ;x
	 */
	private String infixToPostfix() {
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

    public static final String opRegex = "\\^|\\*|\\%|\\/|\\+|\\-|\\(|\\)";
    public static final String intRegex = "[+-]?[0-9]+";
    public static final String zeroRegex = "0+(\\.)?0*";
    /* fpRegex taken from Java documentation */
	private static final String Digits     = "(\\p{Digit}+)";
	private static final String HexDigits  = "(\\p{XDigit}+)";
	// an exponent is 'e' or 'E' followed by an optionally
	// signed decimal integer.
	private static final String Exp        = "[eE][+-]?"+Digits;
	public static final String fpRegex    =
		"[+-]?(" + // Optional sign character
		"NaN|" +           // "NaN" string
		"Infinity|" +      // "Infinity" string

		// A decimal floating-point string representing a finite positive
		// number without a leading sign has at most five basic pieces:
		// Digits . Digits ExponentPart FloatTypeSuffix
		//
		// Since this method allows integer-only strings as input
		// in addition to strings of floating-point literals, the
		// two sub-patterns below are simplifications of the grammar
		// productions from section 3.10.2 of
		// The Java™ Language Specification.

		// Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
		"((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

		// . Digits ExponentPart_opt FloatTypeSuffix_opt
		"(\\.("+Digits+")("+Exp+")?)|"+

		// Hexadecimal strings
		"((" +
		// 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
		"(0[xX]" + HexDigits + "(\\.)?)|" +

		// 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
		"(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

		")[pP][+-]?" + Digits + "))" +
		"[fFdD]?))";
}
