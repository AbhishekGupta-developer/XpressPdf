package com.myorganisation.XpressPdf.service;

import com.myorganisation.XpressPdf.model.Invoice;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;

public interface PdfService {
    public String generatePdf(Invoice invoice)  throws Exception;
    public Resource getPdf(String filename) throws FileNotFoundException;
}

