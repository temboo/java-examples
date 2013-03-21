

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.temboo.Library.Dropbox.CreateFolder;
import com.temboo.Library.Dropbox.UploadFile;
import com.temboo.Library.Dropbox.CreateFolder.CreateFolderInputSet;
import com.temboo.Library.Dropbox.UploadFile.UploadFileInputSet;
import com.temboo.Library.Google.Documents.DownloadBase64EncodedDocument;
import com.temboo.Library.Google.Documents.GetAllDocuments;
import com.temboo.Library.Google.Documents.DownloadBase64EncodedDocument.DownloadBase64EncodedDocumentInputSet;
import com.temboo.Library.Google.Documents.DownloadBase64EncodedDocument.DownloadBase64EncodedDocumentResultSet;
import com.temboo.Library.Google.Documents.GetAllDocuments.GetAllDocumentsInputSet;
import com.temboo.Library.Google.Documents.GetAllDocuments.GetAllDocumentsResultSet;
import com.temboo.Library.Google.Spreadsheets.DownloadBase64EncodedSpreadsheet;
import com.temboo.Library.Google.Spreadsheets.DownloadBase64EncodedSpreadsheet.DownloadBase64EncodedSpreadsheetInputSet;
import com.temboo.Library.Google.Spreadsheets.DownloadBase64EncodedSpreadsheet.DownloadBase64EncodedSpreadsheetResultSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;


/**
 Copyright 2012, Temboo Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   
   
   
  This is a simple Java application that demonstrates how to use the Temboo SDK to backup a set of Google Documents files to Dropbox.
  To run the demo, you'll need a Temboo account, and of course Dropbox and Google Docs accounts. 
  
  The demo uses Temboo SDK functions to create a new folder to hold your backups of Dropbox, then retrieves a list of
  Google Documents files for the specified account, downloads each file and then uploads it to the Dropbox folder.
  
  @author matthewflaming
 */
public class GoogleDocumentsBackup {

	/**********************************************************************************************
	 * UPDATE THE VALUES OF THESE CONSTANTS WITH YOUR OWN CREDENTIALS
	 *********************************************************************************************/

	// Use these constants to define the set of oAuth credentials that will be used to access Dropbox. 
	// (Replace with your own Dropbox oAuth credentials.)
	private static final String DROPBOX_APP_KEY = "YOUR DROPBOX APP KEY";
	private static final String DROPBOX_APP_SECRET = "YOUR DROPBOX APP SECRET";
	private static final String DROPBOX_ACCESS_TOKEN = "YOUR DROPBOX OAUTH TOKEN";
	private static final String DROPBOX_ACCESS_TOKEN_SECRET = "YOUR DROPBOX OAUTH TOKEN SECRET";
	
	// Use this constant to define the name of the folder that will be created on Dropbox, and that will hold
	// the set of uploaded documents. (Note that another folder with the same name can't already exist on Dropbox.)
	private static final String DROPBOX_BACKUP_FOLDERNAME = "Google_Doc_Backups";
	
	// Use these constants to define the set of credentials that will be used to access Google Documents.
	// (Replace with your own Google Docs credentials.)
	private static final String GOOGLEDOCS_USERNAME = "YOUR USERNAME";
	private static final String GOOGLEDOCS_PASSWORD = "YOUR PASSWORD";
	
	// Use these constants to define the set of credentials that will be used to connect with Temboo.
	// (Replace with your own Temboo Application Key.)
	private static final String TEMBOO_ACCOUNT_NAME = "YOUR TEMBOO ACCOUNT NAME";
	private static final String TEMBOO_APPLICATIONKEY_NAME = "YOUR APPKEY NAME";
	private static final String TEMBOO_APPLICATIONKEY = "YOUR APPKEY";
	
	/**********************************************************************************************
	 * END CONSTANTS: NOTHING BELOW THIS POINT SHOULD NEED TO BE CHANGED
	 *********************************************************************************************/

	
	
