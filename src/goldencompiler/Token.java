package goldencompiler;

class Token {
    TokenType type;
    String value;
    int line;

    Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    TokenType getType() {
        return type;
    }

    String getValue() {
        return value;
    }

    int getLine() {
        return line;
    }
}
