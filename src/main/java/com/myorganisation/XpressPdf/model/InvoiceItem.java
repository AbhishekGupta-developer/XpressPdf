package com.myorganisation.XpressPdf.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItem {

    private String name;
    private String quantity;
    private double rate;
    private double amount;

}
