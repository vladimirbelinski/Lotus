import java.util.*;

class Interpreter {
	HashMap<String, Variable> vars;

	public Interpreter() {
		vars = new HashMap<String, Variable>();
	}

	public void newVar(String n, Variable v) {
		this.vars.put(n, v);
	}

	public Variable getVar(String n) {
		return this.vars.get(n);
	}

	/* ---------------------------------------------------------------------- */

	private void let(String line) {
		String[] decl = Variable.fix(line);
		int i = decl.length - 1;
		String type = decl[i];
		Variable v = null;

		while (i--) {
			switch (type) {
				case "int":
				v = new Variable<Integer>(0);
				break;

				case "double":
				v = new Variable<Double>(0.0);
				break;

				case "string":
				v = new Variable<String>("");
				break;
			}
			if (v != null) this.newVar(decl[i], v);
			// else throws Exception
		}
	}
	
	public String solve(String exp) {
		int i = 0, offset = 0;
		String tokens = this.infixToPostfix(exp);
		String t[] = tokens.split(" ");
		String front[], back[];
		String num1, op, num2;
		String answ = new String("0");

		while (t.length > 1) {
			// advance until you don't find an operation to perform
			while (!t[i].matches(opRegex)) {
				i++;
			}

			// then, the operation char is at i's position in the array
			op = t[i];
			// as it's postfix, the 2nd operand is right before op
			num2 = t[i - 1];
			/* now, if i > 1, then 1st operand is certainly 2 positions
			 * before op. Else, it doesn't exist lol
			 */
			if (i > 1) num1 = t[i - 2];
			else num1 = "";

			/* As I am overwriting my token vector, I gotta
			 * copy the parts that I'm not currently working with.
			 * if i == 1, then I operated on it's initial part
			 * and I just need to copy the rest of it
			 */
			if (i == 1) t = Arrays.copyOfRange(t, i, t.length);
			else {
				/* If i != 1, I am working somewhere in the middle
				 * of the vector, so I gotta copy the first (frontside)
				 * and the second (backside) parts, with the result
				 * of the current operation in between. Then, if my
				 * first operand doesn't exist, I gotta copy the first
				 * part until this position (i) - 1. If it exists,
				 * until this position - 2. This way I discard
				 * the spots where the current operands were located
				 * and I leave one spot for the result of this operation
				 * in the middle of the new vector :)
				 */
				if (num1.equals("")) offset = i - 1;
				else offset = i - 2;
				front = Arrays.copyOfRange(t, 0, offset);
				back = Arrays.copyOfRange(t, i, t.length);
				t = this.merge(front, back);
			}

			/* calculating where the result will be put
			 * same logic as the else above
			 */
			if (num1.equals("")) i -= 1;
			else i -= 2;

			answ = this.calculate(num1, num2, op);
			t[i] = answ;
		}
		return answ;
	}

