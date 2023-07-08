package com.BudgetApp.Draft.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BudgetApp.Draft.Constants.ObjectConstants;
import com.BudgetApp.Draft.Util.BudgetUtil;
import com.BudgetApp.Draft.model.ItemRequest;
import com.BudgetApp.Draft.model.ItemResponse;
import com.BudgetApp.Draft.model.ItemSumResponse;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

@Service
public class ItemService {
	@Autowired
	private Sheets sheetService;
	
	@Autowired
	private ValueRange valueRange;
	
	

	public String insertRows(final ItemRequest itemRequest) throws IOException, GeneralSecurityException {
		List<List<Object>> values = new ArrayList<>();
		List<Object> rows = new ArrayList<>();
		rows.add(getrowId());
		rows.add(itemRequest.getItemName());
		rows.add(itemRequest.getCategory());
		rows.add(itemRequest.getPrice());
		rows.add(itemRequest.getPurchaseDate());
		rows.add(itemRequest.getPurchaseMode());
		rows.add(itemRequest.getPaymentMode());
		values.add(rows);
		try {
			// Append values to the specified range.
			valueRange = new ValueRange().setValues(values);	
			sheetService.spreadsheets().values()
					    .append(ObjectConstants.spreadsheetId, ObjectConstants.range, valueRange)
					    .setInsertDataOption(ObjectConstants.DataInsertOption)
					    .setValueInputOption(ObjectConstants.valueInputOption).execute();
		} catch (GoogleJsonResponseException e) {
			GoogleJsonError error = e.getDetails();
			if (error.getCode() == 404) {
				System.out.printf("Spreadsheet not found with id '%s'.\n", ObjectConstants.spreadsheetId);
			} else {
				throw e;
			}
		}

		return "Item "+itemRequest.getItemName()+" "+"Added Successfully";
	}
	public ItemSumResponse getItemsByFilter(String perticular,String from,String to,String filterBy) throws IOException {
        List<ItemResponse> itemList=new ArrayList<>();
        ItemSumResponse itemSumResponse=new ItemSumResponse();
        double totalPrice=0.0;
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(ObjectConstants.dataPattren);
		try {
            LocalDate  fromDate = LocalDate.parse(from, dateFormatter);
            LocalDate toDate = LocalDate.parse(to, dateFormatter);
    		valueRange= sheetService.spreadsheets().values().get(ObjectConstants.spreadsheetId, ObjectConstants.readDataRange).execute();
    		switch (filterBy) {
			case "Date": 
				itemList=valueRange.getValues().stream()
						.map(BudgetUtil::mapToObject).filter(o->BudgetUtil
								.isValidDateRange(fromDate, toDate, o.getPurchaseDate()))
						.collect(Collectors.toList());
				break;
			case "Category": 
				itemList=valueRange.getValues().stream()
	    				.map(BudgetUtil::mapToObject)
	    				.filter(o->(o.getCategory().equals(perticular))&&BudgetUtil.isValidDateRange(fromDate, toDate, o.getPurchaseDate()))
	    				.collect(Collectors.toList());
				break;
			case "Name": 
				itemList=valueRange.getValues().stream()
	    				.map(BudgetUtil::mapToObject)
	    				.filter(o->(o.getItemName().equals(perticular))&&BudgetUtil.isValidDateRange(fromDate, toDate, o.getPurchaseDate()))
	    				.collect(Collectors.toList());
				break;	
			case "purchaseMode":
				itemList=valueRange.getValues().stream()
	    				.map(BudgetUtil::mapToObject)
	    				.filter(o->(o.getPurchaseMode().equals(perticular))&&BudgetUtil.isValidDateRange(fromDate, toDate, o.getPurchaseDate()))
	    				.collect(Collectors.toList());
				break;
			case "paymentMode":
				itemList=valueRange.getValues().stream()
	    				.map(BudgetUtil::mapToObject)
	    				.filter(o->(o.getPaymentMode().equals(perticular))&&BudgetUtil.isValidDateRange(fromDate, toDate, o.getPurchaseDate()))
	    				.collect(Collectors.toList());
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + filterBy);
			}
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
		if(itemList.size()!=0) {
			totalPrice = itemList.stream()
					.mapToDouble(o -> o.getPrice())
					.sum();
		}
		itemSumResponse.setItemResponseList(itemList);
		itemSumResponse.setSum(totalPrice);
		return itemSumResponse;
		
	}
	
	public int getrowId() throws IOException
	{
		valueRange= sheetService.spreadsheets().values().get(ObjectConstants.spreadsheetId, ObjectConstants.readRange).execute();
		return valueRange.getValues().size();
	}
	public String addPerticular(String type,String name) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		List<Object> rows = new ArrayList<>();
		name=URLDecoder.decode(name, "UTF-8".toString());
		Map<String, List<Object>> perticularMap=getPerticularMap();
		List<Object> perticulars=perticularMap.get(type);
		if(perticulars.contains(name)) {
			return type+""+name+""+"already exists";
		}
		rows.add(name);
		values.add(rows);
		valueRange = new ValueRange().setValues(values);	
		String insertRange=type+ "!"+ObjectConstants.readRangePerticular+":"+ObjectConstants.readRangePerticular;
		sheetService.spreadsheets().values()
	    .append(ObjectConstants.spreadsheetId_Perticuler, insertRange, valueRange)
	    .setInsertDataOption(ObjectConstants.DataInsertOption)
	    .setValueInputOption(ObjectConstants.valueInputOption).execute();
		return type+" "+name+" "+"added Successfully";
	}
	public  Map<String, List<Object>> getPerticularMap() throws IOException {
		 Spreadsheet spreadsheet=  sheetService.spreadsheets().get(ObjectConstants.spreadsheetId_Perticuler).execute();
		 List<Sheet> sheets = spreadsheet.getSheets();
		 Map<String, List<Object>> sheetColumnMap = sheets.stream()
	                .collect(Collectors.toMap(
	                        sheet -> sheet.getProperties().getTitle(),
	                        sheet->getColumnValues(sheetService, sheet.getProperties().getTitle())
	                ));
		return sheetColumnMap;
	}

	private static List<Object> getColumnValues(Sheets sheetsService, String sheetName) {
		try {
			// Define the range to read (single column)
			String range = sheetName + "!" + ObjectConstants.readRangePerticular + ":"
					+ ObjectConstants.readRangePerticular;

			// Fetch the values of the single column
			ValueRange response = sheetsService.spreadsheets().values()
					.get(ObjectConstants.spreadsheetId_Perticuler, range).execute();
			List<List<Object>> values = response.getValues();

			// Process the values
			return values.stream().flatMap(Collection::stream).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
}
