

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;

import javax.imageio.ImageIO;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.temboo.Library.Amazon.Marketplace.Feeds.AddOrUpdateInventoryItem;
import com.temboo.Library.Amazon.Marketplace.Feeds.AddOrUpdateInventoryItem.AddOrUpdateInventoryItemInputSet;
import com.temboo.Library.Amazon.Marketplace.Feeds.AddOrUpdateInventoryItem.AddOrUpdateInventoryItemResultSet;
import com.temboo.Library.Amazon.Marketplace.Feeds.AddOrUpdateProductImage;
import com.temboo.Library.Amazon.Marketplace.Feeds.AddOrUpdateProductImage.AddOrUpdateProductImageInputSet;
import com.temboo.Library.Amazon.Marketplace.Feeds.AddOrUpdateProductImage.AddOrUpdateProductImageResultSet;
import com.temboo.Library.Amazon.S3.PutObject;
import com.temboo.Library.Amazon.S3.PutObject.PutObjectInputSet;
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
   
  
 This is a simple Java application that demonstrates how to use the Temboo SDK to submit an inventory listing to your Amazon Marketplace (MWS) account,
 and associate an image (stored in Amazon S3) with that listing. The demo uses Temboo SDK functions to submit an inventory listing to MWS, upload a product 
 image to S3, and update the inventory listing with the product image.
 
 To run the demo, you'll need a Temboo account, an Amazon Marketplace account, and an Amazon Web Services account for S3.
 
 Keep in mind that when making requests to Amazon Markeplace, it will take a little while to return the submission result. 
 This is because the choreos will submit the feed, poll for the submission status, and then retrieve the result when it's ready.
 
 @author aaronjennings

*/

public class AmazonS3ToMarketplace {
	
	/**********************************************************************************************
	 * UPDATE THE VALUES OF THESE CONSTANTS WITH YOUR OWN CREDENTIALS
	 *********************************************************************************************/
	
	// Use this constant to define the name of your Amazon S3 bucket. This gets used in the Amazon.S3.PutObject Choreo (where the actual image will be stored).
	// It is also used when submitting an image link to Amazon Marketplace.
	// Note that this bucket needs to have a read-only policy so that MWS can download the image.
	private static final String S3_BUCKET_NAME = "tembooimages";
	
	// Use these constants to define the set of Amazon Marketplace credentials that will be used to access MWS. 
	// (Replace with your own Amazon Marketplace credentials.)
	private static final String MWS_ACCESS_KEY_ID = "YOUR MWS ACCESS KEY";
	private static final String MWS_MARKETPLACE_ID = "YOUR MWS MARKETPLACE ID";
	private static final String MWS_MERCHANT_ID = "YOUR MWS MERCHANT ID";
	private static final String MWS_SECRET_KEY_ID = "YOUR MWS SECRET KEY";
	
	// Use these constants to define the set of Amazon S3 credentials that will be used to access S3. 
	// (Replace with your own Amazon S3 credentials.)
	private static final String AWS_ACCESS_KEY_ID = "YOUR AWS ACCESS KEY";
	private static final String AWS_SECRET_KEY_ID = "YOUR AWS SECRET KEY";
	
	// Use these constants to define the set of credentials that will be used to connect with Temboo.
	// (Replace with your own Temboo Application Key.)
	private static final String TEMBOO_ACCOUNT_NAME = "YOUR TEMBOO ACCOUNT NAME";
	private static final String TEMBOO_APPLICATIONKEY_NAME = "YOUR TEMBOO APP KEY NAME";
	private static final String TEMBOO_APPLICATIONKEY = "YOUR TEMBOO APP KEY";
	
	// The location of the image to upload: this should be an absolute path on your filesystem;
	// this needs to be a JPEG
	private static final String IMAGE_LOCATION = "/tmp/MyImage.jpg";
	
	/**********************************************************************************************
	 * END CONSTANTS: NOTHING BELOW THIS POINT SHOULD NEED TO BE CHANGED
	 *********************************************************************************************/

	
	
   /**
    * Main method: use the Temboo SDK to submit an inventory feed to Amazon Marketplace, upload a corresponding
    * product image to Amazon S3, and update the inventory listing to include this product image.
    * 
    * @param args
    * @throws Exception
    */
	