	private String calculate(String v1, String v2, String op) {
		boolean intOpns = true;
		Variable opn1, opn2;
		String answ;

		if (v2.matches("[+-]?[0-9]+")) {
			opn2 = new Variable<Integer>(Integer.parseInt(v2));
		}
		else {
			opn2 = new Variable<Double>(Double.parseDouble(v2));
			intOpns = false;
		}

		/* Gotta check if the first operand exists
		 * or parseDouble() freaks out hehe
		 */
		if (v1 != "") {
			if (v1.matches("[+-]?[0-9]+")) {
				opn1 = new Variable<Integer>(Integer.parseInt(v1));
			}
			else {
				opn1 = new Variable<Double>(Double.parseDouble(v1));
				intOpns = false;
			}
		}
		/* If it doesn't and the operation is a '-'
		 * simply return -v2 (it's a number sign).
		 * Else, return v2 to prevent a crash in
		 * the operations below
		 */
		else if (op.equals("-")) return op + v2;
		else return v2;

		switch (op) {
			/* pow() returns double. I will implement
			 * a binary exponentiation later...
			 */
			case "^":
				answ = String.valueOf(Math.pow(opn1.toDouble(), opn2.toDouble()));
				break;

			case "*":
				if (intOpns) answ = String.valueOf(opn1.toInt() * opn2.toInt());
				else answ = String.valueOf(opn1.toDouble() * opn2.toDouble());
				break;

			case "/":
				/* if opn2 is not zero, then I can properly divide...
				 * I think this regex works as it should, identifying
				 * integer zeroes and floating point zeroes, but needs
				 * more testing (or a different, better way lol)
				 */
				if (!opn2.toString().matches("0+(\\.)?0*")) {
					if (intOpns) {
						answ = String.valueOf(opn1.toInt() / opn2.toInt());
					}
					else {
						answ = String.valueOf(opn1.toDouble() / opn2.toDouble());
					}
				}
				else answ = "undefined";
				break;

			case "+":
				if (intOpns) {
					answ = String.valueOf(opn1.toInt() + opn2.toInt());
				}
				else {
					answ = String.valueOf(opn1.toDouble() + opn2.toDouble());
				}
				break;

			case "-":
				if (intOpns) {
					answ = String.valueOf(opn1.toInt() - opn2.toInt());
				}
				else {
					answ = String.valueOf(opn1.toDouble() - opn2.toDouble());
				}
				break;

			default:
				answ = "0";
				break;
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
	 * and that's why it calls the splitExp() monster method ;x
	 */
	private String infixToPostfix(String infix) {
        final String ops = "-+/*^";
        StringBuilder sb = new StringBuilder();
        Stack<Integer> s = new Stack<>();
        String[] tokens = this.splitExp(infix);

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

	/* This is the method that gets a random infix expression
	 * and splits it in the tokens that compose it. This is
	 * called by the infixToPostfix() method, because it needs
	 * an expression that has everything separated by spaces
	 */
	private String[] splitExp(String in) {
		int i = 0, sign = 0;
		String[] t;
        String num = new String("");
        ArrayList<String> tokens = new ArrayList<String>();

        String[] spl = in.split("");
        for (i = 0; i < spl.length; i++) {
			/* If I found a +/- sign and I didn't previously find
			 * a number, I gotta check if its an operation
			 * or is owned by a number
			 */
			if (num.equals("") && spl[i].matches("[+-]")) {
				// to save the current i, as the while will go forward
				// and I need this position inside those ifs
				sign = i;

				while (spl[i + 1].equals(" ")) i++;

				// If I found a digit, then it's not an operation
				if (spl[i + 1].matches("[0-9]") || spl[i + 1].equals(".")) {
					num += spl[sign] + spl[i + 1];
					i++;
				}
				// If I found a '(', it's an operation...
				else if (spl[i + 1].equals("(")) {
					tokens.add(spl[sign]);
				}
			}
			// else, if its a digit or a decimal point
			else if (spl[i].matches("[0-9]") || spl[i].equals(".")) {
				num += spl[i];
			}
			// finally, if its a char that represents an operation
			else if (spl[i].matches(opRegex)) {
				/* If my number is not empty, then I know that
				 * at THIS point, the code is done building it
				 * and I gotta add it to the list of tokens
				 */
	            if (!num.equals("")) {
	                tokens.add(num);
	                num = "";
					tokens.add(spl[i]);
	            }
				/* else, if this char is a '-', I gotta check if
				 * it's indeed an operation or it's a number sign
				 */
				else if (spl[i].equals("-") && i < spl.length - 1 &&
						spl[i + 1].matches("[0-9]"))
				{
					num += spl[i] + spl[i + 1];
					i++;
				}
				// if it enters here, it's a normal operation char
	            else {
					tokens.add(spl[i]);
				}
	        }
        }
		/* I gotta check if I left the for loop with a number
		 * in 'num' string, because I would only add it in the
		 * next iteration, but I have already quit the loop
		 */
        if (!num.equals("")) {
            tokens.add(num);
        }

		/* transform the built list in a normal array of strings
		 * so that infixToPostfix() can use it
		 */
        t = new String[tokens.size()];
        tokens.toArray(t);

		return t;
    }

	static final String opRegex = "\\^|\\*|\\/|\\+|\\-|\\(|\\)";
	/* fpRegex taken from Java documentation */
	static final String Digits     = "(\\p{Digit}+)";
	static final String HexDigits  = "(\\p{XDigit}+)";
	// an exponent is 'e' or 'E' followed by an optionally
	// signed decimal integer.
	static final String Exp        = "[eE][+-]?"+Digits;
	static final String fpRegex    =
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
