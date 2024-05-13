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
    public void generateFile(String data) {
        PrintWriter writer = null;
        try {
            String fileName = "bill.txt";
            writer = new PrintWriter(new FileWriter(fileName));
            writer.println(data);
            LOGGER.info("TXT file generated successfully: {}", fileName);
        } catch (IOException e) {
            LOGGER.error("Error at generating TXT file: {}", e.getMessage());
        } finally {

            if (writer != null) {
                writer.close();
            }
        }
    }

}
