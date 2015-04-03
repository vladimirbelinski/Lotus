import java.util.*;
import java.io.*;

class Lotus {
    public static void main(String[] args) throws Exception {
        File f = new File(args[0]);
        Scanner s = new Scanner(f);
        ArrayList<String> in = new ArrayList<String>();

        while (s.hasNext()) {
            in.add(s.nextLine());
        }
        s.close();

        for (int i = 0; i < in.size(); i++) {
            System.out.println(in.get(i));
        }
    }
}
