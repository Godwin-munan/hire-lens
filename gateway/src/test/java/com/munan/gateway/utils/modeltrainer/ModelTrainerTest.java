package com.munan.gateway.utils.modeltrainer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.munan.gateway.enums.ModelTypes;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import opennlp.tools.namefind.TokenNameFinderModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class ModelTrainerTest {

    private String TRAINING_DATA_CLASS_PATH = "classpath:data/test/person.training.data.train";
    private String TRAINING_DATA_FILE_PATH = "src/main/resources/data/test/person.training.data.train";
    private String MODEL_CLASS_PATH = "classpath:models/test/en-ner-person.bin";
    private String MODEL_FILE_PATH = "src/main/resources/test/models/en-ner-person.bin";

    private AutoCloseable closeable;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource modelResource;

    @Mock
    private Resource dataResource;

    @Mock
    private InputStream mockModelInputStream;

    @Mock
    private InputStream mockTrainingDataInputStream;

    @Mock
    private TokenNameFinderModel mockModel;

    private ModelTrainer modelTrainer;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        modelTrainer = new ModelTrainer(
            TRAINING_DATA_CLASS_PATH,
            TRAINING_DATA_FILE_PATH,
            MODEL_CLASS_PATH,
            MODEL_FILE_PATH,
            resourceLoader
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        modelTrainer = null;
    }

    @Test
    void testEnsureModelTrainingNewModelWhenModelNotFound() throws IOException {
        // Mock resolveClasspathResource to return empty for model
        Optional<InputStream> optionalTrainingDataStream = Optional.of(mockTrainingDataInputStream);
        Optional<InputStream> optionalModelStream = Optional.empty(); // Model not found

        ModelTrainer spyTrainer = spy(modelTrainer);
        doReturn(optionalTrainingDataStream).when(spyTrainer).resolveClasspathResource(TRAINING_DATA_CLASS_PATH);
        doReturn(optionalModelStream).when(spyTrainer).resolveClasspathResource(MODEL_CLASS_PATH);

        doReturn(mockModel).when(spyTrainer).trainNewModel(eq(ModelTypes.PERSON), any());

        TokenNameFinderModel result = spyTrainer.ensureModel(ModelTypes.PERSON);
        assertNotNull(result);
        verify(spyTrainer, times(1)).trainNewModel(eq(ModelTypes.PERSON), any());
    }

    @Test
    void testEnsureModelLoadsExistingModel() throws IOException {
        // Mock resolveClasspathResource to return model input stream
        when(resourceLoader.getResource(MODEL_CLASS_PATH)).thenReturn(modelResource);
        when(resourceLoader.getResource(TRAINING_DATA_CLASS_PATH)).thenReturn(dataResource);

        Optional<InputStream> optionalModelStream = Optional.of(mockModelInputStream);
        Optional<InputStream> optionalTrainingDataStream = Optional.of(mockTrainingDataInputStream);

        ModelTrainer spyTrainer = spy(modelTrainer);

        doReturn(optionalTrainingDataStream).when(spyTrainer).resolveClasspathResource(TRAINING_DATA_CLASS_PATH);
        doReturn(optionalModelStream).when(spyTrainer).resolveClasspathResource(MODEL_CLASS_PATH);
        doReturn(mockModel).when(spyTrainer).loadExistingModel(mockModelInputStream);

        TokenNameFinderModel result = spyTrainer.ensureModel(ModelTypes.PERSON);
        assertNotNull(result);
        verify(spyTrainer, times(1)).loadExistingModel(mockModelInputStream);
    }

    @Test
    void testEnsureModelModelClassPathIsNull() throws IOException {
        ModelTrainer modelTrainer = new ModelTrainer(
            TRAINING_DATA_CLASS_PATH,
            TRAINING_DATA_FILE_PATH,
            null,
            MODEL_FILE_PATH,
            resourceLoader
        );
        ModelTrainer spyTrainer = spy(modelTrainer);

        // Assert that ensureModel() throws an IllegalStateException
        IllegalStateException thrownException = assertThrows(IllegalStateException.class, () -> {
            spyTrainer.ensureModel(ModelTypes.PERSON);
        });

        // Verify the exception message
        assertEquals("Model file path is null for model type: " + ModelTypes.PERSON, thrownException.getMessage());
    }

    @Test
    void testEnsureModelTrainingDataClassPathIsNull() throws IOException {
        when(resourceLoader.getResource(TRAINING_DATA_CLASS_PATH)).thenReturn(dataResource);
        ModelTrainer spyTrainer = spy(modelTrainer);

        Optional<InputStream> optionalTrainingDataStream = Optional.empty();

        doReturn(optionalTrainingDataStream).when(spyTrainer).resolveClasspathResource(TRAINING_DATA_CLASS_PATH);

        // Assert that ensureModel() throws an IllegalStateException
        IllegalStateException thrownException = assertThrows(IllegalStateException.class, () -> {
            spyTrainer.ensureModel(ModelTypes.PERSON);
        });

        // Verify the exception message
        assertEquals("Training data input stream is null for path: " + TRAINING_DATA_CLASS_PATH, thrownException.getMessage());
    }
}
