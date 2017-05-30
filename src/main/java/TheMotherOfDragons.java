import com.datumbox.framework.applications.nlp.TextClassifier;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.common.utilities.RandomGenerator;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.common.text.extractors.NgramsExtractor;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.core.machinelearning.classification.MultinomialNaiveBayes;
import com.datumbox.framework.core.machinelearning.featureselection.ChisquareSelect;
import com.datumbox.framework.core.machinelearning.modelselection.metrics.ClassificationMetrics;

import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * Created by d.kossovich on 20/04/2017.
 */
public class TheMotherOfDragons {

    private static final String makeConspect = "make-conspect";
    private static final String find = "find";

    static final int AVEREGE_SENTENCE_LENGTH = 7;

    /*
        * args are:
        *   first: operation (find, make-conspect)
        *   second: string with path to conspect || search string
     */
    public static void main(String... args) throws IOException, URISyntaxException  {

        //Initialization
        //--------------
        RandomGenerator.setGlobalSeed(42L); //optionally set a specific seed for all Random objects
        Configuration configuration = Configuration.getConfiguration(); //default configuration based on properties file
        //configuration.setStorageConfiguration(new InMemoryConfiguration()); //use In-Memory engine (default)
        //configuration.setStorageConfiguration(new MapDBConfiguration()); //use MapDB engine
        //configuration.getConcurrencyConfiguration().setParallelized(true); //turn on/off the parallelization
        //configuration.getConcurrencyConfiguration().setMaxNumberOfThreadsPerTask(4); //set the concurrency level



        //Reading Data
        //------------
        Map<Object, URI> datasets = new HashMap<>(); //The examples of each category are stored on the same file, one example per row.

        BufferedReader reader = new BufferedReader(new FileReader("uboat.in"));

        String s = " ";
        StringBuilder sentence = new StringBuilder();


        Integer files = 0;

        do {
            if ((s = reader.readLine()) == null) {
                break;
            }

            if (s.isEmpty()) {
                System.out.println("BREAKING!");
                break;
            }

            sentence.append(s);
            sentence.delete(s.length() - 1, s.length());

            System.out.println("Appended " + s);

            if (s.charAt(s.length() - 1) == '$') {
                System.out.println("NEW STR");
                files++;
                FileWriter out = new FileWriter(files.toString());
                out.write(sentence.toString());
                out.close();
                datasets.put(files.toString(), new File(files.toString()).toURI());
                sentence = new StringBuilder();
            }
        } while (s.length() != 0);


        //Setup Training Parameters
        //-------------------------
        TextClassifier.TrainingParameters trainingParameters = new TextClassifier.TrainingParameters();

        //numerical scaling configuration
        trainingParameters.setNumericalScalerTrainingParameters(null);

        //categorical encoding configuration
        trainingParameters.setCategoricalEncoderTrainingParameters(null);

        //Set feature selection configuration
        trainingParameters.setFeatureSelectorTrainingParametersList(Arrays.asList(new ChisquareSelect.TrainingParameters()));

        //Set text extraction configuration
        trainingParameters.setTextExtractorParameters(new NgramsExtractor.Parameters());

        //Classifier configuration
        trainingParameters.setModelerTrainingParameters(new MultinomialNaiveBayes.TrainingParameters());



        //Fit the classifier
        //------------------
        TextClassifier textClassifier = MLBuilder.create(trainingParameters, configuration);
        textClassifier.fit(datasets);
        textClassifier.save("SentimentAnalysis");



        //Use the classifier
        //------------------

        //Get validation metrics on the dataset
        ClassificationMetrics vm = textClassifier.validate(datasets);

        //Classify a single sentence
        reader.close();
        reader = new BufferedReader(new FileReader("uboat.in"));
        sentence = new StringBuilder();

        FileWriter out = new FileWriter("result");

        files = 1;
        do {
            s = reader.readLine();
            if (s.length() == 0) {
                break;
            }

            sentence.append(s);
            sentence.delete(s.length() - 1, s.length());

             if (s.charAt(s.length() - 1) == '$') {
                    datasets.remove(files.toString());
                    textClassifier = MLBuilder.create(trainingParameters, configuration);
                    textClassifier.fit(datasets);
                    textClassifier.save("SentimentAnalysis");

                    datasets.put(files.toString(), new File(files.toString()).toURI());

                    Record r = textClassifier.predict(sentence.toString());
                    //out.write("Classifing sentence: \"" + sentence.toString() + "\"\n");
                    out.write("C: " + r.getYPredicted() + "\n");
                    files++;
                    out.write("P: " + r.getYPredictedProbabilities().get(r.getYPredicted()) + "\n");

                    out.write("A: " + vm.getAccuracy() + "\n");
                    out.write("*****************\n");
                    sentence = new StringBuilder();
            }
        } while (s.length() != 0);

        out.close();
        //Clean up
        //--------

        //Delete the classifier. This removes all files.
        textClassifier.delete();
    }
}
