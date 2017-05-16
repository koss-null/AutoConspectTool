import java.util.List;

/**
 * Created by d.kossovich on 02/05/2017.
 */
public class Paragraph {

    private List<String> text;
    private List<String> keyWords;
    private Conspect.ThemeDefenitionMethod TDM;

    Paragraph(List<String> textBlock, List<String> keyWords, Conspect.ThemeDefenitionMethod TDM) {
        this.text = textBlock;
        this.keyWords = keyWords;
        this.TDM = TDM;
    }

    void formatText() {
        // TODO implement =)
    }
}
