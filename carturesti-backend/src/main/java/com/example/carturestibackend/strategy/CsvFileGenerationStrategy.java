package com.example.carturestibackend.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileWriter;
import java.io.IOException;

public class CsvFileGenerationStrategy implements FileGenerationStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvFileGenerationStrategy.class);

    @Override
    public String generateFile(String data) {
        try (FileWriter writer = new FileWriter("bill.csv")) {
            writer.write(data);
            LOGGER.info("CSV file generated successfully!");
            return "bill.csv"; // Return the file path
        } catch (IOException e) {
            LOGGER.error("Error at generating CSV file: {}", e.getMessage());
            return null;
        }
    }
}

