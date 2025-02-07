package com.munan.gateway.service.languageModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.munan.gateway.config.opennlp.NLPProperties;
import com.munan.gateway.enums.ModelTypes;
import com.munan.gateway.utils.modeltrainer.ModelTrainer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import opennlp.tools.namefind.TokenNameFinderModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

public class ModelTrainerServiceTest {

    private ModelTrainerService modelTrainerService;

    @BeforeEach
    void setup() {
        // Create mocks for dependencies
        ResourceLoader resourceLoader = mock(ResourceLoader.class);
        NLPProperties nlpProperties = mock(NLPProperties.class);

        // Set up dummy stubs for the required path properties so that validatePaths doesn't throw.
        // Assume these nested objects exist (or create minimal dummy implementations).
        NLPProperties.TrainingData trainingData = mock(NLPProperties.TrainingData.class);
        NLPProperties.TrainingData.ClassPath trainingDataClassPath = mock(NLPProperties.TrainingData.ClassPath.class);
        NLPProperties.TrainingData.FilePath trainingDataFilePath = mock(NLPProperties.TrainingData.FilePath.class);

        when(trainingDataClassPath.getPerson()).thenReturn("dummyTrainingDataClassPath");
        when(trainingDataFilePath.getPerson()).thenReturn("dummyTrainingDataFilePath");
        when(trainingData.getClassPath()).thenReturn(trainingDataClassPath);
        when(trainingData.getFilePath()).thenReturn(trainingDataFilePath);

        NLPProperties.TrainingModel trainingModel = mock(NLPProperties.TrainingModel.class);
        NLPProperties.TrainingData.ClassPath trainingModelClassPath = mock(NLPProperties.TrainingData.ClassPath.class);
        NLPProperties.TrainingData.FilePath trainingModelFilePath = mock(NLPProperties.TrainingData.FilePath.class);

        when(trainingModelClassPath.getPerson()).thenReturn("dummyTrainingModelClassPath");
        when(trainingModelFilePath.getPerson()).thenReturn("dummyTrainingModelFilePath");
        when(trainingModel.getClassPath()).thenReturn(trainingModelClassPath);
        when(trainingModel.getFilePath()).thenReturn(trainingModelFilePath);

        when(nlpProperties.getTrainingData()).thenReturn(trainingData);
        when(nlpProperties.getTrainingModel()).thenReturn(trainingModel);

        // Create the service under test
        modelTrainerService = new ModelTrainerService(nlpProperties, resourceLoader);
    }

    @Test
    void testTrainAllModelsMethodToAddModelsToModelTrainerMap() throws IOException {
        // Create a dummy model to be returned by our stubbed trainer
        TokenNameFinderModel dummyModel = mock(TokenNameFinderModel.class);
        // Create a stubbed ModelTrainer and stub its ensureModel method
        ModelTrainer stubTrainer = mock(ModelTrainer.class);
        when(stubTrainer.ensureModel(ModelTypes.PERSON)).thenReturn(dummyModel);

        // Replace the internal modelTrainerMap in the service with our custom map containing only the stubTrainer.
        Map<ModelTypes, ModelTrainer> customTrainerMap = new HashMap<>();
        customTrainerMap.put(ModelTypes.PERSON, stubTrainer);
        ReflectionTestUtils.setField(modelTrainerService, "modelTrainerMap", customTrainerMap);

        // Act: call the method under test
        modelTrainerService.trainAllModels();

        // Assert: the modelMap should now contain an entry for PERSON
        Map<ModelTypes, TokenNameFinderModel> resultModelMap = modelTrainerService.getModelMap();
        assertNotNull(resultModelMap.get(ModelTypes.PERSON), "The PERSON model should be trained and added.");
        assertEquals(dummyModel, resultModelMap.get(ModelTypes.PERSON), "The returned model should match the dummy model.");
    }
}
