package com.BudgetApp.Draft.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.BudgetApp.Draft.Constants.ObjectConstants;


public class ReadTokenFromFile {
	private String access_token;

	public String getAccess_token() {
		Properties properties = new Properties();

		try (FileInputStream fileInputStream = new FileInputStream(ObjectConstants.CREDENTIALS_FILE_PATH)) {
			properties.load(fileInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		access_token = properties.getProperty(ObjectConstants.access_token_string);
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
		Properties properties = new Properties();
		try (FileOutputStream fileOutputStream = new FileOutputStream(ObjectConstants.CREDENTIALS_FILE_PATH)) {
			properties.setProperty(ObjectConstants.access_token_string, access_token);
			properties.store(fileOutputStream, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
