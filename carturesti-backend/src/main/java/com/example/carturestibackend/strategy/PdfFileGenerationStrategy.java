package com.example.carturestibackend.strategy;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class PdfFileGenerationStrategy implements FileGenerationStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfFileGenerationStrategy.class);

    @Override
    public String generateFile(String data) {
        String fileName = "bill.pdf"; // Numele fișierului PDF
        String filePath = Paths.get("").toAbsolutePath().toString() + File.separator + fileName; // Calea absolută către fișierul PDF
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12); // Folosește fontul Courier

            String[] lines = data.split("<br/>");
            float y = 700;

            for (String line : lines) {
                contentStream.beginText();
                contentStream.newLineAtOffset(100, y);
                contentStream.showText(line);
                contentStream.endText();
                y -= 12;
            }

            contentStream.close();

            document.save(filePath);
            LOGGER.info("PDF file generated successfully at: {}", filePath); // Afișează calea către fișierul PDF generat
        } catch (IOException e) {
            LOGGER.error("Error at generating PDF file: {}", e.getMessage());
            // În caz de eroare, returnează null sau o altă valoare semnificativă, în funcție de necesități
            filePath = null;
        }
        return filePath; // Returnează calea către fișierul PDF generat
    }
}
