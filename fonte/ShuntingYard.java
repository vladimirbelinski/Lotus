/*******************************************************************************
Name: ShuntingYard.java
Authors: Acácia dos Campos da Terra- terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com

Description: Class ShuntingYard of Lotus, a programming language based on Java.
             Responsible to infix to postfix conversion.
*******************************************************************************/
import java.util.*;

class ShuntingYard {
    public static void main(String[] args) {
        // String infix = "3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3";
        String infix = "- 300 + 200";
        System.out.printf("infix:   %s%n", infix);
        System.out.printf("postfix: %s%n", infixToPostfix(infix));
        System.out.println("------------------------------");
        infix = "( acacia + gabriel + vladimir ) / prog1";
        System.out.printf("infix:   %s%n", infix);
        System.out.printf("postfix: %s%n", infixToPostfix(infix));
    }

    static String infixToPostfix(String infix) {
        int i;
        String[] t = infix.split(" ");
        String rpn = new String(""), op2;
        final String intRegex = "[+-]?[0-9]+";
        final String varNameRegex = "(?!\\d)\\w+";
        Stack<String> operators = new Stack<String>();
        final String opRegex = "\\-|\\+|\\/|\\%|\\*|\\^";

        for (i = 0; i < t.length; i++) {

            if (t[i].matches(intRegex) || t[i].matches(varNameRegex)) { // if it's a number (not only integers)
                rpn += t[i] + " ";
            }
            // else if (t[i].equals("fn")) {// if it's a function call (not "fn"...)
            //     operators.push(t[i]);
            // }
            // else if (t[i].equals(",")) { // fn separators...
            //     while (!operators.isEmpty() && !operators.peek().equals("(")) {
            //         rpn += operators.pop() + " ";
            //     }
            //
            //     if (operators.isEmpty()) {
            //         System.out.println("Misplaced separator or mismatched parenthesis!");
            //     }
            // }
            else if (t[i].matches(opRegex)) {
                while (!operators.isEmpty()) {
                    op2 = operators.peek();

                    if ((!t[i].equals("^") && opRegex.indexOf(t[i]) <= opRegex.indexOf(op2)) || t[i].equals("^") && opRegex.indexOf(t[i]) < opRegex.indexOf(op2)) {
                        rpn += operators.pop() + " ";
                    }
                    else break;
                }

                operators.push(t[i]);
            }
            else if (t[i].equals("(")) {
                operators.push(t[i]);
            }
            else if (t[i].equals(")")) {
                while (!operators.peek().equals("(")) {
                    rpn += operators.pop() + " ";
                }

                operators.pop(); // discard the "("

                // if (operators.peek().equals("fn")) { // if it's a function call (not "fn"...)
                //     rpn += operators.pop() + " ";
                // }
            }
        }

        while (!operators.isEmpty()) {
            if (operators.peek().matches("[()]")) {
                System.out.println("Mismatched parenthesis!");
            }
            rpn += operators.pop() + " ";
        }

        return rpn;
    }
}

//     - If the token is a function token, then push it onto the stack.
//
//     - If the token is a function argument separator (e.g., a comma) {
//         Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue. If no left parentheses are encountered, either the separator was misplaced or parentheses were mismatched.
//     }
//
//     - If the token is an operator, o1, then {
//
//         - while there is an operator token, op2, at the top of the operator stack, and either o1 is left-associative and its precedence is less than or equal to that of op2 or o1 is right associative, and has precedence less than that of op2 {
//
//             then pop op2 off the operator stack, onto the output queue;
//         }
//
//         - push o1 onto the operator stack.
//     }
//
//     - If the token is a left parenthesis, then push it onto the stack.
//
//     - If the token is a right parenthesis {
//
//         - Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue.
//
//         - Pop the left parenthesis from the stack, but not onto the output queue.
//
//         - If the token at the top of the stack is a function token, pop it onto the output queue.
//
//         - If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
//     }
// }
// - When there are no more t to read {
//
//     - While there are still operator t in the stack {
//
//         - If the operator token on the top of the stack is a parenthesis, then there are mismatched parentheses.
//
//         - Pop the operator onto the output queue.
//     }
// }
//
// Done!
// ----------------------------------------------
// While there are t to be read {
//
//     - Read a token.
//     - If the token is a number, then add it to the output queue.
