package com.munan.gateway.service.languageModel;

import java.io.IOException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ModelTrainingRunner implements CommandLineRunner {

    private final ModelTrainerService modelTrainerService;

    /**
     * Constructor injection for the ModelTrainerService.
     *
     * @param modelTrainerService The service responsible for managing model training.
     */
    public ModelTrainingRunner(ModelTrainerService modelTrainerService) {
        this.modelTrainerService = modelTrainerService;
    }

    /**
     * This method is called after the Spring Boot application has started.
     * It triggers the model training process for all supported entity types.
     *
     * @param args Command line arguments (unused in this case).
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            // Start training all models
            modelTrainerService.trainAllModels();
            // Log message after successful training
            System.out.println("All models have been trained successfully.");
        } catch (IOException e) {
            // Handle any exceptions during model training
            System.err.println("Error during model training: " + e.getMessage());
        }
    }
}
