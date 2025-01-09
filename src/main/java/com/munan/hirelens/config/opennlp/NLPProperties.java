package com.munan.hirelens.config.opennlp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nlp")
public class NLPProperties {

    private final TrainingData trainingData = new TrainingData();
    private final TrainingModel trainingModel = new TrainingModel();

    @Getter
    @Setter
    public static class TrainingData {

        private Path path = new Path();

        @Getter
        @Setter
        public static class Path {

            private String first;
            private String person;
            private String education;
            private String location;
            private String skills;
            private String company;
        }
    }

    @Getter
    @Setter
    public static class TrainingModel {

        private Path path = new Path();

        @Getter
        @Setter
        public static class Path {

            private String first;
            private String person;
            private String education;
            private String location;
            private String skills;
            private String company;
        }
    }

    public TrainingData getTrainingData() {
        return trainingData;
    }

    public TrainingModel getTrainingModel() {
        return trainingModel;
    }
}
