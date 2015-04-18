import java.util.*;

class OriginalShuntingYard {

    public static void main(String[] args) {
        // String infix = "3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3";
        String infix = "- 300 + 200";
        System.out.printf("infix:   %s%n", infix);
        System.out.printf("postfix: %s%n", infixToPostfix(infix));
    }

    /* Dijkstra's Shunting Yard algorithm. Taken from Rosetta Code
	 * It needs an expression with everything separated by spaces
	 * and that's why it calls the fixExp() monster method ;x
	 */
	static String infixToPostfix(String exp) {
        final String ops = "-+/%*^";
        StringBuilder sb = new StringBuilder();
        Stack<Integer> s = new Stack<>();
        String[] tokens = exp.split(" ");

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
