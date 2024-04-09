package goldencompiler;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String inputFilePath = args.length > 0 ? args[0] : "./doc/sample.html";
        FileHandler fileHandler = new FileHandler(inputFilePath);
        ErrorReporter errorReporter = new ErrorReporter(fileHandler);
        
        try {
            // Leer las líneas del archivo de entrada y unirlas en una sola cadena
            ArrayList<String> lines = (ArrayList<String>) fileHandler.readLines();
            StringBuilder content = new StringBuilder();
            for (String line : lines) {
                content.append(line).append("\n");
            }

            // Inicializar Lexer y tokenizar el contenido del archivo
            Lexer lexer = new Lexer(content.toString());
            ArrayList<Token> tokens = lexer.tokenize();

            for (Token token : tokens) {
                System.out.println(token.getType() + " - " + token.getValue()+ " - TOKEN LINE: " + token.line);
            }
    
            // Inicializar el Parser con los tokens generados
            Parser parser = new Parser(tokens, errorReporter);
            parser.parse();  // Aquí el Parser procesa los tokens y genera un AST
            System.out.println("Procesamiento completado exitosamente.");

        } catch (IOException e) {
            System.err.println("Ocurrió un error al procesar el archivo: " + e.getMessage());
        }

        try {
            // Inicializar el ErrorReporter y procesar el archivo
            errorReporter.processFile();
            System.out.println("Archivo procesado exitosamente. El archivo analizado fue guardado en: " + fileHandler.getOutputFilePath());
        } catch (IOException e) {
            System.err.println("Ocurrió un error al procesar el archivo: " + e.getMessage());
        }
    }
}

