package com.example.carturestibackend.strategy;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class PdfFileGenerationStrategy implements FileGenerationStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfFileGenerationStrategy.class);

    @Override
    public void generateFile(String data) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12); // Use Courier font

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

            document.save("bill.pdf");
            LOGGER.info("PDF file generated successfully!");
        } catch (IOException e) {
            LOGGER.error("Error at generating PDF file: {}", e.getMessage());
        }
    }


}