	/**
	 * Main method: use the Temboo SDK to create a new folder on Dropbox, and back up all the items in the
	 * specified Google Documents account to the new Dropbox folder.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
	    
		// Create a new Temboo session, that will be used to run Temboo SDK choreos.
		// (Replace with your own Temboo Account Name, Application Key Name, and Application Key key).
		TembooSession session = new TembooSession(TEMBOO_ACCOUNT_NAME, TEMBOO_APPLICATIONKEY_NAME, TEMBOO_APPLICATIONKEY);
		
		// Instantiate the Dropbox.CreateFolder choreo, using the Temboo session object
		// See https://live.temboo.com/library/Library/Dropbox/CreateFolder for detailed documentation
		CreateFolder createFolder = new com.temboo.Library.Dropbox.CreateFolder(session);
		
		// Get an InputSet object for the CreateFolder choreo, and populate the inputs. This choreo takes inputs
		// specifying the name of the folder to create, and Dropbox oAuth credentials
		CreateFolderInputSet createFolderInput = createFolder.newInputSet();
		
		createFolderInput.set_NewFolderName(DROPBOX_BACKUP_FOLDERNAME);	
		
		createFolderInput.set_AppKey(DROPBOX_APP_KEY);
		createFolderInput.set_AppSecret(DROPBOX_APP_SECRET);
		createFolderInput.set_AccessToken(DROPBOX_ACCESS_TOKEN);
		createFolderInput.set_AccessTokenSecret(DROPBOX_ACCESS_TOKEN_SECRET);
		
		// Run the "create folder" choreo, to create the new backups folder on Dropbox. (Note that in this case,
		// we don't worry about the results that the choreo returns.)
		createFolder.execute(createFolderInput);
		
		// Do a bit of debug logging, so we know what's going on
		System.out.println("Dropbox folder created successfully!");
		  
		    
		// Instantiate the Library.Google.Documents.GetAllDocuments choreo.
		// This choreo retrieves all documents (text, spreadsheet and pdf) in the specified Google Documents account
		// See https://live-eng.temboo.com/library/Library/Google/Documents/GetAllDocuments for detailed documentation
		GetAllDocuments getAllDocuments = new com.temboo.Library.Google.Documents.GetAllDocuments(session);
		 
		// Get an InputSet object for GetAllDocuments, and configure the inputs. This choreo takes inputs 
		// specifying the Google Docs credentials to use, and a flag specifying whether we want to get deleted documents in the list 
		GetAllDocumentsInputSet getAllDocumentsInput = getAllDocuments.newInputSet();
		getAllDocumentsInput.set_Username(GOOGLEDOCS_USERNAME);
		getAllDocumentsInput.set_Password(GOOGLEDOCS_PASSWORD);
		getAllDocumentsInput.set_Deleted(false);	
		   
		    // Get the list of all documents from Google Docs
		GetAllDocumentsResultSet getAllDocumentsResults = getAllDocuments.execute(getAllDocumentsInput);
		
		// In this case, the choreo response is an XML string; we convert it into a w3c.Document object, 
		// to make it easier to parse
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document googleDocsList = builder.parse(
			new InputSource(new ByteArrayInputStream(getAllDocumentsResults.get_Response().getBytes("utf-8"))));
		
		
		// Extract the set of "entry" and "content" from the result XML; each pair of entry/content elements
		// represents a single Google Document, that we'll want to upload
		NodeList entry = googleDocsList.getElementsByTagName("entry");
		NodeList links = googleDocsList.getElementsByTagName("content");
		   
		    // loop over the set of Google Documents (one per "entry" element), and upload each one to Dropbox
		for(int i=0; i < entry.getLength(); i++) {			

			Element entryResult = (Element) entry.item(i);
			
			// extract the title of the document, from the XML retrieved by the GetAllDocuments choreo
			NodeList titles = entryResult.getElementsByTagName("title");
			String title = ((Element) titles.item(0)).getTextContent();
			
			// get the corresponding <content> element that contains data for this document
			Element link = (Element) links.item(i);
			
			// extract the <src> element for each document retrieved by the GetAllDocuments choreo.
			// the <src> will contain a URL that can be used for access to the document content; this URL includes
			// information about the document type, eg:
			// https://docs.google.com/feeds/download/spreadsheets/Export?key=0AkO-My_cBuHFdEJ5bF9IVVA1NzNvU1VrZ2RZ34NWblE
			String linkSrc = link.getAttribute("src");
			      	   	        	  
			// do a bit of debug logging, so we know what's going on
			System.out.println("Now processing document: " + title);

			// Based on the type of the current document, we'll need to do something slightly different...
			if (linkSrc.contains("spreadsheet")) {
        		
				// If it's a spreadsheet, download the document with the DownloadBase64EncodedSpreadsheet choreo.
				// See https://live.temboo.com/library/Library/Google/Spreadsheets/DownloadBase64EncodedSpreadsheet for detailed documentation
				DownloadBase64EncodedSpreadsheet downloadSpreadsheet = new com.temboo.Library.Google.Spreadsheets.DownloadBase64EncodedSpreadsheet(session);
				
				// This choreo takes inputs that specify the Google Documents credentials, and the URL of the document to download
				DownloadBase64EncodedSpreadsheetInputSet downloadSpreadsheetInput = downloadSpreadsheet.newInputSet();
				
				downloadSpreadsheetInput.set_Link(linkSrc);	// here we specify the retrieved URL for the document to download
				downloadSpreadsheetInput.set_Username(GOOGLEDOCS_USERNAME);
				downloadSpreadsheetInput.set_Password(GOOGLEDOCS_PASSWORD);
				downloadSpreadsheetInput.set_Title("");
				
				// Run the choreo to download the spreadsheet document 
				DownloadBase64EncodedSpreadsheetResultSet getDownloadSpreadsheetResults = downloadSpreadsheet.execute(downloadSpreadsheetInput);
				
				// Upload the file to Dropbox (by calling the uploadFileToDropbox method, below)
				uploadFileToDropbox(session, getDownloadSpreadsheetResults.get_FileContents(), title);     	
        		
			} else if (linkSrc.contains("documents")) {
        		
				// If it's a document, download the file with the DownloadBase64EncodedDocument choreo 
				// See https://live.temboo.com/library/Library/Google/Documents/DownloadBase64EncodedDocument for detailed documentation
				DownloadBase64EncodedDocument downloadDocument = new com.temboo.Library.Google.Documents.DownloadBase64EncodedDocument(session);
				
				// This choreo takes inputs that specify the Google Documents credentials, and the URL of the document to download
				DownloadBase64EncodedDocumentInputSet downloadDocumentInput = downloadDocument.newInputSet();
				
				downloadDocumentInput.set_Format("doc");
				downloadDocumentInput.set_Link(linkSrc);
				downloadDocumentInput.set_Username(GOOGLEDOCS_USERNAME);
				downloadDocumentInput.set_Password(GOOGLEDOCS_PASSWORD);
				downloadDocumentInput.set_Title("");
				
				// Run the choreo to download the document
				DownloadBase64EncodedDocumentResultSet getDownloadDocumentResults = downloadDocument.execute(downloadDocumentInput);	
				
				// Upload the file to Dropbox (by calling the uploadFileToDropbox method, below)
				uploadFileToDropbox(session, getDownloadDocumentResults.get_FileContents(), title);

			} else if (linkSrc.contains("securesc")) {	// "securesc" means that this is a PDF document, in Google-speak
        		
				// Use the DownloadBase64EncodedDocument choreo to download the PDF
				DownloadBase64EncodedDocument downloadPDFDocument = new com.temboo.Library.Google.Documents.DownloadBase64EncodedDocument(session);
				
				// This choreo takes inputs that specify the Google Documents credentials, and the URL of the document to download
				DownloadBase64EncodedDocumentInputSet downloadDocumentInput = downloadPDFDocument.newInputSet();
				
				downloadDocumentInput.set_Format("pdf");
				downloadDocumentInput.set_Link(linkSrc);
				downloadDocumentInput.set_Username(GOOGLEDOCS_USERNAME);
				downloadDocumentInput.set_Password(GOOGLEDOCS_PASSWORD);
				downloadDocumentInput.set_Title("");
				
				// Run the choreo to download the PDF file
				DownloadBase64EncodedDocumentResultSet getDownloadDocumentResults = downloadPDFDocument.execute(downloadDocumentInput);
				
				// Upload the file to Dropbox (by calling the uploadFileToDropbox method, below)
				uploadFileToDropbox(session, getDownloadDocumentResults.get_FileContents(), title);
			}

        	    
		}	// end of loop over all documents

		// do a bit of debug logging, so we know what's going on
		System.out.println("Finished copying Google Documents to Dropbox!");
	}
	
	
	/**
	 * This convenience function is used to upload a file to Dropbox (using a Temboo SDK choreo).
	 * 
	 * @param session - A valid Temboo Session object
	 * @param fileContents - The Base64 encoded contents of the file to upload
	 * @param fileTitle - The title of the file to create on Dropbox
	 * @throws TembooException
	 */
	private static void uploadFileToDropbox(TembooSession session, String fileContents, String fileTitle) throws TembooException {
		
		// Create a Dropbox.UploadFile choreo, that will be used to send the data to Dropbox, using the session object (as always)
		// See https://live.temboo.com/library/Library/Dropbox/UploadFile for detailed documentation
		UploadFile upload = new com.temboo.Library.Dropbox.UploadFile(session);
		     	
		// Get an InputSet object for Dropbox.UploadFile, and configure it
		UploadFileInputSet uploadInput = upload.newInputSet();
		uploadInput.set_Folder(DROPBOX_BACKUP_FOLDERNAME);
		
		uploadInput.set_AppKey(DROPBOX_APP_KEY);
		uploadInput.set_AppSecret(DROPBOX_APP_SECRET);
		uploadInput.set_AccessToken(DROPBOX_ACCESS_TOKEN);
		uploadInput.set_AccessTokenSecret(DROPBOX_ACCESS_TOKEN_SECRET);
		
		uploadInput.set_FileContents(fileContents);	// set the file contents
		uploadInput.set_FileName(fileTitle);		// set the file title
    	
		try {
			// Run the choreo to upload the file (Note that in this case, we don't care about the results returned by the choreo)
			upload.execute(uploadInput);
    		
			System.out.println("Successfully uploaded file to Dropbox: " + fileTitle);
		} catch(TembooException e) {
			System.out.println("Uh-oh! Something went wrong uploading the file to Dropbox. The error from the choreo was: " + e.getMessage());
		}
	}
	
}
	

