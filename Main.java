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
        String intType = "Integer";
        String doubleType = "double";
        String stringType = "String";
        String var1 = "myInt";
        String var2 = "myDouble";
        String var3 = "myString";
        Variable<String, Integer> ger = new Variable<String, Integer>(intType, 7);
        Variable<String, Double> d = new Variable<String, Double>(doubleType, 11.0);
        Variable<String, String> str = new Variable<String, String>(stringType, "Oie");

        i.newVar(var1, ger);
        i.newVar(var2, d);
        i.newVar(var3, str);

        System.out.println(i.var(var1).type() + " " + i.var(var1).value);
        System.out.println(i.var(var2).type() + " " + i.var(var2).value);
        System.out.println(i.var(var3).type() + " " + i.var(var3).value);

        //Interpreter.solveExp("-3 +4 *2 / ( 1 - 5)^ 2 ^ 3");
        Interpreter.solveExp("-  300+     200");
    }
}
