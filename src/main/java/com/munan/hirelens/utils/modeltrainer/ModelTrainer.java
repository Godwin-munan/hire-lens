package com.munan.hirelens.utils.modeltrainer;

import com.munan.hirelens.enums.ModelTypes;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import opennlp.tools.namefind.*;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class ModelTrainer {

    private String modelFilePath;
    private final String trainingDataPath;
    private final ResourceLoader resourceLoader;

    /**
     * Constructor to initialize ModelTrainer with model and training data paths.
     *
     * @param modelFilePath   Path to the model file.
     * @param trainingDataPath Path to the training data.
     */
    public ModelTrainer(String modelFilePath, String trainingDataPath, ResourceLoader resourceLoader) {
        this.modelFilePath = modelFilePath;
        this.trainingDataPath = trainingDataPath;
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

        if (Objects.isNull(modelFilePath)) {
            throw new IllegalStateException("Model file path is null for model type: " + modelType);
        }

        InputStream trainingDataInputStream = resolveClasspathResource(trainingDataPath);
        if (trainingDataInputStream == null) {
            throw new IllegalStateException("Training data input stream is null for path: " + trainingDataPath);
        }

        InputStream modelInputStream = resolveClasspathResource(modelFilePath);

        if (modelInputStream != null) {
            return loadExistingModel(modelInputStream);
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
        //try (InputStream modelStream = new FileInputStream(modelFile)) {
        //        try (InputStream modelStream = modelFile) {
        //            return new TokenNameFinderModel(modelStream);
        //        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(modelFile)) {
            return (TokenNameFinderModel) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
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
        System.out.println("##################################TrainNewModel Method _ starting...  : ");
        // Prepare training parameters (adjust as needed)
        //        TrainingParameters params = new TrainingParameters();
        //        params.put(TrainingParameters.ITERATIONS_PARAM, 70);
        //        params.put(TrainingParameters.CUTOFF_PARAM, 5);
        //
        //        // Set up the TokenNameFinderFactory
        //        TokenNameFinderFactory factory = new TokenNameFinderFactory();
        //
        //        // Train the model for the specified entity type
        //        try (ObjectStream<NameSample> nameSampleStream = createNameSampleStream(modelType, trainingDataFile)) {
        //            TokenNameFinderModel model = trainEntityModel(modelType, nameSampleStream, params, factory);
        //            System.out.println("##################################TrainNewModel Method _ calling _ saveModel Method _ file loaded : " + model.isLoadedFromSerialized());
        //            saveModel(model, modelType); // Save model with the entity type name
        //            return model;
        //        }

        // Prepare training parameters (adjust as needed)
        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, 70);
        params.put(TrainingParameters.CUTOFF_PARAM, 5);

        TokenNameFinderFactory factory = new TokenNameFinderFactory();

        // Use try-with-resources to ensure proper resource management
        try (ObjectStream<NameSample> nameSampleStream = createNameSampleStream(modelType, trainingDataFile)) {
            // Train the model
            //            NameSample sample;
            //            while ((sample = nameSampleStream.read()) != null) {
            //                System.out.println("Sample: " + sample.toString());
            //            }
            TokenNameFinderModel model = trainEntityModel(modelType, nameSampleStream, params, factory);
            // Save the trained model
            saveModel(model, modelType);
            return model;
        } catch (Exception e) {
            // Catch IOException here, since it might occur during stream handling or model training
            throw new RuntimeException("Error during model training for type: " + modelType, e);
        }
    }

    private ObjectStream<NameSample> createNameSampleStream(ModelTypes modelType, InputStream trainingDataFile) throws IOException {
        System.out.println("##################################CreateNameSampleStream Method _ starting... : ");
        //        try (InputStream inputStream = trainingDataFile;
        //             ObjectStream<String> lineStream = new PlainTextByLineStream(inputStream, "UTF-8");
        //             NameSampleDataStream nameSampleDataStream = new NameSampleDataStream(lineStream)) {

        //InputStreamFactory inputStreamFactory = null;

        //        // Validate the input stream
        //        if (trainingDataFile == null) {
        //            System.out.println("##################################CreateNameSampleStream Method _ exception _ illegalArgument-exception : ");
        //            throw new IllegalArgumentException("Training data input stream is null.");
        //        }
        //
        //        if (!trainingDataFile.markSupported()) {
        //            System.out.println("##################################CreateNameSampleStream Method _ exception _ io-exception : ");
        //            throw new IOException("Training data input stream does not support mark/reset.");
        //        }
        //
        //        System.out.println("##################################CreateNameSampleStream Method _ inputStream _ before use : ");
        //        // Use InputStreamFactory that returns the training data file InputStream
        //        InputStreamFactory inputStreamFactory = () -> trainingDataFile;
        //        System.out.println("##################################CreateNameSampleStream Method _ inputStream _ after use : ");
        //
        //        System.out.println("##################################CreateNameSampleStream Method _ inputStream _ before try-catch : ");
        //        // Try-with-resources to ensure stream is managed correctly
        //        try (ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        //             NameSampleDataStream nameSampleDataStream = new NameSampleDataStream(lineStream)) {
        //            System.out.println("##################################CreateNameSampleStream Method _ inputStream _ inside try : ");
        //            return nameSampleDataStream;
        //        } catch (Exception e) {
        //            // Handle IOException that may occur during stream creation
        //            System.out.println("##################################CreateNameSampleStream Method _ exception _ runtime-exception : ");
        //            throw new RuntimeException("Error creating NameSampleDataStream for model type: " + modelType, e);
        //        }

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
        //Path modelPath = Paths.get(modelFilePath);
        Path modelPath = Paths.get(getClass().getClassLoader().getResource("models/en-ner-person.bin").toURI());
        //Path modelPath = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("models/en-ner-person.bin")).toURI());
        //Path modelPath = resourceLoader.getResource(modelFilePath).getFile().toPath();
        Path modelFileForEntityTypePath = modelPath.resolveSibling(formatModelFileName(modelPath.getFileName().toString(), modelType));

        System.out.println("##################################SaveModel Method _ modal-path : " + modelPath.getRoot());
        try (OutputStream modelOut = new FileOutputStream(modelFileForEntityTypePath.toFile())) {
            model.serialize(modelOut);
            System.out.println("##################################SaveModel Method _ Save model to path : " + modelPath.getRoot());
        }

        synchronized (this) {
            this.modelFilePath = modelFileForEntityTypePath.toString();
        }
        // TODO: LOG - {modelType.name()} model trained and saved as: {modelFileForEntityTypePath}
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

    private InputStream resolveClasspathResource(String path) throws IOException {
        if (path.startsWith("classpath:")) {
            // Handle classpath resource loading
            try {
                Resource resource = resourceLoader.getResource(path);
                if (!resource.exists()) {
                    System.out.println("########################Resource not found: " + path);
                    return null;
                }

                return resource.getInputStream(); // Return InputStream for the classpath resource
            } catch (Exception e) {
                System.out.println("########################Error loading classpath resource: " + path);
                return null;
            }
        }

        // Handle absolute or relative file paths
        try {
            return new FileInputStream(path); // Return InputStream for the file path
        } catch (Exception e) {
            System.out.println("########################File not found: " + path);
            return null;
        }
    }
}
