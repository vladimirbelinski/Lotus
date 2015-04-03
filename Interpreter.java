import java.util.*;

class Interpreter {
	HashMap<String, Variable> vars;

	public Interpreter() {
		vars = new HashMap<String, Variable>();
	}

	public void newVar(String n, Variable v) {
		this.vars.put(n, v);
	}

	public Variable var(String n) {
		return this.vars.get(n);
	}

	/* ---------------------------------------------------------------------- */

	static void getFirstNumber(String s) {
		int max = s.length();
		int i = 0, j = 0;

		while (!s.substring(i, i + 1).matches("\\d|\\.")) {
			i++;
		}
		j = i + 1;
		while (i < max && s.substring(i, j).matches("\\d+|\\.")) {
			j++;
		} j--;

		System.out.println(i + " " + j);
		System.out.println(s.substring(i, j));
		s = s.replace(s.substring(i, j), "");
		System.out.println(s);
	}

	static String getPrevNumber(String s, int index) {
		int i = index, j = 0;

		while (i > 0 && !s.substring(i - 1, i).matches("\\d|\\.")) {
			i--;
		}
		j = i - 1;
		while (j >= 0 && s.substring(j, i).matches("\\d+|\\.")) {
			j--;
		} j++;

		System.out.println(j + " " + i);
		System.out.println(">> " + s.substring(j, i));
		s = s.replace(s.substring(j, i), "");
		System.out.println("** " + s);

		return s.substring(j, i);
	}

	static double solveExp(String exp) {
		int i;
		double answ = 0.0;
		String tokens = infixToPostfix(exp);
		String num1 = new String("");
		String num2 = new String("");

		System.out.println("original: " + tokens);

		i = 0;
		while (!tokens.substring(i, i + 1).matches(opRegex)) {
			i++;
		}

		num1 = getPrevNumber(tokens, i);

		//getFirstNumber(tokens);

		System.out.println(num1);

		return answ;
	}

	static String infixToPostfix(String infix) {
        final String ops = "-+/*^";
        StringBuilder sb = new StringBuilder();
        Stack<Integer> s = new Stack<>();
        String[] tokens = splitExp(infix);

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

    public static String[] splitExp(String in) {
        String num = new String("");
        ArrayList<String> tokens = new ArrayList<String>();

        String[] splaught = in.split("");
        for (String s: splaught) {
            if (s.matches(fpRegex) || s.matches("\\.")) {
                num += s;
            }
            else if (!s.matches(opRegex) && !s.matches(fpRegex) && !num.equals("")) {
                tokens.add(num);
                num = "";
            }
            else if (s.matches(opRegex)) {
                if (!num.equals("")) {
                    tokens.add(num);
                    num = "";
                }
                tokens.add(s);
            }
        }
        if (!num.equals("")) {
            tokens.add(num);
        }

        String[] t = new String[tokens.size()];
        tokens.toArray(t);

        return t;

        /*for (String t: tokens) {
            System.out.println(t);
        }*/
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
