package goldencompiler;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private ErrorReporter errorReporter;


    public Parser(List<Token> tokens, ErrorReporter errorReporter) {
        this.tokens = tokens;
        this.errorReporter = errorReporter;
    }

    public void parse() {
        // Comprobación inicial para <!DOCTYPE html>
    if (!match(TokenType.DOCTYPE)) {
        throw new ParseException("Se espera '<!DOCTYPE html>' al inicio del archivo.", peek().getLine());
    }
        while (!isAtEnd()) {
            try {
                parseStatement();
            } catch (ParseException e) {
                errorReporter.report(e.getLine(), 100, e.getMessage());
                System.err.println("Parse error: " + e.getMessage());
                synchronize();
            }
        }
    }

    private void parseStatement() {
        if (match(TokenType.LET, TokenType.CONST)) {
            //consume(TokenType.LET, "Expected 'let'");
            parseDeclaration();
        } else if (match(TokenType.FUNCTION)) {
            parseFunctionDeclaration();
        } else {
            parseExpressionStatement();
        }
    }

    private void parseDeclaration() {
        System.out.println("Current Token at Declaration Start: " + peek().getValue());
        //COnsume apropiate type if LET or CONST
        if (match(TokenType.LET)) {
            consume(TokenType.LET, "Expect 'let' keyword.");
        } else if (match(TokenType.CONST)) {
            consume(TokenType.CONST, "Expect 'const' keyword.");
        }
        
        //consume(TokenType.LET, "Expect 'let' or 'const' keyword.");
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
        consume(TokenType.OPERATOR, "Expect '=' after variable name.");
        parseExpression();
        System.out.println("Current Token: " + peek().getValue());
        consume(TokenType.DELIMITER, "Expect ';' after variable declaration.");
    }

    private void parseFunctionDeclaration() {
        System.out.println("Current Token at Function Start: " + peek().getValue());
        consume(TokenType.FUNCTION, "Se espera 'function'.", "function");
        Token name = consume(TokenType.IDENTIFIER, "Se espera nombre de función.");
        consume(TokenType.DELIMITER, "Se espera '(' después del nombre de la función.", "(");
     // Opcionalmente parsea parámetros aquí
        consume(TokenType.DELIMITER, "Se espera ')' después de los parámetros.", ")");
        consume(TokenType.DELIMITER, "Se espera '{' antes del cuerpo de la función.", "{");
        while (!check(TokenType.DELIMITER, "}")) {
            parseStatement();
        }
        consume(TokenType.DELIMITER, "Se espera '}' después del cuerpo de la función.", "}");
    }

    
    private void parseExpressionStatement() {
        parseExpression();
        consume(TokenType.DELIMITER, "Expect ';' after expression.");
    }

    private void parseExpression() {
        // Simplified expression parsing
        if (match(TokenType.IDENTIFIER)) {
            // Variable reference or assignment
            if (match(TokenType.OPERATOR)) {  // assuming '=' for simplicity
                parseExpression();  // Right-hand side of assignment
            }
        } else if (match(TokenType.NUMBER, TokenType.STRING)) {
            // Consumes a number or string literal
            advance();
        } else {
            String error = "Unexpected token: " + peek().getValue();
            throw new ParseException(error, peek().getLine());
        }
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                System.out.println("Matched: " + peek().getValue());
                //advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message, String expectedValue) {
        System.out.println("Trying to consume: " + expectedValue + " Current token: " + peek().getValue());
        if (check(type, expectedValue)) {
            return advance();
        }
        throw new ParseException(message + " Found " + peek().getValue(), peek().getLine());
        //throw new ParseException(message + " Found " + peek().getValue());
    }
    private Token peek() {
        if (isAtEnd()) return null;
        return tokens.get(current);
    }
    // Sobrecarga para casos sin valor específico
    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw new ParseException(message + " Found: " + peek().getValue(), peek().getLine());
    }
    

    private boolean check(TokenType type, String expectedValue) {
        if (isAtEnd()) return false;
        Token currentToken = tokens.get(current);
        return currentToken.getType() == type && currentToken.getValue().equals(expectedValue);
    }
    
    // Sobrecarga del método `check` para casos donde solo importa el tipo
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return tokens.get(current).getType() == type;
    }
    

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void synchronize() {
        advance();  // Avanza más allá del token problemático.
        while (!isAtEnd()) {
            if (previous().getType() == TokenType.DELIMITER && previous().getValue().equals(";")) {
                return; // Retorna después de encontrar un delimitador de fin de expresión.
            }
            switch (peek().getType()) {
                case FUNCTION:
                case LET:
                case CONST:
                    return; // Estos tokens inician declaraciones, por lo tanto, son buenos puntos para reanudar.
            }
            advance();
        }
    }
    
    /*
    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().getType() == TokenType.DELIMITER) return;
            if (match(TokenType.FUNCTION, TokenType.LET, TokenType.CONST)) return;
            advance();
        } 
    } */





    // Agregar main para pruebas
    public static void main(String[] args) {
        testParser("let x = 42;", "Prueba 1: Declaración de variable simple");
        testParser("const str = \"Hello, world!\";", "Prueba 2: Asignación de string");
        testParser("function test() { return test; }", "Prueba 3: Definición de función");
        testParser("x += 5;", "Prueba 4: Asignación compuesta");
    }

    // Método para probar el Parser
    public static void testParser(String input, String testDescription) {
        Lexer lexer = new Lexer(input);
        ArrayList<Token> tokens = lexer.tokenize();
        System.out.println(testDescription);
        for (Token token : tokens) {
            System.out.println("Token: " + token.getType() + " - " + token.getValue());
        }
        ErrorReporter errorReporter = new ErrorReporter(new FileHandler("output.txt"));
        Parser parser = new Parser(tokens, errorReporter);
        //parser.parse();

        System.out.println(testDescription);
        try {
            parser.parse();
            System.out.println("Análisis completado correctamente.");
        } catch (Exception e) {
            System.out.println("Error durante el análisis: " + e.getMessage());
        }
        System.out.println("--------------");
    }


}
