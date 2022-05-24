import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ZRTokenizer {

    final static int BEGINNING_STATE = 0;
    static List<Token> tokenStream;
    static int tokenStreamSize;
    static ZRTokenizer instance = null;
    static HashMap<String, String> tokenList = new HashMap<>();
    String command;

    /**
     * Returns instance of the ZRTokenizer if instance's not null.
     * Otherwise, creates a new instance.
     * @return ZRTokenizer instance.
     */
    public static ZRTokenizer getInstance(){
        if(instance == null){
            instance = new ZRTokenizer();
        }
        return instance;
    }

    /**
     * Initializes the Tokenizer.
     * @param string command entered by user
     */
    public void initLexer(String string){
        this.command = string;
        tokenStream = tokenize(string);
        tokenStreamSize = tokenStream.size();
    }

    /**
     * Deletes the current lookahead and
     * returns following Token in the Token Stream.
     * @return Token
     */
    public Token nextToken(){
        tokenStream.remove(0);
        return tokenStream.get(0);
    }

    /**
     * Takes the Statement and returns a Token Stream.
     * Each character of the Statement is assigned to a
     * Token Class and added to the Stream.
     * @param word entered statement
     * @return list of Tokens
     */
    private List<Token> tokenize(String word){
        List<Token> tokenStream = new ArrayList<>();
        tokenStream.add(new Token("S", "S", -1));
        // position is saved for better error handling later on
        int state = BEGINNING_STATE;
        ArrayList<String> characterStream = new ArrayList<>(List.of(word.split("(?=\\W)|(?<=\\W)(?=\\w)")));
        characterStream.removeIf(item -> item == null || "".equals(item));
        for(String character : characterStream){
            //spaces are ignored
            if(!character.equals(" ") || !character.isBlank()) {
                tokenStream.add(matchToken(character.trim(), state));
            }
            state++;
        }
        tokenStream.add(new Token("$", "$", tokenStreamSize));
        return tokenStream;
    }

    /**
     * This Method takes a character and its position and returns a matching
     * Token. If no predefined Token Class is matched to the character, it is matched
     * with the "unexpected" Token Class.
     * @param character current character
     * @param position current position in the statement
     * @return corresponding Token
     */
    private Token matchToken(String character, int position){
        switch(character){
            case "VW": return new Token("VW", character, position);
            case "WH": return new Token("WH", character, position);
            case "RE": return new Token("RE", character, position);
            case "STIFT": return new Token("STIFT", character, position);
            case "FARBE": return new Token("FARBE", character, position);
            case "[": return new Token("[", character, position);
            case "]": return new Token("]", character, position);
            default:
                if(character.matches("[0-9]|[1-9][0-9]*")) return new Token("Number", character, position);
                if(character.matches("red|black|white|yellow|green|blue|orange|purple")) return new Token("Farbwert", character, position);
        }
        return new Token("unexpected", character, position);
    }
}
