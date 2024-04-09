package goldencompiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ErrorReporter {
    private final Map<Integer, String> errors = new LinkedHashMap<>();
    private FileHandler fileHandler;

    public ErrorReporter(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    public void processFile() throws IOException {
        List<String> lines = fileHandler.readLines();
        checkForErrors(lines);
        writeOutputFile(lines);
    }

    private void checkForErrors(List<String> lines) {
        // Lógica para detectar errores genéricos en las líneas
    }

    private void writeOutputFile(List<String> lines) throws IOException {
        List<String> outputLines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String line = String.format("%04d %s", lineNumber, lines.get(i));
            outputLines.add(line);

            if (errors.containsKey(lineNumber)) {
                outputLines.add(errors.get(lineNumber));
            }
        }
        fileHandler.writeLines(outputLines);
    }

    public void report(int lineNumber, String errorMessage) {
        errors.put(lineNumber, "Error: " + errorMessage);
    }
    public void report(int lineNumber, int errorCode, String errorMessage) {
        errors.put(lineNumber, "Error Code: " + errorCode + ", Message: " + errorMessage);
    }
}
