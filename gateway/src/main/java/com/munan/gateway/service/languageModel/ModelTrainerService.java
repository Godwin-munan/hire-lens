package com.munan.gateway.service.languageModel;

import com.munan.gateway.config.opennlp.NLPProperties;
import com.munan.gateway.enums.ModelTypes;
import com.munan.gateway.utils.modeltrainer.ModelTrainer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import opennlp.tools.namefind.TokenNameFinderModel;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class ModelTrainerService {

    private final Map<ModelTypes, ModelTrainer> modelTrainerMap = new HashMap<>();

    @Getter
    private final Map<ModelTypes, TokenNameFinderModel> modelMap = new ConcurrentHashMap<>();

    public ModelTrainerService(NLPProperties nlpProperties, ResourceLoader resourceLoader) {
        validatePaths(nlpProperties);

        // Initialize model trainers dynamically based on ModelTypes
        modelTrainerMap.put(
            ModelTypes.PERSON,
            new ModelTrainer(
                nlpProperties.getTrainingData().getClassPath().getPerson(),
                nlpProperties.getTrainingData().getFilePath().getPerson(),
                nlpProperties.getTrainingModel().getClassPath().getPerson(),
                nlpProperties.getTrainingModel().getFilePath().getPerson(),
                resourceLoader
            )
        );

        modelTrainerMap.put(
            ModelTypes.SKILLS,
            new ModelTrainer(
                nlpProperties.getTrainingData().getClassPath().getSkills(),
                nlpProperties.getTrainingData().getFilePath().getSkills(),
                nlpProperties.getTrainingModel().getClassPath().getSkills(),
                nlpProperties.getTrainingModel().getFilePath().getSkills(),
                resourceLoader
            )
        );
        //        modelTrainerMap.put(
        //            ModelTypes.EDUCATION,
        //            new ModelTrainer(
        //                nlpProperties.getTrainingModel().getPath().getEducation(),
        //                nlpProperties.getTrainingData().getPath().getEducation(),
        //                resourceLoader
        //            )
        //        );
        //        modelTrainerMap.put(
        //            ModelTypes.COMPANY,
        //            new ModelTrainer(
        //                nlpProperties.getTrainingModel().getPath().getCompany(),
        //                nlpProperties.getTrainingData().getPath().getCompany(),
        //                resourceLoader
        //            )
        //        );
        //        modelTrainerMap.put(
        //            ModelTypes.LOCATION,
        //            new ModelTrainer(
        //                nlpProperties.getTrainingModel().getPath().getLocation(),
        //                nlpProperties.getTrainingData().getPath().getLocation(),
        //                resourceLoader
        //            )
        //        );
        //        modelTrainerMap.put(
        //            ModelTypes.SKILLS,
        //            new ModelTrainer(
        //                nlpProperties.getTrainingModel().getPath().getSkills(),
        //                nlpProperties.getTrainingData().getPath().getLocation(),
        //                resourceLoader
        //            )
        //        );
    }

    /**
     * Iteratively trains models for multiple entity types (PERSON, LOCATION, SKILLS, COMPANY, EDUCATION).
     */
    public void trainAllModels() throws IOException {
        for (ModelTypes modelType : ModelTypes.values()) {
            // Fetch the appropriate trainer and train the model
            ModelTrainer trainer = modelTrainerMap.get(modelType);

            if (Objects.nonNull(trainer)) {
                TokenNameFinderModel model = trainer.ensureModel(modelType);
                modelMap.put(modelType, model);
                // TODO: LOG - Model trained for entity: {modelType.name()}
            } else {
                // TODO: LOG - No trainer found for model type: {modelType.name()}
            }
        }
    }

    private void validatePaths(NLPProperties nlpProperties) {
        if (
            Objects.isNull(nlpProperties.getTrainingData().getClassPath().getPerson()) ||
            Objects.isNull(nlpProperties.getTrainingData().getFilePath().getPerson()) ||
            Objects.isNull(nlpProperties.getTrainingModel().getClassPath().getPerson()) ||
            Objects.isNull(nlpProperties.getTrainingModel().getFilePath().getPerson())
        ) {
            throw new IllegalStateException("Model or training data path is null for model type: PERSON");
        }
        //        if (
        //            Objects.isNull(nlpProperties.getTrainingModel().getPath().getEducation()) ||
        //            Objects.isNull(nlpProperties.getTrainingData().getPath().getEducation())
        //        ) {
        //            throw new IllegalStateException("Model or training data path is null for model type: EDUCATION");
        //        }
        //        if (
        //            Objects.isNull(nlpProperties.getTrainingModel().getPath().getCompany()) ||
        //            Objects.isNull(nlpProperties.getTrainingData().getPath().getCompany())
        //        ) {
        //            throw new IllegalStateException("Model or training data path is null for model type: COMPANY");
        //        }
        //        if (
        //            Objects.isNull(nlpProperties.getTrainingModel().getPath().getLocation()) ||
        //            Objects.isNull(nlpProperties.getTrainingData().getPath().getLocation())
        //        ) {
        //            throw new IllegalStateException("Model or training data path is null for model type: LOCATION");
        //        }
        if (
            Objects.isNull(nlpProperties.getTrainingData().getClassPath().getSkills()) ||
            Objects.isNull(nlpProperties.getTrainingData().getFilePath().getSkills()) ||
            Objects.isNull(nlpProperties.getTrainingModel().getClassPath().getSkills()) ||
            Objects.isNull(nlpProperties.getTrainingModel().getFilePath().getSkills())
        ) {
            throw new IllegalStateException("Model or training data path is null for model type: SKILLS");
        }
    }
}
