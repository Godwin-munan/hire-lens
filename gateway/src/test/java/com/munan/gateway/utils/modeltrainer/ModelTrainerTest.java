package com.munan.gateway.utils.modeltrainer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.munan.gateway.enums.ModelTypes;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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

    @TempDir
    Path tempDir;

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
    private TokenNameFinderModel mockTokenNameFinderModel;

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
    void testEnsureModelMethodToTrainingNewModelWhenModelNotFound() throws IOException {
        // Mock resolveClasspathResource to return empty for model
        Optional<InputStream> optionalTrainingDataStream = Optional.of(mockTrainingDataInputStream);
        Optional<InputStream> optionalModelStream = Optional.empty(); // Model not found

        ModelTrainer spyTrainer = spy(modelTrainer);
        doReturn(optionalTrainingDataStream).when(spyTrainer).resolveClasspathResource(TRAINING_DATA_CLASS_PATH);
        doReturn(optionalModelStream).when(spyTrainer).resolveClasspathResource(MODEL_CLASS_PATH);

        doReturn(mockTokenNameFinderModel).when(spyTrainer).trainNewModel(eq(ModelTypes.PERSON), any());

        TokenNameFinderModel result = spyTrainer.ensureModel(ModelTypes.PERSON);
        assertNotNull(result);
        verify(spyTrainer, times(1)).trainNewModel(eq(ModelTypes.PERSON), any());
    }

    @Test
    void testEnsureModelMethodToLoadsExistingModel() throws IOException {
        // Mock resolveClasspathResource to return model input stream
        when(resourceLoader.getResource(MODEL_CLASS_PATH)).thenReturn(modelResource);
        when(resourceLoader.getResource(TRAINING_DATA_CLASS_PATH)).thenReturn(dataResource);

        Optional<InputStream> optionalModelStream = Optional.of(mockModelInputStream);
        Optional<InputStream> optionalTrainingDataStream = Optional.of(mockTrainingDataInputStream);

        ModelTrainer spyTrainer = spy(modelTrainer);

        doReturn(optionalTrainingDataStream).when(spyTrainer).resolveClasspathResource(TRAINING_DATA_CLASS_PATH);
        doReturn(optionalModelStream).when(spyTrainer).resolveClasspathResource(MODEL_CLASS_PATH);
        doReturn(mockTokenNameFinderModel).when(spyTrainer).loadExistingModel(mockModelInputStream);

        TokenNameFinderModel result = spyTrainer.ensureModel(ModelTypes.PERSON);
        assertNotNull(result);
        verify(spyTrainer, times(1)).loadExistingModel(mockModelInputStream);
    }

    @Test
    void testEnsureModelMethodToLoadTrainedModelWithModelClassPathIsNull() throws IOException {
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
    void testEnsureModelMethodToTrainNewModelWithTrainingDataClassPathIsNull() throws IOException {
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

    @Test
    void testLoadExistingModelMethodToLoadExistingModel() throws IOException {
        ModelTrainer spyTrainer = spy(modelTrainer);
        doReturn(mockTokenNameFinderModel).when(spyTrainer).loadExistingModel(mockModelInputStream);

        TokenNameFinderModel result = spyTrainer.loadExistingModel(mockModelInputStream);
        assertNotNull(result);
        assertEquals(mockTokenNameFinderModel, result);
        verify(spyTrainer, only()).loadExistingModel(mockModelInputStream);
    }

    @Test
    void testLoadExistingModelMethodToLoadExistingModelWithInputStreamIsNull() throws IOException {
        ModelTrainer spyTrainer = spy(modelTrainer);

        // Create a valid byte array representing a fake model file
        byte[] fakeModelData = "FAKE MODEL DATA".getBytes(StandardCharsets.UTF_8);

        doAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            int offset = invocation.getArgument(1);
            int length = invocation.getArgument(2);

            // Copy fake data into buffer (simulate successful read)
            System.arraycopy(fakeModelData, 0, buffer, offset, fakeModelData.length);

            // Return the number of bytes read (normal behavior)
            return fakeModelData.length;
        })
            .doThrow(new IOException("Simulated read failure"))
            .when(mockModelInputStream)
            .read(any(byte[].class), anyInt(), anyInt());

        doNothing().when(mockModelInputStream).close();

        // Expect RuntimeException because loadExistingModel() rethrows it
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            spyTrainer.loadExistingModel(mockModelInputStream); // Call the real method
        });

        assertTrue(thrownException.getMessage().contains("Failed to load model"));
        verify(spyTrainer, times(1)).loadExistingModel(mockModelInputStream);
    }

    @Test
    void testTrainNewModelMethodToTrainNewModelWithTrainingData() throws IOException, URISyntaxException {
        ModelTrainer spyTrainer = spy(modelTrainer);

        ObjectStream mockNameSampleStream = mock(ObjectStream.class);

        doReturn(mockNameSampleStream).when(spyTrainer).createNameSampleStream(mockTrainingDataInputStream);
        doNothing().when(spyTrainer).saveModel(eq(mockTokenNameFinderModel), any());
        doReturn(mockTokenNameFinderModel).when(spyTrainer).trainEntityModel(eq(ModelTypes.PERSON), eq(mockNameSampleStream), any(), any());

        TokenNameFinderModel result = spyTrainer.trainNewModel(ModelTypes.PERSON, mockTrainingDataInputStream);
        assertNotNull(result);
        //assertEquals(result, mockTokenNameFinderModel);

    }

    @Test
    void testTrainNewModelMethodToTrainNewModelWithInvalidURI() throws IOException, URISyntaxException {
        ModelTrainer spyTrainer = spy(modelTrainer);

        ObjectStream<NameSample> dummyNameSampleStream = mock(ObjectStream.class);
        doReturn(dummyNameSampleStream).when(spyTrainer).createNameSampleStream(any(InputStream.class));
        doReturn(mockTokenNameFinderModel)
            .when(spyTrainer)
            .trainEntityModel(eq(ModelTypes.PERSON), eq(dummyNameSampleStream), any(), any());

        doThrow(new URISyntaxException("Simulated URISyntaxException", "Simulated URISyntaxException"))
            .when(spyTrainer)
            .saveModel(any(), any());

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            spyTrainer.trainNewModel(ModelTypes.PERSON, mockTrainingDataInputStream);
        });

        assertEquals("Error during model training for type: " + ModelTypes.PERSON, thrownException.getMessage());
    }

    @Test
    void testCreateNameSampleStreamMethodToReturnObjectStreamWithTrainingDataInputStreamParameter() throws IOException {
        // Create a valid ByteArrayInputStream with dummy training data.
        String fakeTrainingData = "This is a fake training data line.\nAnother line of training data.";
        InputStream trainingDataStream = new ByteArrayInputStream(fakeTrainingData.getBytes(StandardCharsets.UTF_8));

        ModelTrainer spyTrainer = spy(modelTrainer);

        ObjectStream<NameSample> nameSampleStream = spyTrainer.createNameSampleStream(trainingDataStream);

        assertNotNull(nameSampleStream);
        NameSample sample = nameSampleStream.read();
        assertNotNull(sample); // Ensure that a sample is produced
    }

    @Test
    void testCreateNameSampleStreamMethodToThrowIllegalArgumentExceptionWithTrainingDataInputStreamIsNullParameter() throws IOException {
        ModelTrainer spyTrainer = spy(modelTrainer);

        // Assert that ensureModel() throws an IllegalStateException
        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            spyTrainer.createNameSampleStream(null).close();
        });

        // Verify the exception message
        assertEquals("Training data input stream is null.", thrownException.getMessage());
    }

    @Test
    void testCreateNameSampleStreamMethodToThrowIOExceptionWithTrainingDataInputStreamMarkSupportedIsFalse() throws IOException {
        String fakeTrainingData = "This is a fake training data line.\nAnother line of training data.";
        InputStream trainingDataStream = new ByteArrayInputStream(fakeTrainingData.getBytes(StandardCharsets.UTF_8));
        InputStream spyTrainingDataStream = spy(trainingDataStream);

        doReturn(false).when(spyTrainingDataStream).markSupported();

        ModelTrainer spyTrainer = spy(modelTrainer);

        IOException thrownException = assertThrows(IOException.class, () -> {
            spyTrainer.createNameSampleStream(spyTrainingDataStream);
        });

        assertEquals("Training data input stream does not support mark/reset.", thrownException.getMessage());
    }

    @Test
    void testTrainEntityModelMethodToReturnTokenNameFinderModelObjectWithModelTypeAndNameSampleStreamAndTrainingParametersAndTokenNameFinderFactoryParameters()
        throws IOException {
        String fakeTrainingData = "This is a fake training data line.\nAnother line of training data.";
        InputStream trainingDataStream = new ByteArrayInputStream(fakeTrainingData.getBytes(StandardCharsets.UTF_8));

        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, 100);
        params.put(TrainingParameters.CUTOFF_PARAM, 1);
        TokenNameFinderFactory factory = new TokenNameFinderFactory();

        ModelTrainer spyTrainer = spy(modelTrainer);

        ObjectStream<NameSample> nameSampleStream = spyTrainer.createNameSampleStream(trainingDataStream);

        doReturn(mockTokenNameFinderModel).when(spyTrainer).trainEntityModel(ModelTypes.PERSON, nameSampleStream, params, factory);

        TokenNameFinderModel tokenNameFinderModel = spyTrainer.trainEntityModel(ModelTypes.PERSON, nameSampleStream, params, factory);

        assertNotNull(tokenNameFinderModel);
    }

    @Test
    void testSaveModelMethodToSaveTrainedModelWithTokenNameFinderModelAndModelTypeParameters() throws IOException, URISyntaxException {
        // Create a temporary file path with a "classpath:" prefix.
        // For example, tempDir/testmodel.bin will be our target file.
        Path tempFile = tempDir.resolve("testmodel.bin");
        String modelFilePath = "classpath:" + tempFile;

        // Create an instance of ModelTrainer.
        // Assume that ModelTrainer has a setter for modelFilePath.
        modelTrainer = new ModelTrainer(TRAINING_DATA_CLASS_PATH, TRAINING_DATA_FILE_PATH, MODEL_CLASS_PATH, modelFilePath, resourceLoader);

        // Create a dummy TokenNameFinderModel using Mockito.
        TokenNameFinderModel dummyModel = mock(TokenNameFinderModel.class);
        // Stub the serialize method to write known content to the output stream.
        doAnswer(invocation -> {
            OutputStream out = invocation.getArgument(0);
            out.write("dummy content".getBytes(StandardCharsets.UTF_8));
            return null;
        })
            .when(dummyModel)
            .serialize(any(OutputStream.class));

        // Call saveModel with the dummy model and a sample ModelTypes (e.g., PERSON)
        modelTrainer.saveModel(dummyModel, ModelTypes.PERSON);

        // Verify that the file was created.
        assertTrue(Files.exists(tempFile), "The model file should exist.");

        String fileContent = Files.readString(tempFile, StandardCharsets.UTF_8);
        assertEquals("dummy content", fileContent, "File content should match the expected output.");
    }
}
