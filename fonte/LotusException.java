class LotusException extends Exception {
    private String code, message;

    public LotusException(String code, String line) {
        switch (code) {
        case "syntaxError": this.setMessage("Syntax error: \"" + line + "\""); break;
        case "multipleCommands": this.setMessage("You can only have one command per line: \"" + line + "\""); break;
        case "unknownCommand": this.setMessage("Unknown command \"" + line + "\""); break;
        case "usingReservedWords": this.setMessage("You cannot use Lotus' reserved words as variable names: \"" + line + "\""); break;
        case "invalidType": this.setMessage("Invalid type \"" + line + "\""); break;
        case "invalidVarName": this.setMessage("Variable names cannot start with numbers or special characters: '" + line + "'"); break;
        case "varNotFound": this.setMessage("Could not find variable \"" + line + "\""); break;
        case "unknownSymbol": this.setMessage("Unknown symbol '" + line + "'"); break;
        case "nonIntMod": this.setMessage("Cannot calculate remainder of a division between non-integer operands: \"" + line + "\""); break;
        case "divisionByZero": this.setMessage("Division by zero: \"" + line + "\""); break;
        case "unknownEscape": this.setMessage("Unknown escape sequence: \"" + line + "\""); break;
        case "nullAssignment": this.setMessage("Assigning null to \"" + line + "\""); break;
        case "nullVar": this.setMessage("Trying to assign a value to a Variable pointing to null:\n" + line); break;
        case "nullToNull": this.setMessage("Assigning null to null!?"); break;
        case "cantAssignInt": this.setMessage("Cannot assign an int to a variable of type \"" + line + "\""); break;
        case "cantAssignBool": this.setMessage("Cannot assign a bool to a variable of type \"" + line + "\""); break;
        case "cantAssignDouble": this.setMessage("Cannot assign a double to a variable of type \"" + line + "\""); break;
        case "cantAssignString": this.setMessage("Cannot assign a string to a variable of type \"" + line + "\""); break;
        case "cantAssign": this.setMessage("Cannot assign a \"" + line.substring(0, line.indexOf(");") - 1) + "\" value to a variable of type \"" + line.substring(line.indexOf(");") + 1) + "\""); break;
        }
        this.setCode(code);
    }

    private void setCode(String code) {
        this.code = code;
    }
    private void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }
    public String getMessage() {
        return this.message;
    }

    // public String toString() {
    //     return this.message;
    // }
}
