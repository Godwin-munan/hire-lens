package com.munan.hirelens.utils.modeltrainer;

import com.munan.hirelens.enums.ModelTypes;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import opennlp.tools.namefind.*;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 *
 * @author Godwin Ngusen Jethro.
 */
public class ModelTrainer {

    private final String trainingDataClassPath;

    /**
     * note: variable trainingDataFilePath might be needed in the future.
     */
    @SuppressWarnings("unused")
    private final String trainingDataFilePath;

    private final String modelClassPath;
    private final String modelFilePath;
    private final ResourceLoader resourceLoader;

    /**
     * Constructor to initialize ModelTrainer with model and training data paths.
     *
     * @param trainingDataClassPath   Path to the model file.
     * @param modelClassPath Path to the training data.
     */
    public ModelTrainer(
        String trainingDataClassPath,
        String trainingDataFilePath,
        String modelClassPath,
        String modelFilePath,
        ResourceLoader resourceLoader
    ) {
        this.trainingDataClassPath = trainingDataClassPath;
        this.trainingDataFilePath = trainingDataFilePath;
        this.modelClassPath = modelClassPath;
        this.modelFilePath = modelFilePath;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Ensures that the model exists. If the model file is missing, a new model is trained.
     *
     * @param modelType The model type to train.
     * @return TokenNameFinderModel instance for the specific model type.
     * @throws IOException If an I/O error occurs.
     */
    public TokenNameFinderModel ensureModel(ModelTypes modelType) throws IOException {
        System.out.println("##################################EnsureModel Method _ starting... cl ");
        //        if (Objects.isNull(modelFilePath)) {
        //            throw new IllegalStateException("Model file path is null for model type: " + modelType);
        //        }
        //
        //        InputStream modelInputStream;
        //        InputStream trainingDataInputStream;
        //
        //        System.out.println("##################################EnsureModel Method _ before _ synchronized block : ");
        //
        //        synchronized (this) {
        ////            modelFile = new File(modelFilePath);
        //            trainingDataInputStream = resolveClasspathResource(trainingDataPath);
        //            modelInputStream = resolveClasspathResource(modelFilePath);
        //
        //        }
        //        System.out.println("##################################EnsureModel Method _ after _ synchronized block : ");
        //
        //        if (Objects.nonNull(modelInputStream)) {
        //            // TODO: LOG - Loading existing model for: modelType
        //            System.out.println("##################################EnsureModel Method _ calling _ loadExistingModel Method _ file : " + trainingDataInputStream.markSupported());
        //            return loadExistingModel(modelInputStream);
        //        } else {
        //            // TODO: LOG - Training new model for: modelType
        //            System.out.println("##################################EnsureModel Method _ calling _ trainNewModel Method _ file : " + trainingDataInputStream.markSupported());
        //            return trainNewModel(modelType, trainingDataInputStream);
        //        }

        if (Objects.isNull(modelClassPath)) {
            throw new IllegalStateException("Model file path is null for model type: " + modelType);
        }

        InputStream trainingDataInputStream = resolveClasspathResource(trainingDataClassPath).orElseThrow(() ->
            new IllegalStateException("Training data input stream is null for path: " + trainingDataClassPath)
        );

        // Resolve the model input stream
        Optional<InputStream> modelInputStream = resolveClasspathResource(modelClassPath);

        if (modelInputStream.isPresent()) {
            // Use orElseThrow with a custom exception supplier
            return loadExistingModel(
                modelInputStream.orElseThrow(() -> new IllegalStateException("Model input stream is null for path: " + modelClassPath))
            );
        } else {
            return trainNewModel(modelType, trainingDataInputStream);
        }
    }

    /**
     * Loads the existing model from the specified file.
     *
     * @param modelFile The model file.
     * @return Loaded TokenNameFinderModel.
     * @throws IOException If an I/O error occurs.
     */
    private TokenNameFinderModel loadExistingModel(InputStream modelFile) throws IOException {
        System.out.println("##################################LoadExistingModel Method _ starting...  : ");
        //try (InputStream modelStream = new FileInputStream(modelFile)) {
        //        try (InputStream modelStream = modelFile) {
        //            return new TokenNameFinderModel(modelStream);
        //        }

        System.out.println("##################################LoadExistingModel Method _ before try!  : ");
        //        try (ObjectInputStream objectInputStream = new ObjectInputStream(modelFile)) {
        try (InputStream modelStream = modelFile) {
            System.out.println("##################################LoadExistingModel Method _ after try!  : ");
            //return (TokenNameFinderModel) objectInputStream.readObject();
            return new TokenNameFinderModel(modelStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load model", e);
        }
    }

    /**
     * Trains a new model using the provided training data and saves it to the model file.
     *
     * @param modelType The model type to train (e.g., PERSON, EDUCATION, etc.).
     * @return Trained TokenNameFinderModel for the specific entity type.
     * @throws IOException If an I/O error occurs.
     */
    private TokenNameFinderModel trainNewModel(ModelTypes modelType, InputStream trainingDataFile) throws IOException {
        // TODO: LOG - trainNewModel Method _ starting...

        // Prepare training parameters (adjust as needed)
        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, 100);
        params.put(TrainingParameters.CUTOFF_PARAM, 1);

        TokenNameFinderFactory factory = new TokenNameFinderFactory();

        try (ObjectStream<NameSample> nameSampleStream = createNameSampleStream(trainingDataFile)) {
            TokenNameFinderModel model = trainEntityModel(modelType, nameSampleStream, params, factory);
            // Save the trained model
            saveModel(model, modelType);
            return model;
        } catch (URISyntaxException e) {
            // TODO: LOG - trainNewModel Method _ exception _ URISyntax-exception _ ex-message
            throw new RuntimeException("Error during model training for type: " + modelType, e);
        }
    }

    private ObjectStream<NameSample> createNameSampleStream(InputStream trainingDataFile) throws IOException {
        // TODO: LOG - createNameSampleStream Method _ starting...

        // Validate the input stream
        if (trainingDataFile == null) {
            System.out.println(
                "##################################CreateNameSampleStream Method _ exception _ illegalArgument-exception : "
            );
            throw new IllegalArgumentException("Training data input stream is null.");
        }

        if (!trainingDataFile.markSupported()) {
            System.out.println("##################################CreateNameSampleStream Method _ exception _ io-exception : ");
            throw new IOException("Training data input stream does not support mark/reset.");
        }

        System.out.println("##################################CreateNameSampleStream Method _ inputStream _ before use : ");
        // Use InputStreamFactory that returns the training data file InputStream
        InputStreamFactory inputStreamFactory = () -> trainingDataFile;
        System.out.println("##################################CreateNameSampleStream Method _ inputStream _ after use : ");

        System.out.println("##################################CreateNameSampleStream Method _ inputStream _ before try-catch : ");
        // Create the line stream and name sample stream but don't close them here
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        NameSampleDataStream nameSampleDataStream = new NameSampleDataStream(lineStream);

        System.out.println("##################################CreateNameSampleStream Method _ inputStream _ after stream creation : ");
        // Return the nameSampleDataStream without closing it
        return nameSampleDataStream;
    }

    private TokenNameFinderModel trainEntityModel(
        ModelTypes modelType,
        ObjectStream<NameSample> nameSampleStream,
        TrainingParameters params,
        TokenNameFinderFactory factory
    ) throws IOException {
        return NameFinderME.train("en", modelType.name(), nameSampleStream, params, factory);
    }

    private void saveModel(TokenNameFinderModel model, ModelTypes modelType) throws IOException, URISyntaxException {
        // TODO: LOG - saveModel Method _ starting...

        // Resolve the target file path by removing 'classpath:' and preparing the absolute path
        String updatedModelFilePath = modelFilePath.replace("classpath:", "").trim();
        Path modelPath = Paths.get(updatedModelFilePath);

        // Construct the new file name for the entity type
        Path modelFileForEntityTypePath = modelPath.resolveSibling(formatModelFileName(modelPath.getFileName().toString(), modelType));
        modelFileForEntityTypePath = Path.of(modelPath.toString().replace("\\", "/"));

        // Ensure the directory structure exists
        Files.createDirectories(modelFileForEntityTypePath.getParent());

        // Serialize and save the model
        try (OutputStream modelOut = new FileOutputStream(modelFileForEntityTypePath.toFile())) {
            model.serialize(modelOut);
            System.out.println("################################## SaveModel Method _ Model saved to path: " + modelFileForEntityTypePath);
        }

        // Update the modelFilePath to the new location
        synchronized (this) {
            updatedModelFilePath = modelFileForEntityTypePath.toString();
        }
        // Log the successful save
        // TODO: LOG - saveModel Method _ completed _ new model saved successfully!
    }

    private String formatModelFileName(String modelFilePath, ModelTypes modelType) {
        Map<ModelTypes, String> modelTypeMapping = Map.of(
            ModelTypes.PERSON,
            "person",
            ModelTypes.EDUCATION,
            "education",
            ModelTypes.COMPANY,
            "company",
            ModelTypes.LOCATION,
            "location",
            ModelTypes.SKILLS,
            "skills"
        );

        String suffix = modelTypeMapping.getOrDefault(modelType, modelType.name().toLowerCase());
        Path originalPath = Paths.get(modelFilePath);
        String baseName = originalPath.getFileName().toString();

        // Check if the base name already includes the suffix
        if (baseName.contains("-" + suffix + ".bin")) {
            return modelFilePath;
        }

        // Append the suffix and return the updated file path
        String updatedFileName = baseName.replace(".bin", "-" + suffix + ".bin");
        return originalPath.getParent() != null ? originalPath.getParent().resolve(updatedFileName).toString() : updatedFileName;
    }

    public NameFinderME getNameFinder(ModelTypes modelType) throws IOException {
        TokenNameFinderModel model = ensureModel(modelType);
        return new NameFinderME(model);
    }

    private Optional<InputStream> resolveClasspathResource(String path) throws IOException {
        if (path.startsWith("classpath:")) {
            // Handle classpath resource loading
            try {
                Resource resource = resourceLoader.getResource(path);
                if (!resource.exists()) {
                    System.out.println("########################Resource not found: " + path);
                    return Optional.empty();
                }

                return Optional.of(resource.getInputStream()); // Return InputStream for the classpath resource
            } catch (Exception e) {
                System.out.println("########################Error loading classpath resource: " + path);
                return Optional.empty();
            }
        }

        // Handle absolute or relative file paths
        try {
            return Optional.of(new FileInputStream(path)); // Return InputStream for the file path
        } catch (Exception e) {
            System.out.println("########################File not found: " + path);
            return Optional.empty();
        }
    }
}
