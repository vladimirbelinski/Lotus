class LotusException extends Exception {
    private String message, line;
    private int lN;

    public LotusException(String code, String line) {
        this.setLine(line);

        switch (code) {
        case "syntaxError":
			this.setMessage("Syntax error"); break;
        case "multipleCommands":
			this.setMessage("You can only have one command per line"); break;
        case "unknownCommand":
			this.setMessage("Unknown command"); break;
        case "bracketNotFound":
            this.setMessage("Could not find closing bracket"); break;
        case "usingReservedWords":
			this.setMessage("You cannot use Lotus' reserved words"); break;
        case "invalidType":
			this.setMessage("Invalid type"); break;
        case "invalidVarName":
			this.setMessage("Variable names cannot start with numbers or special characters"); break;
        case "invalidExp":
            this.setMessage("Invalid expression"); break;
        case "varNotFound":
			this.setMessage("Could not find variable"); break;
        case "unknownSymbol":
			this.setMessage("Unknown symbol"); break;
        case "stringPow":
			this.setMessage("Cannot calculate the power of a string"); break;
        case "nonIntMod":
			this.setMessage("Cannot calculate remainder of a division between non-integer operands"); break;
        case "divisionByZero":
			this.setMessage("Division by zero"); break;
        case "unknownEscape":
			this.setMessage("Unknown escape sequence"); break;
        case "cantAssign":
			this.setMessage("Invalid assignment of types"); break;
        case "inputMismatch":
			this.setMessage("Could not read the input"); break;
        case "missingParen":
            this.setMessage("Missing parenthesis"); break;
        }
    }

    private void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }

    public void setLine(String line) {
        this.line = line;
    }
    public String getLine() {
        return this.line;
    }

    public void setLN(int lN) {
        this.lN = lN;
    }
    public int getLN() {
        return this.lN;
    }
}
