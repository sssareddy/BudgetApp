package com.BudgetApp.Draft.Util;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.BudgetApp.Draft.Constants.ObjectConstants;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

@Component
public class LoadDataBean {
	@Autowired
	private Sheets sheetService;
	@Autowired
	private ValueRange valueRange;

	public List<List<Object>> loadData(){
		try {
			valueRange= sheetService.spreadsheets().values().get(ObjectConstants.spreadsheetId, ObjectConstants.readRange).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return valueRange.getValues();
	}
}
