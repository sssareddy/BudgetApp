package com.BudgetApp.Draft.Util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.BudgetApp.Draft.Constants.ObjectConstants;
import com.BudgetApp.Draft.Service.CredentialService;
import com.BudgetApp.Draft.model.ItemResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Component
public class BudgetUtil {
	
	@Autowired
	private CredentialService credService;
	
    
	@Bean
	public Sheets getSheetService() throws GeneralSecurityException, IOException {
		
		GoogleCredentials credentials=credService.getGoogleCredentials();
		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
		        credentials);
		Sheets service = new Sheets.Builder(new NetHttpTransport(),
		        GsonFactory.getDefaultInstance(),
		        requestInitializer)
		        .setApplicationName(ObjectConstants.APPLICATION_NAME)
		        .build();
		return service;
	}

	public NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException {
		final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		return httpTransport;
	}
	
	@Bean
	public ValueRange getValueRange()
	{
		ValueRange valueRange=new ValueRange();
		return valueRange;
	}
	public static boolean isValidDateRange(LocalDate fromDate,LocalDate toDate,LocalDate sheetDate) {
		return sheetDate.isEqual(fromDate)||sheetDate.isEqual(toDate) || (sheetDate.isAfter(fromDate) && sheetDate.isBefore(toDate));
			
	}
	public static ItemResponse mapToObject(List<Object> row) {	
		LocalDate pDate = null;
		int serialNo=Integer.parseInt(row.get(0).toString());
		String itemName=row.get(1).toString();
	    String category=row.get(2).toString();
		Double price=Double.parseDouble(row.get(3).toString());
		String purchaseDate=row.get(4).toString();;
		String purchaseMode=row.get(5).toString();
		String paymentMode=row.get(6).toString();
		String datePattern = "dd/MMM/yyyy";
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
		try {
            pDate = LocalDate.parse(purchaseDate, dateFormatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
		return new ItemResponse(serialNo, itemName, category, price, pDate, purchaseMode, paymentMode);
	}
}
