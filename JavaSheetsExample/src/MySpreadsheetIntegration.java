import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthSigner;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class MySpreadsheetIntegration {



	/**
	 * Log in to Google, with the OAuth2 authentication
	 * 
	 * @param clientID client ID received from registering the application with Google
	 * @param clientSecret client Secret received from registering the application with Google
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public void loginOAuth2(String clientID, String clientSecret)
			throws IOException, ServiceException {

		String SCOPES = "https://docs.google.com/feeds https://spreadsheets.google.com/feeds";

		// STEP 1: Set up the OAuth objects

		// You first need to initialize a few OAuth-related objects.
		// GoogleOAuthParameters holds all the parameters related to OAuth.
		// OAuthSigner is responsible for signing the OAuth base string.
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();

		// Set your OAuth Consumer Key (which you can register at
		// https://www.google.com/accounts/ManageDomains).
		oauthParameters.setOAuthConsumerKey(clientID);

		// Initialize the OAuth Signer.  
		OAuthSigner signer = null;
		oauthParameters.setOAuthConsumerSecret(clientSecret);
		signer = new OAuthHmacSha1Signer();

		// Finally create a new GoogleOAuthHelperObject.  This is the object you
		// will use for all OAuth-related interaction.
		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(signer);


		// STEP 2: Get the Authorization URL

		// Set the scope for this particular service.
		oauthParameters.setScope(SCOPES);

		// This method also makes a request to get the unauthorized request token,
		// and adds it to the oauthParameters object, along with the token secret
		// (if it is present).
		try {
			oauthHelper.getUnauthorizedRequestToken(oauthParameters);
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get the authorization url.  The user of your application must visit
		// this url in order to authorize with Google.  If you are building a
		// browser-based application, you can redirect the user to the authorization
		// url.
		String requestUrl = oauthHelper.createUserAuthorizationUrl(oauthParameters);
		System.out.println(requestUrl);
		System.out.println("Please visit the URL above to authorize your OAuth "
				+ "request token.  Once that is complete, press any key to "
				+ "continue...");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// STEP 3: Get the Access Token

		// Once the user authorizes with Google, the request token can be exchanged
		// for a long-lived access token.  If you are building a browser-based
		// application, you should parse the incoming request token from the url and
		// set it in GoogleOAuthParameters before calling getAccessToken().
		String token = null;
		try {
			token = oauthHelper.getAccessToken(oauthParameters);
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OAuth Access Token: " + token);
		System.out.println();


		// STEP 4: Make an OAuth authorized request to Google

		// Initialize the variables needed to make the request
		URL feedUrl = null;
		try {
			feedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Sending request to " + feedUrl.toString());
		System.out.println();
		SpreadsheetService googleService =
				new SpreadsheetService("oauth-sample-app");

		// Set the OAuth credentials which were obtained from the step above.
		try {
			googleService.setOAuthCredentials(oauthParameters, signer);
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Make the request to Google


		// Make a request to the API and get all spreadsheets.
		SpreadsheetFeed feed = googleService.getFeed(feedUrl, SpreadsheetFeed.class);
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();

		System.out.println("Response Data:");
		System.out.println("=====================================================");
		if(spreadsheets != null) {

			// Iterate through all of the spreadsheets returned
			for (SpreadsheetEntry spreadsheet : spreadsheets) {
				//Print the name of each spreadshet
				System.out.println(spreadsheet.getTitle().getPlainText());
			}
		}

		System.out.println("=====================================================");
		System.out.println();



		////////////////////////////////////////////////////////////////////////////
		// STEP 5: Revoke the OAuth token
		////////////////////////////////////////////////////////////////////////////

		System.out.println("Revoking OAuth Token...");
		try {
			oauthHelper.revokeToken(oauthParameters);
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OAuth Token revoked...");
	}


	public static void main(String[] args)
			throws AuthenticationException, MalformedURLException, IOException, ServiceException {

		SpreadsheetService service =
				new SpreadsheetService("MySpreadsheetIntegration-v1");

		// TODO: Authorize the service object for a specific user (see other sections)


		// Define the URL to request.  This should never change.
		URL SPREADSHEET_FEED_URL = new URL(
				"https://spreadsheets.google.com/feeds/spreadsheets/private/full");

		// Make a request to the API and get all spreadsheets.
		SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();

		// Iterate through all of the spreadsheets returned
		for (SpreadsheetEntry spreadsheet : spreadsheets) {
			// Print the title of this spreadsheet to the screen
			System.out.println(spreadsheet.getTitle().getPlainText());
		}
		SpreadsheetEntry spreadsheet = spreadsheets.get(0);
		System.out.println(spreadsheet.getTitle().getPlainText());

		// Make a request to the API to fetch information about all
		// worksheets in the spreadsheet.
		List<WorksheetEntry> worksheets = spreadsheet.getWorksheets();

		// Iterate through each worksheet in the spreadsheet.
		for (WorksheetEntry worksheet : worksheets) {
			// Get the worksheet's title, row count, and column count.
			String title = worksheet.getTitle().getPlainText();
			int rowCount = worksheet.getRowCount();
			int colCount = worksheet.getColCount();

			// Print the fetched information to the screen for this worksheet.
			System.out.println("\t" + title + "- rows:" + rowCount + " cols: " + colCount);
		}
	}
}