	public static void main(String args[]) throws Exception {
			
		// Specify the SKU of the item we're adding
		final String SKU = "778W037SRU";
		
		// Get a file handle for the image to upload
		File image = new File(IMAGE_LOCATION);
		if(!image.exists())
			throw new Exception("Whoops! The image file to upload doesn't exist; make sure the path is valid!");
		
		// Create a new Temboo session, that will be used to run Temboo SDK choreos.
		// (Replace with your own Temboo Account Name, Application Key Name, and Application Key key).
		TembooSession session = new TembooSession(TEMBOO_ACCOUNT_NAME, TEMBOO_APPLICATIONKEY_NAME, TEMBOO_APPLICATIONKEY);
		   
		// Instantiate the MarketPlace.AddOrUpdateInventoryItem choreo, using the Temboo session object
		// See https://live.temboo.com/library/Library/Amazon/Marketplace/Feeds/AddOrUpdateInventoryItem for detailed documentation
		AddOrUpdateInventoryItem addItem = new com.temboo.Library.Amazon.Marketplace.Feeds.AddOrUpdateInventoryItem(session);
		   
		// Get an InputSet object for the AddOrUpdateInventoryItem choreo, and populate the inputs. This choreo takes inputs
		// specifying the various inventory fields, and Amazon MWS credentials
		AddOrUpdateInventoryItemInputSet addItemInput = addItem.newInputSet();
		   
		addItemInput.set_AWSAccessKeyId(MWS_ACCESS_KEY_ID);          
		addItemInput.set_AWSMarketplaceId(MWS_MARKETPLACE_ID);
		addItemInput.set_AWSMerchantId(MWS_MERCHANT_ID);
		addItemInput.set_AWSSecretKeyId(MWS_SECRET_KEY_ID);
			
		addItemInput.set_ItemCondition(11);		// item condition is represented by an integer; 11 = "New" in Amazon-speak          
		addItemInput.set_Price(new BigDecimal(100.00));			// specify the item price
		addItemInput.set_ProductId("B003V7OWAI");	
		addItemInput.set_ProductIdType(1);		// The ProductIdType is associated with the ProductId that you provide. 
												// In this case, we're providing an ASIN which is represented by a 1.
		addItemInput.set_Quantity(1);
		addItemInput.set_SKU(SKU);
		
		// Do a bit of debug logging
		System.out.println("Now submitting product to Amazon Marketplace, and waiting for submission result...");
		   
		// Run the AddOrUpdateInventoryItem choreo, to submit an inventory feed with one item. (Note that after submitting the feed information,
		// the choreo will poll for the feed submission result, and finally retrieve the submission result when it's ready.)
		AddOrUpdateInventoryItemResultSet addItemResults = addItem.execute(addItemInput);
		   
		// Display the SubmissionResult.
		System.out.println("The feed has been submitted! The result from MWS was:" + String.format("%n") + addItemResults.get_SubmissionResult());
		
		
		// Base64 encode the image so that you can pass the encoded image contents to the Amazon.PutObject choreo.
		BufferedImage bufferedImg = ImageIO.read(image);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufferedImg, "jpg", baos);
		String encodedImage = Base64.encode(baos.toByteArray());
		
		// Instantiate the Amazon.S3.PutObject choreo, using the Temboo session object
		// See https://live.temboo.com/library/Library/Amazon/S3/PutBucket for detailed documentation
		PutObject putObject = new com.temboo.Library.Amazon.S3.PutObject(session);
		   
		// Get an InputSet object for the PutObject choreo, and populate the inputs. This choreo takes inputs
		// specifying the name of the bucket, file contents, file name, and AWS credentials
		PutObjectInputSet putObjectInput = putObject.newInputSet();
		   
		putObjectInput.set_AWSAccessKeyId(AWS_ACCESS_KEY_ID);          
		putObjectInput.set_AWSSecretKeyId(AWS_SECRET_KEY_ID);
		putObjectInput.set_BucketName(S3_BUCKET_NAME);
		putObjectInput.set_FileContents(encodedImage);
		putObjectInput.set_FileName(image.getName());
		
		// Run the Choreo to upload the image to S3
		putObject.execute(putObjectInput);
		
		// Do a bit of debug logging, so we know what's going on.
		System.out.println("Uploaded image " + image.getName() + " to S3 successfully!");
		
		// Instantiate the MarketPlace.Feeds.AddOrUpdateProductImage choreo, using the Temboo session object
		// See https://live.temboo.com/library/Library/Amazon/Marketplace/Feeds/AddOrUpdateProductImage for detailed documentation
		AddOrUpdateProductImage productImage = new com.temboo.Library.Amazon.Marketplace.Feeds.AddOrUpdateProductImage(session);
		   
		// Get an InputSet object for the AddOrUpdateProductImage choreo, and populate the inputs. This choreo takes inputs
		// specifying the image location, sku, and Amazon MWS credentials
		AddOrUpdateProductImageInputSet productImageInput = productImage.newInputSet();
		   
		productImageInput.set_AWSAccessKeyId(MWS_ACCESS_KEY_ID);          
		productImageInput.set_AWSMarketplaceId(MWS_MARKETPLACE_ID);
		productImageInput.set_AWSMerchantId(MWS_MERCHANT_ID);
		productImageInput.set_AWSSecretKeyId(MWS_SECRET_KEY_ID);
		productImageInput.set_ImageLocation("http://" + S3_BUCKET_NAME + ".s3.amazonaws.com/" + image.getName());          
		productImageInput.set_SKU(SKU);
		   
		// Do some debug logging...
		System.out.println("Now submitting image feed to Amazon Marketplace, and waiting for submission result...");
		
		// Run the AddOrUpdateProductImage choreo, to update the specified Marketplace item with a new image.
		AddOrUpdateProductImageResultSet productImageResults = productImage.execute(productImageInput);
		   
		// Display the SubmissionResult.
		System.out.println("The image has been submitted! The result from MWS was:" + String.format("%n") + productImageResults.get_SubmissionResult());
	}
}
 
