class LotusException extends Exception {
    private String code, message;

    public LotusException(String code, String line) {
        // encapsular isso aqui!
        switch (code) {
            case "syntaxError": this.message = "Syntax error: \"" + line + "\""; break;
            case "multipleCommands": this.message = "You can only have one command per line: \"" + line + "\""; break;
            case "unknownCommand": this.message = "Unknown command \"" + line + "\""; break;
            case "usingReservedWords": this.message = "You cannot use Lotus' reserved words as variable names: \"" + line + "\""; break;
            case "invalidType": this.message = "Invalid type \"" + line + "\""; break;
            case "invalidVarName": this.message = "Variable names cannot start with numbers or special characters: '" + line + "'"; break;
            case "varNotFound": this.message = "Could not find variable \"" + line + "\""; break;
            case "unknownSymbol": this.message = "Unknown symbol '" + line + "'"; break;
            case "nonIntMod": this.message = "Cannot calculate remainder of a division between non-integer operands: \"" + line + "\""; break;
            case "divisionByZero": this.message = "Division by zero: \"" + line + "\""; break;
            case "unknownEscape": this.message = "Unknown escape sequence: \"" + line + "\""; break;
            case "nullAssignment": this.message = "Assigning null to \"" + line + "\""; break;
            case "nullVar": this.message = "Trying to assign a value to a Variable pointing to null:\n" + line; break;
            case "nullToNull": this.message = "Assigning null to null!?"; break;
            case "cantAssignInt": this.message = "Cannot assign an int to a variable of type \"" + line + "\""; break;
            case "cantAssignBool": this.message = "Cannot assign a bool to a variable of type \"" + line + "\""; break;
            case "cantAssignDouble": this.message = "Cannot assign a double to a variable of type \"" + line + "\""; break;
            case "cantAssignString": this.message = "Cannot assign a string to a variable of type \"" + line + "\""; break;
            case "cantAssign": this.message = "Cannot assign a \"" + line.substring(0, line.indexOf(";") - 1) + "\" value to a variable of type \"" + line.substring(line.indexOf(";") + 1) + "\""; break;
        }
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return this.message;
    }
}
