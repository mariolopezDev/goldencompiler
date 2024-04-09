package goldencompiler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final String input;
    private final ArrayList<Token> tokens = new ArrayList<>();

    public Lexer(String input) {
        this.input = input;
    }

    public ArrayList<Token> tokenize() {
        String tokenPatterns = "(?<DOCTYPE><!DOCTYPE\\s+html>)|" + // <!DOCTYPE html>
                                "(?<WHITESPACE>[ \\t]+)|" + // "\\s+|" +  // ignore whitespace
                                "(?<NEWLINE>\\n)|" + 
                                "(?<LET>\\blet\\b)|" + //  reconocimiento de 'let'
                                "(?<CONST>\\bconst\\b)|" + //  reconocimiento de 'const'
                                "(?<FUNCTION>\\bfunction\\b)|" + //  reconocimiento de 'function'                         
                               "(?<IDENTIFIER>[a-zA-Z_$][a-zA-Z\\d_$]*)|" + // 
                               "(?<NUMBER>\\b\\d+(\\.\\d+)?\\b)|" + // Numbers, including decimals
                               "(?<OPERATOR>\\+=|-=|\\*=|/=|==|!=|<=|>=|=|\\+|-|\\*|/)|" + // Operators, including compound
                               "(?<DELIMITER>[{}();,])|" + // Delimiters
                               "(?<STRING>\"(\\\\.|[^\"])*\")|" + // String literals with escapes
                               "(?<HTMLTAG><\\/?[\\w\\s=\"']+\\/?>)|" + // HTML tags with attributes
                               "(?<HTMLCONTENT>(?<=>)[^<]+(?=<))"; // Text between HTML tags
    
        Pattern pattern = Pattern.compile(tokenPatterns);
        Matcher matcher = pattern.matcher(input);

        int line = 1;

        
            while (matcher.find()) {
                if (matcher.group("NEWLINE") != null) {
                    line++;
                    continue;
                }
                if (matcher.group("DOCTYPE") != null) {
                    tokens.add(new Token(TokenType.DOCTYPE, matcher.group("DOCTYPE"), line));
                } else if (matcher.group("WHITESPACE") != null) {
                    continue; 
                } else if (matcher.group("IDENTIFIER") != null) {
                    tokens.add(new Token(TokenType.IDENTIFIER, matcher.group("IDENTIFIER"), line));
                } else if (matcher.group("NUMBER") != null) {
                    tokens.add(new Token(TokenType.NUMBER, matcher.group("NUMBER"), line));
                } else if (matcher.group("OPERATOR") != null) {
                    tokens.add(new Token(TokenType.OPERATOR, matcher.group("OPERATOR"), line));
                } else if (matcher.group("DELIMITER") != null) {
                    tokens.add(new Token(TokenType.DELIMITER, matcher.group("DELIMITER"), line));
                } else if (matcher.group("STRING") != null) {
                    tokens.add(new Token(TokenType.STRING, matcher.group("STRING"), line));
                } else if (matcher.group("HTMLTAG") != null) {
                    tokens.add(new Token(TokenType.HTMLTAG, matcher.group("HTMLTAG"), line));
                } else if (matcher.group("HTMLCONTENT") != null) {
                    tokens.add(new Token(TokenType.HTMLCONTENT, matcher.group("HTMLCONTENT"), line));
                } else if (matcher.group("FUNCTION") != null) {
                    tokens.add(new Token(TokenType.FUNCTION, matcher.group("FUNCTION"), line));
                } else if (matcher.group("LET") != null) {
                    tokens.add(new Token(TokenType.LET, matcher.group("LET"), line));
                } else if (matcher.group("CONST") != null) {
                tokens.add(new Token(TokenType.CONST, matcher.group("CONST"), line));
            }

        }
        return tokens;    
    }

    public static void main(String[] args) {
        // Las pruebas existentes
        testLexer("let x = 42;", "Test 1: Simple variable declaration");
        testLexer("const str = \"Hello, world!\";", "Test 2: String assignment");
        testLexer("function test() {}", "Test 3: Function declaration");
        testLexer("x += 5;", "Test 4: Compound assignment");
        testLexer("<div>Sample</div>", "Test 5: Basic HTML");
        testLexer("a -= b;", "Test 6: Compound subtraction");
        testLexer("<input type=\"text\" id=\"username\" />", "Test 7: Self-closing HTML tag with attributes");
        testLexer("<script>var x = 10;</script>", "Test 8: Script tag with JavaScript code inside");
    
        // Prueba nueva con HTML y JavaScript más complejo
        testLexer("<!DOCTYPE html><html><head><title>Calculadora de Edad</title></head><body> <h1>Calculadora de Edad</h1><label for=\"nombre\">Nombre Completo: </label><input type=\"text\" id=\"nombre\" placeholder=\"Nombre Completo\"><label for=\"fechaNacimiento\">Fecha de Nacimiento: </label><input type=\"date\" id=\"fechaNacimiento\"><button onclick=\"calcularEdad()\">Calcular Edad</button><p id=\"resultado\"></p><script>function calcularEdad() { const nomb9re = document.getElementById(\"nombre\").value; const 8fechaNacimiento = new Date(document.getElementById(\"fechaNacimiento\").value); const fechaHoy = new Date(); const edad = fechaHoy.getFullYear() - fechaNacimiento.getFullYear(); let resultado = `${nombre}, tienes ${edad} años de edad. Eres `; if (edad < 18) { resultado += \"menor de edad.\"; } else if (edad >= 18 && edad < 65) { resultado += \"mayor de edad.\"; } else { resultado += \"adulto mayor.\"; } document.getElementById(\"resultado\").innerHTML = resultado; }</script></body></html>", "Test 9: Complex HTML with embedded JavaScript");
    
        // Nuevas pruebas para validar los nuevos patrones
        testLexer("let varName = 10;", "Test 10: 'let' declaration");
        testLexer("const PI = 3.14;", "Test 11: 'const' declaration");
        testLexer("function add(a, b) { return a + b; }", "Test 12: 'function' declaration");
    }

    private static void testLexer(String input, String testDescription) {
        Lexer lexer = new Lexer(input);
        ArrayList<Token> tokens = lexer.tokenize();
        System.out.println(testDescription);
        for (Token token : tokens) {
            System.out.println(token.type + " - " + token.value + " - TOKEN LINE: " + token.line);
        }
        System.out.println("------------");
    }

}


