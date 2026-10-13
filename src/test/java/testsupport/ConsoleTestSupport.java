package testsupport;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public abstract class ConsoleTestSupport {
    private PrintStream originalOut;
    private PrintStream capturedOut;
    private ByteArrayOutputStream output;

    @BeforeEach
    void captureConsole() {
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        capturedOut = new PrintStream(output, true, StandardCharsets.UTF_8);
        System.setOut(capturedOut);
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        capturedOut.close();
    }

    protected String consoleOutput() {
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
