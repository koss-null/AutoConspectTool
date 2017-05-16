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

        String s = "";

        do {
            if((s = reader.readLine()) == null) {
                break;
            }

            StringBuilder sentence = new StringBuilder();

            int cnt = 0;
            Integer files = 0;
            if (s.isEmpty()) {
                break;
            }
            for (char c : s.toCharArray()) {
                sentence.append(c);
                if (c == ' ') {
                    cnt++;
                }
                if (cnt % 7 == 0) {
                    files++;
                    cnt = 0;
                    FileWriter out = new FileWriter(files.toString());
                    out.write(sentence.toString());
                    out.close();
                    datasets.put(files.toString(), new File(files.toString()).toURI());
                    sentence = new StringBuilder();
                }
            }
        } while (s != null && s.length() != 0);


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
        do {
            s = reader.readLine();
            if (s.length() == 0) {
                break;
            }
            StringBuilder sentence = new StringBuilder();

            for (char c : s.toCharArray()) {
                sentence.append(c);
                if (c == '.' || c == '!' || c == '?') {
                    Record r = textClassifier.predict(sentence.toString());
                    System.out.println("Classifing sentence: \"" + sentence.toString() + "\"");
                    System.out.println("Predicted class: " + r.getYPredicted());
                    System.out.println("Probability: " + r.getYPredictedProbabilities().get(r.getYPredicted()));

                    System.out.println("Classifier Accuracy: "+vm.getAccuracy());
                    System.out.println("*****************");
                    sentence = new StringBuilder();
                }
            }
        } while (s.length() != 0);

        //Clean up
        //--------

        //Delete the classifier. This removes all files.
        textClassifier.delete();
    }
}
