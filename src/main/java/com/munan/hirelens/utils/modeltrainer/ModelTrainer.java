package com.munan.hirelens.utils.modeltrainer;

import com.munan.hirelens.enums.ModelTypes;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import opennlp.tools.namefind.*;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class ModelTrainer {

    private String modelFilePath;
    private final String trainingDataPath;

    /**
     * Constructor to initialize ModelTrainer with model and training data paths.
     *
     * @param modelFilePath   Path to the model file.
     * @param trainingDataPath Path to the training data.
     */
    public ModelTrainer(String modelFilePath, String trainingDataPath) {
        this.modelFilePath = modelFilePath;
        this.trainingDataPath = trainingDataPath;
    }

    /**
     * Ensures that the model exists. If the model file is missing, a new model is trained.
     *
     * @param modelType The model type to train.
     * @return TokenNameFinderModel instance for the specific model type.
     * @throws IOException If an I/O error occurs.
     */
    public TokenNameFinderModel ensureModel(ModelTypes modelType) throws IOException {
        if (Objects.isNull(modelFilePath)) {
            throw new IllegalStateException("Model file path is null for model type: " + modelType);
        }

        File modelFile;
        synchronized (this) {
            modelFile = new File(modelFilePath);
        }

        if (modelFile.exists()) {
            // TODO: LOG - Loading existing model for: modelType
            return loadExistingModel(modelFile);
        } else {
            // TODO: LOG - Training new model for: modelType
            return trainNewModel(modelType);
        }
    }

    /**
     * Loads the existing model from the specified file.
     *
     * @param modelFile The model file.
     * @return Loaded TokenNameFinderModel.
     * @throws IOException If an I/O error occurs.
     */
    private TokenNameFinderModel loadExistingModel(File modelFile) throws IOException {
        try (InputStream modelStream = new FileInputStream(modelFile)) {
            return new TokenNameFinderModel(modelStream);
        }
    }

    /**
     * Trains a new model using the provided training data and saves it to the model file.
     *
     * @param modelType The model type to train (e.g., PERSON, EDUCATION, etc.).
     * @return Trained TokenNameFinderModel for the specific entity type.
     * @throws IOException If an I/O error occurs.
     */
    private TokenNameFinderModel trainNewModel(ModelTypes modelType) throws IOException {
        // Prepare training parameters (adjust as needed)
        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, 100);
        params.put(TrainingParameters.CUTOFF_PARAM, 1);

        // Set up the TokenNameFinderFactory
        TokenNameFinderFactory factory = new TokenNameFinderFactory();

        // Train the model for the specified entity type
        try (ObjectStream<NameSample> nameSampleStream = createNameSampleStream(modelType)) {
            TokenNameFinderModel model = trainEntityModel(modelType, nameSampleStream, params, factory);
            saveModel(model, modelType); // Save model with the entity type name
            return model;
        }
    }

    private ObjectStream<NameSample> createNameSampleStream(ModelTypes modelType) throws IOException {
        try (InputStream trainingDataStream = new FileInputStream(trainingDataPath)) {
            return new NameSampleDataStream(new PlainTextByLineStream(() -> new FileInputStream(trainingDataPath), "UTF-8"));
        }
    }

    private TokenNameFinderModel trainEntityModel(
        ModelTypes modelType,
        ObjectStream<NameSample> nameSampleStream,
        TrainingParameters params,
        TokenNameFinderFactory factory
    ) throws IOException {
        return NameFinderME.train("en", modelType.name(), nameSampleStream, params, factory);
    }

    private void saveModel(TokenNameFinderModel model, ModelTypes modelType) throws IOException {
        Path modelPath = Paths.get(modelFilePath);
        Path modelFileForEntityTypePath = modelPath.resolveSibling(formatModelFileName(modelPath.getFileName().toString(), modelType));

        try (OutputStream modelOut = new FileOutputStream(modelFileForEntityTypePath.toFile())) {
            model.serialize(modelOut);
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
}
