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

    private List<String> pureText;
    private List<Paragraph> paragraphs;

    Conspect(String path) throws IOException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));

            pureText = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                pureText.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Wrong file path");
        }
    }

    void paragraphDevision() {
        
    }
}
