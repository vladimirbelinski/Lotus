/*******************************************************************************
Name: Main.java
Authors: Acácia dos Campos da Terra- terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com
Version: 1.0
Copyright: Your copyleft.
Description: Main class of Lotus, a programming language based on Java.
*******************************************************************************/

import java.util.*;
import java.io.*;

/* To execute the test, run:
 * javac *.java
 * java Lotus ../exemplos/testing.lt
 */

class Lotus {
    public static Interpreter lotus = new Interpreter();

    public static void main(String[] args) throws Exception {
        File f;
        Scanner sc;
        int max, ind = 0;
        boolean hasParam = true, validParam = true;
        ArrayList<String> input = new ArrayList<String>();

        if (args.length > 0) {
            for (ind = 0; ind < args.length; ind++) {
                if (args[ind].matches(".+\\.lt")) {
                    validParam = true;
                    break;
                }
                else validParam = false;
            }
        }
        else {
            System.out.println("No input file detected.");
            hasParam = false;
        }

        if (hasParam && validParam) {
            f = new File(args[ind]);
            sc = new Scanner(f);

            while (sc.hasNext()) {
                input.add(sc.nextLine().replaceAll("( )+", " "));
                // should we parse and "compile" previously?
            }
            sc.close();

            max = input.size();
            for (int i = 0; i < max; i++) {
                lotus.execute(input.get(i));

                System.out.println("~~~~~~~~~~~~~~~");

                /*System.out.println(input.get(i));
                System.out.println("= " + lotus.solve(input.get(i)));
                if (i < max - 1) System.out.println();*/
            }
        }
        else if (!validParam) {
            System.out.println("Invalid input file.");
        }
        /**********************************************************************/
        StringVar g = new StringVar("Gabriel");
        DoubleVar d = new DoubleVar(7.0);
        IntVar tni = new IntVar(11);
        Variable test = new IntVar(0);

        if (test instanceof IntVar) {
            System.out.println("--> works!");
        }
        else {
            System.out.println("--> this doesn't work -.-");
        }

        System.out.println("---------------------------------------");
        IntVar a, b, c;
        a = new IntVar(7);
        b = new IntVar(11);
        c = new IntVar(a.value + b.value);
        System.out.println(a + ", " + b + ", " + c);
        c.value = (a.value + b.value) * 2;
        System.out.println(a + ", " + b + ", " + c);
        c.value = d.toInt() + 5;
        System.out.println(a + ", " + b + ", " + c);
        System.out.println("---------------------------------------");
    }
}
