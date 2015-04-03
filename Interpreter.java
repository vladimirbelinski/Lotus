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

	static void getFirstNumber(String s, int index) {
		int max = s.length();
		int i = 0, j = 0;

		while (!s.substring(i, i + 1).matches("\\d|\\.")) {
			i++;
		}
		j = i + 1;
		while (i < max && s.substring(i, j).matches("\\d+|\\.")) {
			j++;
		} j--;

		System.out.println();
		System.out.println("i: " + i + ", j: " + j);
		System.out.println("subs: " + s.substring(i, j));
		s = s.replace(s.substring(i, j), "");
		System.out.println("s: " + s);
	}

	static String getPrevNumber(String s, int index) {
		int i = index, j = 0;

		while (i > 0 && !s.substring(i - 1, i).matches("\\d|\\.")) {
			i--;
		}
		j = i - 1;

		while (j >= 0 && s.substring(j, i).matches("\\d+|\\.")) {
			j--;
		}
		j++;

		return s.substring(j, i);
	}

	static String solve(String exp) {
		int i = 0, offset = 0;

		System.out.println("param exp: " + exp);

		String tokens = infixToPostfix(exp);

		System.out.println(">> tokens: " + tokens);

		String t[] = tokens.split(" ");
		String front[], back[];
		String num1, op, num2;
		String answ = new String("0");

		while (t.length > 1) {
			System.out.println();
			System.out.println("t:");
			printSV(t);
			System.out.println();

			//i = 0;
			while (!t[i].matches(opRegex)) {
				i++;
			}

			op = t[i];
			num2 = t[i - 1];
			if (i > 1) num1 = t[i - 2];
			else num1 = "";
			System.out.println("num1: " + num1);
			System.out.println("op: " + op);
			System.out.println("num2: " + num2);
			System.out.println("> i: " + i);

			if (i == 1) t = Arrays.copyOfRange(t, i, t.length);
			else {
				if (num1.equals("")) offset = i - 1;
				else offset = i - 2;
				front = Arrays.copyOfRange(t, 0, offset);
				back = Arrays.copyOfRange(t, i, t.length);
				t = merge(front, back);
			}

			if (num1.equals("")) i -= 1;
			else i -= 2;

			answ = calculate(num1, num2, op);
			t[i] = answ;
		}
		return answ;
	}

	private static String calculate(String v1, String v2, String op) {
		Double d1;
		Double d2 = Double.parseDouble(v2);
		Double answ;

		if (v1 != "") {
			d1 = Double.parseDouble(v1);
		}
		//else if (op.equals("+")) return v2;
		//else if (op.equals("-")) return op + v2;
		else return v2;

		switch (op) {
			case "^": answ = Math.pow(d1, d2); break;
			case "*": answ = d1 * d2; break;
			case "/": answ = d1 / d2; break;
			case "+": answ = d1 + d2; break;
			case "-": answ = d1 - d2; break;
			default: answ = 0.0; break;
		}
		//System.out.println("calc answ: " + answ);
		return answ.toString();
	}

	static String[] merge(String[] front, String[] back) {
		String[] ret = new String[front.length + back.length];
		int i = 0;
		for (String s: front) {
			ret[i++] = s;
		}
		for (String s: back) {
			ret[i++] = s;
		}

		return ret;
	}

	static void printSV(String[] str) {
		for (String s: str) {
			System.out.print("[" + s + "]");
		} System.out.println();
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
		int i = 0;
        String num = new String("");
        ArrayList<String> tokens = new ArrayList<String>();

        String[] spl = in.split("");
        for (i = 0; i < spl.length; i++) {
			System.out.println("~ spl[i]: " + spl[i]);
			if (num.equals("") && spl[i].equals("-")) {
				if (i < spl.length - 1 && spl[i + 1].matches("[0-9]") &&
					!spl[i + 1].matches("[()]"))
				{
					System.out.println("-num");
					num += spl[i] + spl[i + 1];
					i++;
				}
			}
			else if (spl[i].matches("[0-9]") || spl[i].matches("\\.")) {
				System.out.println("num");
				num += spl[i];
			}
			else if (spl[i].matches(opRegex)) {
	            if (!num.equals("")) {
					System.out.println("!num");
	                tokens.add(num);
	                num = "";
					tokens.add(spl[i]);
	            }
				else if (spl[i].equals("-") && i < spl.length - 1 &&
					spl[i + 1].matches("[0-9]"))
				{
					System.out.println("op -num");
					num += spl[i] + spl[i + 1];
					i++;
				}
	            else {
					System.out.println("op");
					tokens.add(spl[i]);
				}
	        }
        }
        if (!num.equals("")) {
            tokens.add(num);
        }

        String[] t = new String[tokens.size()];
        tokens.toArray(t);

		System.out.println("tokens:");
		printSV(t);

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
