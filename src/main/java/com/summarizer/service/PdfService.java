package com.summarizer.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class PdfService {
    public InputStreamResource createPdf(String largeText, String headerText) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true);

        float margin = 50;
        float pageWidth = page.getMediaBox().getWidth() - 2 * margin;
        float pageHeight = page.getMediaBox().getHeight() - 50;

        // Load Arial Unicode MS (ensure the font file is available)
        File fontFile = new File("src/main/resources/ARIALUNI.TTF"); // Replace with the actual path
        PDFont unicodeFont = PDType0Font.load(document, fontFile);


        // **Add Header**
        contentStream.setFont(unicodeFont, 18);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, pageHeight);
        contentStream.showText(headerText);
        contentStream.endText();

        // **Separator Line Below Header**
        contentStream.setStrokingColor(0, 0, 0);
        contentStream.setLineWidth(2);
        contentStream.moveTo(margin, pageHeight - 10);
        contentStream.lineTo(margin + pageWidth, pageHeight - 10);
        contentStream.stroke();

        // **Positioning for Body Content**
        contentStream.beginText();
        contentStream.setFont(unicodeFont, 12);
        contentStream.setLeading(16);
        float textPositionY = pageHeight - 40;
        contentStream.newLineAtOffset(margin, textPositionY);

        String[] lines = largeText.split("\n");

        for (String line : lines) {
            boolean isQuestion = line.trim().endsWith("?") || line.trim().startsWith("Q.");
            boolean isAnswer = line.trim().startsWith("A.");
            boolean questionBegin = line.trim().startsWith("Q.");
            if(questionBegin){
                contentStream.newLine();
            }


            if (isAnswer && textPositionY < pageHeight - 40) { // Add a new line before the answer, but not at the very beginning
                contentStream.newLine();
                textPositionY -= 16;
                if (textPositionY < 50) {
                    contentStream.endText();
                    contentStream.close();

                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true);
                    textPositionY = pageHeight - 50;

                    contentStream.beginText();
                    contentStream.setFont(unicodeFont, 12);
                    contentStream.setLeading(16);
                    contentStream.newLineAtOffset(margin, textPositionY);
                }
            }

            for (String wrappedLine : wrapText(line.trim(), pageWidth, unicodeFont, isQuestion ? 15 : 12)) {
                if (textPositionY < 50) {
                    contentStream.endText();
                    contentStream.close();

                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true);
                    textPositionY = pageHeight - 50;

                    contentStream.beginText();
                    contentStream.setFont(unicodeFont, 12);
                    contentStream.setLeading(16);
                    contentStream.newLineAtOffset(margin, textPositionY);
                }

                contentStream.setFont(unicodeFont, isQuestion ? 15 : 12);
                contentStream.showText(wrappedLine);
                contentStream.newLine();
                textPositionY -= 16;
            }
        }

        contentStream.endText();
        contentStream.close();
        document.save(outputStream);
        document.close();

        System.out.println("Formatted PDF with bold questions and answer spacing generated successfully.");
        return new InputStreamResource(new BufferedInputStream(new ByteArrayInputStream(outputStream.toByteArray())));
    }

    private static java.util.List<String> wrapText(String text, float maxWidth, PDFont font, int fontSize) throws IOException {
        java.util.List<String> wrappedLines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        String[] words = text.split(" ");

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            float width = font.getStringWidth(testLine) / 1000 * fontSize;

            if (width > maxWidth) {
                wrappedLines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine.append(currentLine.isEmpty() ? word : " " + word);
            }
        }

        if (!currentLine.toString().isEmpty()) {
            wrappedLines.add(currentLine.toString());
        }

        return wrappedLines;
    }
}
