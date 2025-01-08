package com.munan.hirelens.service.languageModel;

import com.munan.hirelens.config.opennlp.NLPProperties;
import com.munan.hirelens.enums.ModelTypes;
import com.munan.hirelens.utils.modeltrainer.ModelTrainer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class ModelTrainerService {

    private final Map<ModelTypes, ModelTrainer> modelTrainerMap = new HashMap<>();

    public ModelTrainerService(NLPProperties nlpProperties) {
        validatePaths(nlpProperties);
        // Initialize model trainers dynamically based on ModelTypes
        modelTrainerMap.put(
            ModelTypes.PERSON,
            new ModelTrainer(nlpProperties.getTrainingModel().getPath().getPerson(), nlpProperties.getTrainingData().getPath().getPerson())
        );
        modelTrainerMap.put(
            ModelTypes.EDUCATION,
            new ModelTrainer(
                nlpProperties.getTrainingModel().getPath().getEducation(),
                nlpProperties.getTrainingData().getPath().getEducation()
            )
        );
        modelTrainerMap.put(
            ModelTypes.COMPANY,
            new ModelTrainer(
                nlpProperties.getTrainingModel().getPath().getCompany(),
                nlpProperties.getTrainingData().getPath().getCompany()
            )
        );
        modelTrainerMap.put(
            ModelTypes.LOCATION,
            new ModelTrainer(
                nlpProperties.getTrainingModel().getPath().getLocation(),
                nlpProperties.getTrainingData().getPath().getLocation()
            )
        );
        modelTrainerMap.put(
            ModelTypes.SKILLS,
            new ModelTrainer(
                nlpProperties.getTrainingModel().getPath().getSkills(),
                nlpProperties.getTrainingData().getPath().getLocation()
            )
        );
    }

    /**
     * Iteratively trains models for multiple entity types (PERSON, LOCATION, SKILLS, COMPANY, EDUCATION).
     */
    public void trainAllModels() throws IOException {
        for (ModelTypes modelType : ModelTypes.values()) {
            // Fetch the appropriate trainer and train the model
            ModelTrainer trainer = modelTrainerMap.get(modelType);

            if (Objects.nonNull(trainer)) {
                // TODO: LOG - Training model for: {modelType.name()}
                trainer.ensureModel(modelType);
                // TODO: LOG - Model trained for entity: {modelType.name()}
            } else {
                // TODO: LOG - No trainer found for model type: {modelType.name()}
            }
        }
    }

    private void validatePaths(NLPProperties nlpProperties) {
        if (
            Objects.isNull(nlpProperties.getTrainingModel().getPath().getPerson()) ||
            Objects.isNull(nlpProperties.getTrainingData().getPath().getPerson())
        ) {
            throw new IllegalStateException("Model or training data path is null for model type: PERSON");
        }
        if (
            Objects.isNull(nlpProperties.getTrainingModel().getPath().getEducation()) ||
            Objects.isNull(nlpProperties.getTrainingData().getPath().getEducation())
        ) {
            throw new IllegalStateException("Model or training data path is null for model type: EDUCATION");
        }
        if (
            Objects.isNull(nlpProperties.getTrainingModel().getPath().getCompany()) ||
            Objects.isNull(nlpProperties.getTrainingData().getPath().getCompany())
        ) {
            throw new IllegalStateException("Model or training data path is null for model type: COMPANY");
        }
        if (
            Objects.isNull(nlpProperties.getTrainingModel().getPath().getLocation()) ||
            Objects.isNull(nlpProperties.getTrainingData().getPath().getLocation())
        ) {
            throw new IllegalStateException("Model or training data path is null for model type: LOCATION");
        }
        if (
            Objects.isNull(nlpProperties.getTrainingModel().getPath().getSkills()) ||
            Objects.isNull(nlpProperties.getTrainingData().getPath().getSkills())
        ) {
            throw new IllegalStateException("Model or training data path is null for model type: SKILLS");
        }
    }
}
