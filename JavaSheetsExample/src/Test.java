import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

public class Test {

	static final String CLIENT_ID = "701985732551-eguqngfeidtpurch0qsuabatkccobjqq.apps.googleusercontent.com";
	static final String CLIENT_SECRET = "VguvQOqispAdT577T4uyvt_n";
	static final String DRIVE_URL = "https://docs.google.com/spreadsheets/d/1c8MFCOKNaJ8xmKrpVvC2iYuE3kUsjRxdRWwEc4PqH5I/edit#gid=0";

	static final String DBOX_URL = "https://www.dropbox.com/s/w8rfdsw57380v1b/yemek_listesi_original.pdf";
	static final String DBOX_FNAME = "yemek_listesi_original.pdf";

	static final String APP_KEY = "co6uzh3qhvp9pp8";
	static final String APP_SECRET = "8ob9rmso7muonwf";

	public static void downloadFile() throws Exception {
		try {
			URL download = new URL(DBOX_URL);
			ReadableByteChannel rbc = Channels.newChannel(download.openStream());
			FileOutputStream fileOut = new FileOutputStream(DBOX_FNAME);
			fileOut.getChannel().transferFrom(rbc, 0, 1 << 24);
			fileOut.flush();
			fileOut.close();
			rbc.close();
		} catch (
		Exception e) {
			e.printStackTrace();
		}
	}
	
	// recursively download a folder from Dropbox to the local file system
	public static void downloadFolder(String path, String destination) throws IOException {
	    new File(destination).mkdirs();
	    try {
	        for (DbxEntry child : client.getMetadataWithChildren(path).children) {
	            if (child instanceof DbxEntry.Folder) {
	                // recurse
	                downloadFolder(path + "/" + child.name, destination + "/" + child.name);
	            } else if (child instanceof DbxEntry.File) {
	                // download an individual file
	                OutputStream outputStream = new FileOutputStream(
	                    destination + "/" + child.name);
	                try {
	                    DbxEntry.File downloadedFile = client.getFile(
	                        path + "/" + child.name, null, outputStream);
	                } finally {
	                    outputStream.close();
	                }
	            }
	        }
	    } catch (DbxException e) {
	        System.out.println(e.getMessage());
	    } catch (IOException e) {
	        System.out.println(e.getMessage());
	    }
	}

	public static void DropboxAppExample() throws Exception {

		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

		// Have the user sign in and authorize your app.
		String authorizeUrl = webAuth.start();
		System.out.println("1. Go to: " + authorizeUrl);
		System.out.println("2. Click \"Allow\" (you might have to log in first)");
		System.out.println("3. Copy the authorization code.");
		String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

		DbxAuthFinish authFinish = webAuth.finish(code);
		String accessToken = authFinish.accessToken;

		DbxClient client = new DbxClient(config, accessToken);
		System.out.println("Linked account: " + client.getAccountInfo().displayName);

		FileOutputStream outputStream = new FileOutputStream("yemek_listesi_original.pdf");
		try {
			DbxEntry.File downloadedFile = client.getFile("/yemek_listesi_original.pdf", null, outputStream);
			System.out.println("Metadata: " + downloadedFile.toString());
		} finally {
			outputStream.close();
		}
	}

	public static void main(String args[]) throws Exception {
		// downloadFile();
		downloadFolder();
	}
}