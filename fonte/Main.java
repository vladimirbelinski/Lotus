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
        //String intType = "Integer";
        //String doubleType = "double";
        //String stringType = "String";
        String var1 = "myInt";
        String var2 = "myDouble";
        String var3 = "myString";
        //Variable<String, Integer> ger = new Variable<String, Integer>(intType, 7);
        //Variable<String, Double> d = new Variable<String, Double>(doubleType, 11.0);
        //Variable<String, String> str = new Variable<String, String>(stringType, "Oie");

        Variable ger = new Variable(7.2);
        Variable d = new Variable(11.0);

        i.newVar(var1, ger);
        i.newVar(var2, d);
        //i.newVar(var3, str);

        System.out.println(i.var(var1).type() + " " + i.var(var1).value);
        System.out.println(i.var(var2).type() + " " + i.var(var2).value);
        //System.out.println(i.var(var3).type() + " " + i.var(var3).value);

        System.out.println("------------");
        System.out.println("should be: -100");
        System.out.println("main: " + Interpreter.solve("-  300+     200"));
        System.out.println("------------");
        System.out.println("should be: -7");
        System.out.println("main: " + Interpreter.solve("-3-4"));
        System.out.println("------------");
        System.out.println("should be: -2.9998779296875");
        System.out.println("main: " + Interpreter.solve("-3 +4 *2 / ( 1 - 5)^ 2 ^ 3"));
        System.out.println("------------");
        System.out.println("shoudl be: 3.0001220703125");
        System.out.println("main: " + Interpreter.solve("3+4*2/(1-5)^2^3"));
        System.out.println("------------");
        System.out.println("shoud be: 0.01");
        System.out.println("main: " + Interpreter.solve("10^-2"));
        System.out.println("------------");
        System.out.println("shoud be: 0.01");
        System.out.println("main: " + Interpreter.solve("10^(-2)"));
        System.out.println("------------");
        System.out.println("shoud be: -1");
        System.out.println("main: " + Interpreter.solve("(+3-+4)"));
        System.out.println("------------");
        System.out.println("shoud be: -7");
        System.out.println("main: " + Interpreter.solve("(-3+-4)"));
        System.out.println("------------");
        System.out.println("shoud be: 7");
        System.out.println("main: " + Interpreter.solve("3--4"));
        System.out.println("------------");
        System.out.println("shoud be: -9");
        System.out.println("main: " + Interpreter.solve("-1+5 - 5 + 4 *-2"));
        System.out.println("------------");
        System.out.println("shoud be: 0.11111...");
        System.out.println("main: " + Interpreter.solve("3 ^ -2"));
        System.out.println("------------");
        System.out.println("shoud be: 0.0625");
        System.out.println("main: " + Interpreter.solve("+ 2 ^ - 4 + -0 / + 5"));
        System.out.println("------------");
        System.out.println("shoud be: -0.037037...");
        System.out.println("main: " + Interpreter.solve("- 3 ^ - 3 + - 0"));
        System.out.println("------------");
        System.out.println("shoud be: -5");
        System.out.println("main: " + Interpreter.solve("-(3 + 2)"));
        System.out.println("------------");
        System.out.println("shoud be: 5");
        System.out.println("main: " + Interpreter.solve("- 5 * - 2+ -(3 + 2)"));
        System.out.println("------------");
        System.out.println("shoud be: -5");
        System.out.println("main: " + Interpreter.solve("-10 + -(-3 + - 2)"));
        System.out.println("------------");
        System.out.println("shoud be: -5");
        System.out.println("main: " + Interpreter.solve("-       (   3 +2  )"));
        System.out.println("------------");
    }
}
