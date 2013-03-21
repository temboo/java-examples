

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.temboo.Library.Dropbox.UploadFile;
import com.temboo.Library.Dropbox.UploadFile.UploadFileInputSet;
import com.temboo.Library.Flickr.PhotoSets.GetList;
import com.temboo.Library.Flickr.PhotoSets.GetList.GetListInputSet;
import com.temboo.Library.Flickr.PhotoSets.GetList.GetListResultSet;
import com.temboo.Library.Flickr.PhotoSets.GetPhotos;
import com.temboo.Library.Flickr.PhotoSets.GetPhotos.GetPhotosInputSet;
import com.temboo.Library.Flickr.PhotoSets.GetPhotos.GetPhotosResultSet;
import com.temboo.Library.Flickr.Photos.GetBase64EncodedPhoto;
import com.temboo.Library.Flickr.Photos.GetBase64EncodedPhoto.GetBase64EncodedPhotoInputSet;
import com.temboo.Library.Flickr.Photos.GetBase64EncodedPhoto.GetBase64EncodedPhotoResultSet;
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



 This is a simple Java application that demonstrates how to use the Temboo SDK to backup a photos in your Flickr photosets to folders in Dropbox.
 To run the demo, you'll need a Temboo account, and of course Dropbox and Flickr accounts. 

 The demo uses Temboo SDK functions to retrieve a list of Flickr photosets, retrieve a list of photos in each set, 
 download each photo, and upload each image to corresponding folders in Dropbox.

 @author aaronjennings
 */

public class BackupFlickrToDropbox {

	/**********************************************************************************************
	 * UPDATE THE VALUES OF THESE CONSTANTS WITH YOUR OWN CREDENTIALS
	 *********************************************************************************************/

	// Use these constants to define the set of oAuth credentials that will be used to access Flickr.
	// For info about obtaining oAuth credentials, see http://www.flickr.com/services/api/auth.oauth.html
	// (Replace with your own Flickr oAuth credentials.)
	private static final String FLICKR_API_KEY = "YOUR FLICKR API KEY";
	private static final String FLICKR_API_SECRET = "YOUR FLICKR API SECRET";
	private static final String FLICKR_ACCESS_TOKEN = "YOUR FLICKR OAUTH TOKEN";
	private static final String FLICKR_ACCESS_TOKEN_SECRET = "YOUR FLICKER OAUTH TOKEN SECRET";

	// Use these constants to define the set of oAuth credentials that will be used to access Dropbox. 
	// (Replace with your own Dropbox oAuth credentials.)
	private static final String DROPBOX_APP_KEY = "YOUR DROPBOX APP KEY";
	private static final String DROPBOX_APP_SECRET = "YOUR DROPBOX APP SECRET";
	private static final String DROPBOX_ACCESS_TOKEN = "YOUR DROPBOX OAUTH TOKEN";
	private static final String DROPBOX_ACCESS_TOKEN_SECRET = "YOUR DROPBOX OAUTH TOKEN SECRET";

	// Use these constants to define the set of credentials that will be used to connect with Temboo.
	// (Replace with your own Temboo Application Key.)
	private static final String TEMBOO_ACCOUNT_NAME = "YOUR TEMBOO ACCOUNT NAME";
	private static final String TEMBOO_APPLICATIONKEY_NAME = "YOUR TEMBOO APP KEY NAME";
	private static final String TEMBOO_APPLICATIONKEY = "YOUR TEMBOO APP KEY";

	/**********************************************************************************************
	 * END CONSTANTS: NOTHING BELOW THIS POINT SHOULD NEED TO BE CHANGED
	 *********************************************************************************************/


