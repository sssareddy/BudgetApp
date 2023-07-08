package com.BudgetApp.Draft.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.BudgetApp.Draft.Constants.ObjectConstants;
import com.BudgetApp.Draft.model.ReadTokenFromFile;
import com.BudgetApp.Draft.model.TokenResponse;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;

@Service
public class CredentialService {
	
	public GoogleCredentials getGoogleCredentials() {
		return GoogleCredentials.create(getAccessToken())
				.createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
	}

	public AccessToken getAccessToken() {
		TokenResponse tokenResponse = new TokenResponse();
		ReadTokenFromFile readToken=new ReadTokenFromFile();
		LocalDate date = LocalDate.now().plusDays(1);
		if (readToken.getAccess_token().isEmpty() || !isValidToken(readToken.getAccess_token())) {
			System.out.println("Inside False Condition");
			try {
				HttpRequest request = HttpRequest.newBuilder().uri(new URI(ObjectConstants.TOKEN_URI))
						.headers("Content-Type", "text/plain;charset=UTF-8").POST(HttpRequest.BodyPublishers.noBody())
						.build();
				HttpClient client = HttpClient.newHttpClient();

				try {
					HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
					Gson gson = new Gson(); // Or use new GsonBuilder().create();
					tokenResponse = gson.fromJson(response.body(), TokenResponse.class); // deserializes json into
					System.out.println(readToken.getAccess_token());
					readToken.setAccess_token(tokenResponse.getAccess_token());
					System.out.println(readToken.getAccess_token());// target2
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Inside True Condition");
			return new AccessToken(readToken.getAccess_token(),
					Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
		return new AccessToken(readToken.getAccess_token(),
				Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}

	public boolean isValidToken(String accessToken) {
		boolean isValid = false;
		System.out.println("Inside is Valid==="+accessToken);
		try {
			URL url = new URL(ObjectConstants.VALIDATE_TOKEN_URI + accessToken);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();

			if (responseCode == 200) {
				isValid = true;
			}
		} catch (IOException e) {
			System.out.println(ObjectConstants.VALIDATE_TOKEN_ERROR+ e.getMessage());
		}
		return isValid;
	}
}
