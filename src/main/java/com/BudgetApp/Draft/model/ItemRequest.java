package com.BudgetApp.Draft.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {
private String itemName;
private String category;
private double price;
private String purchaseDate;
private String purchaseMode;
private String paymentMode;

}
