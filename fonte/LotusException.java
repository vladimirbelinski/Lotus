class LotusException extends Exception {
    String message;

    public LotusException(String message) {
        this.message = message;
    }

    public String toString() {
        return this.message;
    }
}
