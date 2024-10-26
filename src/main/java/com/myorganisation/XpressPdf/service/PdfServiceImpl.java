package com.myorganisation.XpressPdf.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.myorganisation.XpressPdf.model.Invoice;
import com.myorganisation.XpressPdf.model.InvoiceItem;
import com.myorganisation.XpressPdf.utils.HashUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

@Service
public class PdfServiceImpl implements PdfService {

    private final String pdfStoragePath;

    public PdfServiceImpl(@Value("${pdf.storage.path}") String pdfStoragePath) {
        this.pdfStoragePath = pdfStoragePath;
        ensureStorageDirectoryExists();
    }

    private void ensureStorageDirectoryExists() {
        File directory = new File(pdfStoragePath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Could not create storage directory: " + pdfStoragePath);
        }
    }

    @Override
    public String generatePdf(Invoice invoice) throws Exception {
        String hash = HashUtil.generateMD5(invoice.toString());
        String pdfPath = pdfStoragePath + hash + ".pdf";

        File pdfFile = new File(pdfPath);
        if(pdfFile.exists()) {
            return pdfPath; // Reuse existing PDF
        }

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.open();

        // Seller and Buyer table
        PdfPTable sellerBuyerTable = new PdfPTable(4);
        sellerBuyerTable.setWidthPercentage(100);

        // Seller information
        PdfPCell sellerCell = new PdfPCell();
        sellerCell.setColspan(2);
        sellerCell.addElement(new Paragraph("Seller:"));
        sellerCell.addElement(new Paragraph(invoice.getSeller()));
        sellerCell.addElement(new Paragraph(invoice.getSellerAddress()));
        sellerCell.addElement(new Paragraph("GSTIN: " + invoice.getSellerGstin()));

        sellerCell.setPaddingLeft(20);
        sellerCell.setPaddingTop(20);
        sellerCell.setPaddingBottom(20);
        sellerBuyerTable.addCell(sellerCell);

        // Buyer information
        PdfPCell buyerCell = new PdfPCell();
        buyerCell.setColspan(2);
        buyerCell.addElement(new Paragraph("Buyer:"));
        buyerCell.addElement(new Paragraph(invoice.getBuyer()));
        buyerCell.addElement(new Paragraph(invoice.getBuyerAddress()));
        buyerCell.addElement(new Paragraph("GSTIN: " + invoice.getBuyerGstin()));

        buyerCell.setPaddingLeft(20);
        buyerCell.setPaddingTop(20);
        buyerCell.setPaddingBottom(20);
        sellerBuyerTable.addCell(buyerCell);

        document.add(sellerBuyerTable);

        // Item table
        PdfPTable itemTable = new PdfPTable(4);
        itemTable.setWidthPercentage(100);
        itemTable.setWidths(new float[]{2, 1, 1, 1});

        // Item table header
        PdfPCell itemHeader;

        itemHeader = createCenteredCell("Item");
        itemTable.addCell(itemHeader);

        itemHeader = createCenteredCell("Quantity");
        itemTable.addCell(itemHeader);

        itemHeader = createCenteredCell("Rate");
        itemTable.addCell(itemHeader);

        itemHeader = createCenteredCell("Amount");
        itemTable.addCell(itemHeader);


        // Table data (items)
        List<InvoiceItem> items = invoice.getItems();
        for (InvoiceItem item : items) {

            PdfPCell cellValue;

            cellValue = createCenteredCell(item.getName());
            itemTable.addCell(cellValue);

            cellValue = createCenteredCell(item.getQuantity());
            itemTable.addCell(cellValue);

            cellValue = createCenteredCell(String.valueOf(item.getRate()));
            itemTable.addCell(cellValue);

            cellValue = createCenteredCell(String.valueOf(item.getAmount()));
            itemTable.addCell(cellValue);
        }

        document.add(itemTable);

        // Blank table
        PdfPTable blankTable = new PdfPTable(4);
        blankTable.setWidthPercentage(100);

        PdfPCell blankCell = new PdfPCell();
        blankCell.setColspan(4);
        blankCell.addElement(new Paragraph(" "));
        blankCell.setPadding(20);
        blankTable.addCell(blankCell);

        document.add(blankTable);

        document.close();

        return pdfPath;
    }

    // Helper method to create a centered cell with padding
    private PdfPCell createCenteredCell(String content) {
        PdfPCell cell = new PdfPCell(new Paragraph(content));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);
        return cell;
    }

    @Override
    public Resource getPdf(String filename) throws FileNotFoundException {
        File file = new File(pdfStoragePath + filename);
        if(!file.exists()) {
            throw new FileNotFoundException("PDF file not found");
        }
        return new FileSystemResource(file);
    }
}
