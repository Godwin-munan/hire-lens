package com.munan.gateway.config.opennlp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.munan.gateway.SkipSetup;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

//@ActiveProfiles("dev")
//@SpringJUnitConfig(NLPPropertiesTest.Config.class)
@SpringBootTest
public class NLPPropertiesTest {

    @Autowired
    private NLPProperties nlpProperties;

    //    private NLPProperties.TrainingModel.ClassPath modelClassPath;
    //    NLPProperties.TrainingModel.FilePath modelFilePath;

    @Before(value = "")
    void setup(ExtensionContext context) {
        //        if (context.getTestMethod().isPresent() && !context.getTestMethod().get().isAnnotationPresent(SkipSetup.class)) {
        //
        //            modelClassPath = new NLPProperties.TrainingModel.ClassPath();
        //            modelFilePath = new NLPProperties.TrainingModel.FilePath();
        //
        //        }

    }

    @AfterEach
    void tearDown() throws Exception {}

    @Test
    @SkipSetup
    void testTrainingDataPropertiesBinding() {
        assertNotNull(nlpProperties);
        assertNotNull(nlpProperties.getTrainingData());
        assertNotNull(nlpProperties.getTrainingData().getClassPath());
        assertNotNull(nlpProperties.getTrainingData().getClassPath().getPerson());

        // Verify TrainingData properties
        assertEquals("classpath:data/person.training.data.train", nlpProperties.getTrainingData().getClassPath().getPerson());
        assertEquals("src/main/resources/data/person.training.data.train", nlpProperties.getTrainingData().getFilePath().getPerson());
    }

    @Test
    void testTrainingModelPropertiesBinding() {
        assertNotNull(nlpProperties);
        assertNotNull(nlpProperties.getTrainingModel());
        assertNotNull(nlpProperties.getTrainingModel().getClassPath());
        assertNotNull(nlpProperties.getTrainingModel().getClassPath().getPerson());

        // Verify TrainingModel properties
        assertEquals("classpath:models/en-ner-person.bin", nlpProperties.getTrainingModel().getClassPath().getPerson());
        assertEquals("src/main/resources/models/en-ner-person.bin", nlpProperties.getTrainingModel().getFilePath().getPerson());
    }

    @Test
    void testTrainingDataGettersAndSetters() {
        NLPProperties.TrainingData.ClassPath classPath = new NLPProperties.TrainingData.ClassPath();
        classPath.setPerson("new-path");

        assertEquals("new-path", classPath.getPerson());

        NLPProperties.TrainingData.FilePath filePath = new NLPProperties.TrainingData.FilePath();
        filePath.setPerson("another-path");

        assertEquals("another-path", filePath.getPerson());
    }

    @Test
    void testTrainingModelGettersAndSetters() {
        NLPProperties.TrainingModel.ClassPath classPath = new NLPProperties.TrainingModel.ClassPath();
        classPath.setPerson("new-path");

        assertEquals("new-path", classPath.getPerson());

        NLPProperties.TrainingModel.FilePath filePath = new NLPProperties.TrainingModel.FilePath();
        filePath.setPerson("another-path");

        assertEquals("another-path", filePath.getPerson());
    }
    //    @TestConfiguration
    //    @EnableConfigurationProperties(NLPProperties.class)
    //    static class Config {}
}
