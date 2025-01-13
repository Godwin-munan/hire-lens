package com.munan.hirelens.config.opennlp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "opennlp")
public class NLPProperties {

    private final TrainingData trainingData = new TrainingData();
    private final TrainingModel trainingModel = new TrainingModel();

    @Getter
    @Setter
    public static class TrainingData {

        private ClassPath classPath = new ClassPath();
        private FilePath filePath = new FilePath();

        @Getter
        @Setter
        public static class ClassPath {

            private String person;
            //            private String education;
            //            private String location;
            //            private String skills;
            //            private String company;
        }

        @Getter
        @Setter
        public static class FilePath {

            private String person;
            //            private String education;
            //            private String location;
            //            private String skills;
            //            private String company;
        }
    }

    @Getter
    @Setter
    public static class TrainingModel {

        private TrainingData.ClassPath classPath = new TrainingData.ClassPath();
        private TrainingData.FilePath filePath = new TrainingData.FilePath();

        @Getter
        @Setter
        public static class ClassPath {

            private String person;
            //            private String education;
            //            private String location;
            //            private String skills;
            //            private String company;
        }

        @Getter
        @Setter
        public static class FilePath {

            private String person;
            //            private String education;
            //            private String location;
            //            private String skills;
            //            private String company;
        }
    }

    public TrainingData getTrainingData() {
        return trainingData;
    }

    public TrainingModel getTrainingModel() {
        return trainingModel;
    }
}
