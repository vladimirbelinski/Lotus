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
        int max;
        File f = new File(args[0]);
        Scanner s = new Scanner(f);
        Interpreter lotus = new Interpreter();
        ArrayList<String> in = new ArrayList<String>();

        while (s.hasNext()) {
            in.add(s.nextLine());
        }
        s.close();

        max = in.size();
        for (int i = 0; i < max; i++) {
            System.out.println(in.get(i));
            System.out.println("= " + lotus.solve(in.get(i)));
            if (i < max - 1) System.out.println();
        }
        /**********************************************************************/
        Variable g, d, tni;
        g = new Variable<String>("Gabriel");
        d = new Variable<Double>(7.0);
        tni = new Variable<Integer>(11);

        System.out.println("---------------------------------------");
        // Gotta be sure that s is a String to do that casting!
        System.out.println(((String)g.value).substring(0, 1));
        // or:
        System.out.println(g.toString().substring(0, 1));

        System.out.println(g);
        System.out.println(g.type());
        System.out.println(d);
        System.out.println(tni);
    }
}
