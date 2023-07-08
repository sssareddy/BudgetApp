package com.BudgetApp.Draft.model;

import java.util.List;

import lombok.Data;

@Data
public class ItemSumResponse {
List<ItemResponse> itemResponseList;
Double sum;
}
