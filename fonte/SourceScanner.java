import java.io.*;
import java.util.*;
import java.util.regex.*;

class SourceScanner {
    public ArrayList<String> scan(File f) throws Exception {
        String tmpInput;
        Scanner sc = new Scanner(f);
        int ind = 0, max, semicolon, bracket;
        ArrayList<String> input = new ArrayList<String>();

        while (sc.hasNext()) {
            tmpInput = sc.nextLine().trim();

            // discarding comments
            if (tmpInput.startsWith("--")) {
                tmpInput = "";
            }
            else if (!tmpInput.isEmpty()) {
                semicolon = this.indexOf(";", tmpInput);
                bracket = this.indexOf("{", tmpInput);

                if (semicolon > bracket) {
                    tmpInput = tmpInput.substring(0, semicolon + 1);
                }
                else if (bracket > 0) {
                    tmpInput = tmpInput.substring(0, bracket + 1);
                }
            }

            input.add(tmpInput);
        }
        sc.close();

        return input;
    }

    private int indexOf(String r, String s) {
        int aux, index = 0;
        String lineEnding = "";

        do {
            aux = s.indexOf(r, index + 1);
            if (aux > 0) {
                index = aux;
                lineEnding = s.substring(index + 1).trim();
                System.out.println("lineEnding: " + lineEnding);
            }
        } while (aux > 0 && !lineEnding.isEmpty() && !lineEnding.startsWith("--"));

        return index;
    }
}
