class LotusException extends Exception {
    private String code, message;

    // put the stack trace too!
    // take off all "?
    public LotusException(String code, String line) {
        switch (code) {
        case "syntaxError":
			this.setMessage("Syntax error: \"" + line + "\""); break;
        case "multipleCommands":
			this.setMessage("You can only have one command per line:\n\"" + line + "\""); break;
        case "unknownCommand":
			this.setMessage("Unknown command: \"" + line + "\""); break;
        case "usingReservedWords":
			this.setMessage("You cannot use Lotus' reserved words as variable names:\n\"" + line + "\""); break;
        case "invalidType":
			this.setMessage("Invalid type: \"" + line + "\""); break;
        case "invalidVarName":
			this.setMessage("Variable names cannot start with numbers or special characters:\n'" + line + "'"); break;
        case "varNotFound":
			this.setMessage("Could not find variable: \"" + line + "\""); break;
        case "unknownSymbol":
			this.setMessage("Unknown symbol: '" + line + "'"); break;
        case "nonIntMod":
			this.setMessage("Cannot calculate remainder of a division between non-integer operands: \"" + line + "\""); break;
        case "divisionByZero":
			this.setMessage("Division by zero: \"" + line + "\""); break;
        case "unknownEscape":
			this.setMessage("Unknown escape sequence: \"" + line + "\""); break;
        case "cantAssign":
			this.setMessage("Cannot assign a \"" + line.substring(0, line.indexOf(";")) + "\" value to a variable of type \"" + line.substring(line.indexOf(";") + 1) + "\""); break;
        case "inputMismatch":
			this.setMessage("Could not read the input: \"" + line + "\""); break;
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
