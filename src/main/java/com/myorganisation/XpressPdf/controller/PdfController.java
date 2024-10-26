package com.myorganisation.XpressPdf.controller;

import com.myorganisation.XpressPdf.model.Invoice;
import com.myorganisation.XpressPdf.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/generate")
    public ResponseEntity<String> generatePdf(@RequestBody Invoice invoice) throws Exception {
        String pdfPath = pdfService.generatePdf(invoice);
        return new ResponseEntity<>("PDF generated at: " + pdfPath, HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadPdf(@RequestParam String filename) {
        try {
            Resource file = pdfService.getPdf(filename);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            headers.set(HttpHeaders.CONTENT_TYPE, "application/pdf");

            return new ResponseEntity<>(file, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

}
