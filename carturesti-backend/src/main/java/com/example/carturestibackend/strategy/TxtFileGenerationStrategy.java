package com.example.carturestibackend.strategy;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxtFileGenerationStrategy implements FileGenerationStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxtFileGenerationStrategy.class);

    @Override
    public String generateFile(String data) {
        try {
            String fileName = "bill.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(fileName));
            writer.println(data);
            writer.close();
            LOGGER.info("TXT file generated successfully: {}", fileName);
            return fileName; // Return the file path
        } catch (IOException e) {
            LOGGER.error("Error at generating TXT file: {}", e.getMessage());
            return null;
        }
    }
}
