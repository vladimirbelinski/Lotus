/*
===================================================================================================
Name: Main.java
Authors: Acácia dos Campos da Terra- terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com
Version: 1.0
Copyright: Your copyleft.
Description: Main class of Lotus, a programming language based on Java.
==================================================================================================
*/

class Main {
    public static void main (String[] args){
        Interpreter i = new Interpreter();

        System.out.println("------------------------------");
        System.out.println("should be: -100");
        System.out.println("     answ: " + Interpreter.solve("-  300+     200"));
        System.out.println("------------------------------");
        System.out.println("should be: -7");
        System.out.println("     answ: " + Interpreter.solve("-3-4"));
        System.out.println("------------------------------");
        System.out.println("should be: -2.9998779296875");
        System.out.println("     answ: " + Interpreter.solve("-3 +4 *2 / ( 1 - 5)^ 2 ^ 3"));
        System.out.println("------------------------------");
        System.out.println("should be: 3.0001220703125");
        System.out.println("     answ: " + Interpreter.solve("3+4*2/(1-5)^2^3"));
        System.out.println("------------------------------");
        System.out.println("should be: 0.01");
        System.out.println("     answ: " + Interpreter.solve("10^-2"));
        System.out.println("------------------------------");
        System.out.println("should be: 0.01");
        System.out.println("     answ: " + Interpreter.solve("10^(-2)"));
        System.out.println("------------------------------");
        System.out.println("should be: -1");
        System.out.println("     answ: " + Interpreter.solve("(+3-+4)"));
        System.out.println("------------------------------");
        System.out.println("should be: -7");
        System.out.println("     answ: " + Interpreter.solve("(-3+-4)"));
        System.out.println("------------------------------");
        System.out.println("should be: 7");
        System.out.println("     answ: " + Interpreter.solve("3--4"));
        System.out.println("------------------------------");
        System.out.println("should be: -9");
        System.out.println("     answ: " + Interpreter.solve("-1+5 - 5 + 4 *-2"));
        System.out.println("------------------------------");
        System.out.println("should be: 0.11111...");
        System.out.println("     answ: " + Interpreter.solve("3 ^ -2"));
        System.out.println("------------------------------");
        System.out.println("should be: 0.0625");
        System.out.println("     answ: " + Interpreter.solve("+ 2 ^ - 4 + -0 / + 5"));
        System.out.println("------------------------------");
        System.out.println("should be: -0.037037...");
        System.out.println("     answ: " + Interpreter.solve("- 3 ^ - 3 + - 0"));
        System.out.println("------------------------------");
        System.out.println("should be: -5");
        System.out.println("     answ: " + Interpreter.solve("-(3 + 2)"));
        System.out.println("------------------------------");
        System.out.println("should be: 5");
        System.out.println("     answ: " + Interpreter.solve("- 5 * - 2+ -(3 + 2)"));
        System.out.println("------------------------------");
        System.out.println("should be: -5");
        System.out.println("     answ: " + Interpreter.solve("-10 + -(-3 + - 2)"));
        System.out.println("------------------------------");
        System.out.println("should be: -5");
        System.out.println("     answ: " + Interpreter.solve("-       (   3 +2  )"));
        System.out.println("------------------------------");
        System.out.println("should be: -1.0");
        System.out.println("     answ: " + Interpreter.solve("-.5 * 2"));
        System.out.println("------------------------------");
        System.out.println("should be: -2.0");
        System.out.println("     answ: " + Interpreter.solve("-1. * 2"));
        System.out.println("------------------------------");
        System.out.println("should be: -2.0");
        System.out.println("     answ: " + Interpreter.solve("-   1 . *2  "));
        System.out.println("------------------------------");
        System.out.println("should be: -1.0");
        System.out.println("     answ: " + Interpreter.solve("-.   5*2"));
        System.out.println("------------------------------");
        System.out.println("should be: undefined");
        System.out.println("     answ: " + Interpreter.solve("2/0"));
        System.out.println("------------------------------");
        System.out.println("should be: undefined");
        System.out.println("     answ: " + Interpreter.solve("2 .5  7/ 0     ."));
        System.out.println("------------------------------");
        System.out.println("should be: undefined");
        System.out.println("     answ: " + Interpreter.solve("2 / 0  .  0"));
        System.out.println("------------------------------");
        System.out.println("should be: undefined");
        System.out.println("     answ: " + Interpreter.solve("2 /.   0  "));
        System.out.println("------------------------------");
        System.out.println("should be: 1.57");
        System.out.println("     answ: " + Interpreter.solve("3 .1  4/ 2     ."));
        System.out.println("------------------------------");
        System.out.println("should be: 5");
        System.out.println("     answ: " + Interpreter.solve("1 00/ 2   0"));
        System.out.println("------------------------------");

        Variable g, d, tni;
        g = new Variable<String>("Gabriel");
        d = new Variable<Double>(7.0);
        tni = new Variable<Integer>(11);

        // Gotta be sure that s is a String to do that casting!
        System.out.println(((String)g.value).substring(0, 1));
        // or:
        System.out.println(g.toString().substring(0, 1));

        System.out.println(g);
        System.out.println(d);
        System.out.println(tni);
    }
}
