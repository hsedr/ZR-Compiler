import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        String command = "FARBE yellow VW 200 STIFT 1 WH 36[RE 10 VW 10 WH 4[VW 100 RE 90]]";
        ZRTokenizer lexer = ZRTokenizer.getInstance();
        lexer.initLexer(command);
        ZRCompiler compiler = new ZRCompiler();
        String compiledCode = compiler.Start();
        try (PrintStream out = new PrintStream(new FileOutputStream("src/main/java/zr.html"))) {
                out.print(compiledCode);
        }
    }
}