	/**
	 * Main method: use the Temboo SDK to get a list of photosets from Flickr, retrieve a list of photos within those sets,
	 * download each photo, and copy them to new Dropbox folders.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Create a new Temboo session, that will be used to run Temboo SDK choreos.
		// (Replace with your own Temboo Account Name, Application Key Name, and Application Key key).
		TembooSession session = new TembooSession(TEMBOO_ACCOUNT_NAME, TEMBOO_APPLICATIONKEY_NAME, TEMBOO_APPLICATIONKEY);

		// Instantiate the Flickr.PhotoSets.GetList Choreo, using the session object.
		// See https://live.temboo.com/library/Library/Flickr/PhotoSets/GetList for detailed documentation.
		GetList getList = new com.temboo.Library.Flickr.PhotoSets.GetList(session);

		// Get an InputSet object for the GetList, and populate the inputs. This choreo takes inputs that specify
		// your Flickr oAuth credentials.
		GetListInputSet getListInput = getList.newInputSet();

		getListInput.set_APIKey(FLICKR_API_KEY);
		getListInput.set_APISecret(FLICKR_API_SECRET);
		getListInput.set_AccessToken(FLICKR_ACCESS_TOKEN);
		getListInput.set_AccessTokenSecret(FLICKR_ACCESS_TOKEN_SECRET);

		// Run the GetList choreo to get a list of photosets in Flickr.
		GetListResultSet getListResults = getList.execute(getListInput);

		// Print some debugging to see if this is working.
		System.out.println("Retrieved list of photo sets from Flickr!");
		
		// Convert data for photosets to XML. See convenience function at the bottom of this code.
		Document photoSetDoc = convertToXML(getListResults.get_Response());

		// Extract the set of elements from the result XML
		NodeList photoSet = photoSetDoc.getElementsByTagName("photoset");

		// Loop over the set of <photoset> elements
		for(int num1=0; num1 < photoSet.getLength(); num1++) {			

			Element photoSetEntry = (Element) photoSet.item(num1);

			// Extract the title element and attribute for photoSetId for this photoset
			NodeList titles = photoSetEntry.getElementsByTagName("title");
			Element title = (Element) titles.item(0);
			String photoSetName = title.getTextContent();
			String photoSetID = photoSetEntry.getAttribute("id");

			// Print some debugging info to see if this is still working.
			System.out.println("Processing photo set: " + photoSetName);

			// Instantiate the Flickr.PhotoSet.GetPhotos Choreo, using the session object.
			// See https://live.temboo.com/library/Library/Flickr/PhotoSets/GetPhotos for detailed documentation
			GetPhotos getPhotos = new com.temboo.Library.Flickr.PhotoSets.GetPhotos(session);

			// Get an InputSet object for GetPhotos, and populate the inputs. This choreo takes inputs specifying
			// the photoset id that we just parsed and your Flickr oAuth credentials.
			GetPhotosInputSet getPhotosInput = getPhotos.newInputSet();

			getPhotosInput.set_PhotoSetID(Integer.parseInt(photoSetID));
			getPhotosInput.set_APIKey(FLICKR_API_KEY);
			getPhotosInput.set_APISecret(FLICKR_API_SECRET);
			getPhotosInput.set_AccessToken(FLICKR_ACCESS_TOKEN);
			getPhotosInput.set_AccessTokenSecret(FLICKR_ACCESS_TOKEN_SECRET);

			// Execute GetPhotos to get a list of photos within the specified photoset.
			GetPhotosResultSet getPhotosResults = getPhotos.execute(getPhotosInput);

			// Print some debugging statements.
			System.out.println("Retrieved the list of photos in " + photoSetName + "!");

			// Convert the data for photos into XML
			Document photoEntryDoc = convertToXML(getPhotosResults.get_Response());

			// Extract the set of elements from the result XML
			NodeList photos = photoEntryDoc.getElementsByTagName("photo");

			// Loop over the set of <photo> elements
			for(int num2=0; num2 < photos.getLength(); num2++) {			

				Element photoEntry = (Element) photos.item(num2);

				// Get attribute values that we need to retrieve the image
				String photoID = photoEntry.getAttribute("id");
				String secret = photoEntry.getAttribute("secret");
				String server = photoEntry.getAttribute("server");
				String farm = photoEntry.getAttribute("farm");
				String photoTitle = photoEntry.getAttribute("title");

				// Instantiate the Flicker.Photos.GetBase64EncodedPhoto Choreo, using the session object
				// See https://live.temboo.com/library/Library/Flickr/Photos/GetBase64EncodedPhoto for detailed documentation
				GetBase64EncodedPhoto getImage = new com.temboo.Library.Flickr.Photos.GetBase64EncodedPhoto(session);

				// Get an InputSet object for GetBase64EncodedPhoto, and populate the inputs. This choreo takes inputs that specify
				//the farm, photoID, secret, and server associated with each photo.
				GetBase64EncodedPhotoInputSet getImageInput = getImage.newInputSet();

				getImageInput.set_FarmID(Integer.parseInt(farm));
				getImageInput.set_PhotoID(Integer.parseInt(photoID));
				getImageInput.set_Secret(secret);
				getImageInput.set_ServerID(Integer.parseInt(server));

				// Execute GetBase64EncodedPhoto which will retrieve the image contents as base64 encoded data.
				// Note that the Dropbox Upload choreo expects base64 encoded data.
				GetBase64EncodedPhotoResultSet getImageResults = getImage.execute(getImageInput);

				// Print some debugging information so we know this is working.
				System.out.println("Downloaded " + photoTitle + " from the photo set: " + photoSetName);

				// Instantiate the Dropbox.UploadFile Choreo, using the session object
				// See https://live.temboo.com/library/Library/Dropbox/UploadFile for detailed documentation
				UploadFile upload = new com.temboo.Library.Dropbox.UploadFile(session);

				// Get an InputSet object for UploadFile, and populate the inputs. This choreo takes inputs that specify
				// the file contents, file name, folder, and oAuth credentials.
				UploadFileInputSet uploadInput = upload.newInputSet();

				// Set inputs for UploadFile. Note that the photoSetName is being passed in as the Dropbox folder name. This will create a new folder.
				// For info about obtaining oauth credentials, see https://www.dropbox.com/developers/reference/api
				uploadInput.set_FileContents(getImageResults.get_Response());
				uploadInput.set_FileName(photoTitle);
				uploadInput.set_Folder(photoSetName);

				uploadInput.set_AppKey(DROPBOX_APP_KEY);
				uploadInput.set_AppSecret(DROPBOX_APP_SECRET);
				uploadInput.set_AccessToken(DROPBOX_ACCESS_TOKEN);
				uploadInput.set_AccessTokenSecret(DROPBOX_ACCESS_TOKEN_SECRET);

				// Execute UploadFile. Note that we don't care about the response from this choreo.
				upload.execute(uploadInput);

				// Print response from UploadFile response
				System.out.println("Uploaded " + photoTitle + " to the Dropbox folder: " + photoSetName + "!");

			}
		}
	}

	/**
	 * This convenience function used when parsing XML from Flickr.
	 * In this case, the choreo response is an XML string; 
	 * we convert it into a w3c.Document object, to make it easier to parse.
	 * @param xml - An XML result returned by the Flickr choreos
	 */
	private static Document convertToXML(String xml) throws Exception {
		// In this case, the choreo response is an XML string; we convert it into a w3c.Document object, 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);

		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(
				new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

		return doc;
	}
}