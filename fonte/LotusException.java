class LotusException extends Exception {
    private String code, message;
    private Integer line;

    public LotusException(String code, String info) {
        switch (code) {
        case "syntaxError":
			this.setException(code, "Syntax error: \"" + info + "\""); break;
        case "multipleCommands":
			this.setException(code, "You can only have one command per info:\n\"" + info + "\""); break;
        case "unknownCommand":
			this.setException(code, "Unknown command: \"" + info + "\""); break;
        case "usingReservedWords":
			this.setException(code, "You cannot use Lotus' reserved words as variable names:\n\"" + info + "\""); break;
        case "invalidType":
			this.setException(code, "Invalid type: \"" + info + "\""); break;
        case "invalidVarName":
			this.setException(code, "Variable names cannot start with numbers or special characters:\n'" + info + "'"); break;
        case "invalidExp":
            this.setException(code, "Invalid expression:\n\"" + info + "\""); break;
        case "varNotFound":
			this.setException(code, "Could not find variable: \"" + info + "\""); break;
        case "unknownSymbol":
			this.setException(code, "Unknown symbol: '" + info + "'"); break;
        case "stringPow":
			this.setException(code, "Cannot calculate the power of a string: \"" + info + "\""); break;
        case "nonIntMod":
			this.setException(code, "Cannot calculate remainder of a division between non-integer operands: \"" + info + "\""); break;
        case "divisionByZero":
			this.setException(code, "Division by zero: \"" + info + "\""); break;
        case "unknownEscape":
			this.setException(code, "Unknown escape sequence: \"" + info + "\""); break;
        case "cantAssign":
			this.setException(code, "Cannot assign a \"" + info.substring(0, info.indexOf(";")) + "\" value to a variable of type \"" + info.substring(info.indexOf(";") + 1) + "\""); break;
        case "inputMismatch":
			this.setException(code, "Could not read the input: \"" + info + "\""); break;
        case "missingParen":
            this.setException(code, "Missing parenthesis in the expression: \"" + info + "\""); break;
        }
    }

    private void setException(String code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

    private void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return this.code;
    }

    private void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }

    public void setLine(int i) {
        this.line = i;
    }
    public Integer getLine() {
        return this.line;
    }
}
