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
    public static void main(String[] args) throws Exception {
        File f;
        Scanner sc;
        int max, ind = 0;
        Interpreter lotus = new Interpreter();
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
                input.add(sc.nextLine());
            }
            sc.close();

            max = input.size();
            for (int i = 0; i < max; i++) {
              lotus.interpret(input.get(i));
              System.out.println(lotus.getVar("x"));
              System.out.println(lotus.getVar("y"));
              System.out.println(lotus.getVar("z"));
              System.out.println(lotus.getVar("k"));
              System.out.println(lotus.getVar("s"));
              System.out.println();
              /*System.out.println(input.get(i));
              System.out.println("= " + lotus.solve(input.get(i)));
              if (i < max - 1) System.out.println();*/
            }

            Variable x = lotus.getVar("x");
            x.value = 2 + 3;
            System.out.println(x);
            System.out.println(lotus.getVar("x"));
        }
        else if (!validParam) {
            System.out.println("Invalid input file.");
        }
        /**********************************************************************/
        Variable g, d, tni, z;
        g = new Variable<String>("Gabriel");
        d = new Variable<Double>(7.0);
        tni = new Variable<Integer>(11);
        z = new Variable<Double>(d.toDouble() + tni.toInt());

        System.out.println("---------------------------------------");
        // Gotta be sure that it's a String to do that casting!
        System.out.println(((String)g.value).substring(0, 1));
        // or:
        System.out.println(g.toString().substring(0, 1));

        System.out.println(g);
        System.out.println(g.type());
        System.out.println(d);
        System.out.println(tni);
        System.out.println(z);
    }
}
