class LotusException extends Exception {
    private String code, message;

    // put the stack trace too!
    // take off all "?
    public LotusException(String code, String line) {
        switch (code) {
        case "syntaxError":
			this.setMessage("Syntax error:\n\"" + line + "\""); break;
        case "multipleCommands":
			this.setMessage("You can only have one command per line:\n\"" + line + "\""); break;
        case "unknownCommand":
			this.setMessage("Unknown command:\n\"" + line + "\""); break;
        case "usingReservedWords":
			this.setMessage("You cannot use Lotus' reserved words as variable names:\n\"" + line + "\""); break;
        case "invalidType":
			this.setMessage("Invalid type:\n\"" + line + "\""); break;
        case "invalidVarName":
			this.setMessage("Variable names cannot start with numbers or special characters:\n'" + line + "'"); break;
        case "varNotFound":
			this.setMessage("Could not find variable: \n\"" + line + "\""); break;
        case "unknownSymbol":
			this.setMessage("Unknown symbol: \n'" + line + "'"); break;
        case "nonIntMod":
			this.setMessage("Cannot calculate remainder of a division between non-integer operands:\n\"" + line + "\""); break;
        case "divisionByZero":
			this.setMessage("Division by zero:\n\"" + line + "\""); break;
        case "unknownEscape":
			this.setMessage("Unknown escape sequence:\n\"" + line + "\""); break;
        case "nullAssignment":
			this.setMessage("Assigning null to:\n\"" + line + "\""); break;
        case "nullVar":
			this.setMessage("Trying to assign a value to a Variable pointing to null:\n" + line); break;
        case "nullToNull":
			this.setMessage("Assigning null to null!?"); break; // stack trace?
        case "cantAssignInt":
			this.setMessage("Cannot assign an int to a variable of type: \n\"" + line + "\""); break;
        case "cantAssignBool":
			this.setMessage("Cannot assign a bool to a variable of type: \n\"" + line + "\""); break;
        case "cantAssignDouble":
			this.setMessage("Cannot assign a double to a variable of type: \n\"" + line + "\""); break;
        case "cantAssignString":
			this.setMessage("Cannot assign a string to a variable of type: \n\"" + line + "\""); break;
        case "cantAssign":
			this.setMessage("Cannot assign a\n\"" + line.substring(0, line.indexOf(";")) + "\"\nvalue to a variable of type\n\"" + line.substring(line.indexOf(";") + 1) + "\""); break;
        case "inputMismatch":
			this.setMessage("Could not read the input:\n\"" + line + "\""); break;
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
