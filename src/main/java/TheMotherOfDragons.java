import java.io.IOException;

/**
 * Created by d.kossovich on 20/04/2017.
 */
public class TheMotherOfDragons {

    private static final String makeConspect = "make-conspect";
    private static final String find = "find";

    /*
        * args are:
        *   first: operation (find, make-conspect)
        *   second: string with path to conspect || search string
     */
    public static void main(String... args) throws IOException {
        String issue = args[0];

        if (issue.equals(makeConspect)) {
            String path = args[1];
            Conspect conspect = new Conspect(path);

            conspect.paragraphDevision();
            //conspect.bayesThemeDefenition();
            //conspect.ldaThemeDefenition();
            conspect.paragraphDevision();
            conspect.themeDefenition(Conspect.ThemeDefenitionMethod.LSA);
            //conspect.paragraphReformatting();
            //conspect.createWiki();

        } else {
            System.out.println("Sorry, I can't do this");
        }
    }
}
