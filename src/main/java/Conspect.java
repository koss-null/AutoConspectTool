import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by d.kossovich on 20/04/2017.
 */
public class Conspect {

    private final int TileSize = 20; // we need to change this number to get the best result

    private List<String> pureText;
    private List<Paragraph> paragraphs;

    public static enum ThemeDefenitionMethod {
        LSA, LDA, BAYES;
    }

    Conspect(String path) throws IOException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));

            pureText = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                pureText.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Wrong file path");
        }
    }

    private void lsaThemeDefenition() {

    }

    private void ldaThemeDefenition() {
        System.out.println("LDA is not implemented yet");
    }

    private void bayesThemeDefenition() {
        System.out.println("Bayes is not implemented yet");
    }

    void themeDefenition(ThemeDefenitionMethod method) {
        switch(method) {
            case LSA:
                lsaThemeDefenition();
                break;
            case LDA:
                ldaThemeDefenition();
                break;
            case BAYES:
                bayesThemeDefenition();
                break;
        }
    }

    // text tiling here using Stanford CoreNLP
    void paragraphDevision() {

    }
}
