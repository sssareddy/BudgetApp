package com.BudgetApp.Draft.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemResponse {
	
	private Integer serialNo;
	private String itemName;
	private String category;
	private Double price;
	private LocalDate purchaseDate;
	private String purchaseMode;
	private String paymentMode;
}
