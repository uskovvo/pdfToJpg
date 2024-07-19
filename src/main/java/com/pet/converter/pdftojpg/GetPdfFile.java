package com.pet.converter.pdftojpg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Slf4j
public class GetPdfFile {

    @PostMapping(path = "pdf_to_jpg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> convertPdfToJpg(@RequestPart(name = "file", required = true) MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = originalFileName != null ? originalFileName.substring(0, originalFileName.lastIndexOf('.')) : "output";

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 1200); // Увеличение DPI для лучшего качества
            File outputDir = new File("./images");

            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, fileName + ".jpg");
            ImageIO.write(image, "JPEG", outputFile);

            return ResponseEntity.ok().body(outputFile.getAbsolutePath());
        } catch (IOException e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при конвертации PDF в JPEG");
        }
    }

    @PostMapping(path = "jpg_to_pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> convertJpgToPdf(@RequestPart(name = "file", required = true) MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = originalFileName != null ? originalFileName.substring(0, originalFileName.lastIndexOf('.')) : "output";

        try (PDDocument document = new PDDocument()) {
            PDImageXObject pdImage = JPEGFactory.createFromStream(document, file.getInputStream());
            PDRectangle imageRect = new PDRectangle(pdImage.getWidth(), pdImage.getHeight());
            PDPage page = new PDPage(imageRect);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
            contentStream.close();

            File outputDir = new File("./newPdf");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            document.save(new File(outputDir, fileName + "_converted.pdf"));
            return ResponseEntity.ok().body(fileName + "_converted.pdf");
        }
    }

}
