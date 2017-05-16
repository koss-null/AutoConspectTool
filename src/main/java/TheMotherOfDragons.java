import com.datumbox.framework.applications.nlp.TextClassifier;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.common.utilities.RandomGenerator;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.common.text.extractors.NgramsExtractor;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.core.machinelearning.classification.MultinomialNaiveBayes;
import com.datumbox.framework.core.machinelearning.featureselection.ChisquareSelect;
import com.datumbox.framework.core.machinelearning.modelselection.metrics.ClassificationMetrics;

import java.io.IOException;
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

    /*
        * args are:
        *   first: operation (find, make-conspect)
        *   second: string with path to conspect || search string
     */
    public static void main(String... args) throws IOException, URISyntaxException  {
//        String issue = args[0];
//
//        if (issue.equals(makeConspect)) {
//            String path = args[1];
//            Conspect conspect = new Conspect(path);
//
//            conspect.paragraphDevision();
//            //conspect.bayesThemeDefenition();
//            //conspect.ldaThemeDefenition();
//            conspect.paragraphDevision();
//            conspect.themeDefenition(Conspect.ThemeDefenitionMethod.LSA);
//            //conspect.paragraphReformatting();
//            //conspect.createWiki();
//
//        } else {
//            System.out.println("Sorry, I can't do this");
//        }


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
        datasets.put("positive", TheMotherOfDragons.class.getClassLoader().getResource("datasets/sentiment-analysis/rt-polarity.pos").toURI());
        datasets.put("negative", TheMotherOfDragons.class.getClassLoader().getResource("datasets/sentiment-analysis/rt-polarity.neg").toURI());



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
        String sentence = "Datumbox is amazing!";
        Record r = textClassifier.predict(sentence);

        System.out.println("Classifing sentence: \""+sentence+"\"");
        System.out.println("Predicted class: "+r.getYPredicted());
        System.out.println("Probability: "+r.getYPredictedProbabilities().get(r.getYPredicted()));

        System.out.println("Classifier Accuracy: "+vm.getAccuracy());



        //Clean up
        //--------

        //Delete the classifier. This removes all files.
        textClassifier.delete();
    }
}
