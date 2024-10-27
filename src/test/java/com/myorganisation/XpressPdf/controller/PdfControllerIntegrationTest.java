package com.myorganisation.XpressPdf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorganisation.XpressPdf.model.Invoice;
import com.myorganisation.XpressPdf.model.InvoiceItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PdfControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${pdf.storage.path}")
    private String pdfStoragePath;

    private Invoice invoice;
    private String pdfFilename;

    @BeforeEach
    public void setUp() {
        invoice = new Invoice();
        invoice.setSeller("XYZ Pvt. Ltd.");
        invoice.setSellerGstin("29AABBCCDD121ZD");
        invoice.setSellerAddress("New Delhi, India");
        invoice.setBuyer("Vedant Computers");
        invoice.setBuyerGstin("29AABBCCDD131ZD");
        invoice.setBuyerAddress("New Delhi, India");
        invoice.setItems(List.of(
                new InvoiceItem("Product 1", "12 Nos", 123.00, 1476.00)
        ));
    }

    @Test
    public void testGeneratePdf() throws Exception {
        String response = mockMvc.perform(post("/api/pdf/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoice)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("PDF generated at: " + pdfStoragePath)))
                .andReturn().getResponse().getContentAsString();

        // Extract the filename from the response
        pdfFilename = response.replace("PDF generated at: " + pdfStoragePath, "").trim();
    }

    @Test
    public void testDownloadPdf() throws Exception {
        // First generate the PDF to ensure the file exists
        testGeneratePdf();

        mockMvc.perform(get("/api/pdf/download")
                        .param("filename", pdfFilename))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfFilename + "\""))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/pdf"));

        // Verify that the file actually exists on disk
        Path path = Paths.get(pdfStoragePath, pdfFilename);
        assert(Files.exists(path));
    }
}
