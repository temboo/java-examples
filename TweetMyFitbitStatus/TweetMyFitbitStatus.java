
import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.temboo.Library.Fitbit.GetTimeSeriesByPeriod;
import com.temboo.Library.Fitbit.GetTimeSeriesByPeriod.GetTimeSeriesByPeriodInputSet;
import com.temboo.Library.Fitbit.GetTimeSeriesByPeriod.GetTimeSeriesByPeriodResultSet;
import com.temboo.Library.Twitter.Tweets.StatusesUpdate;
import com.temboo.Library.Twitter.Tweets.StatusesUpdate.StatusesUpdateInputSet;
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
   
   
 This simple Java application  demonstrates how to get started building cool Fibit apps with the Temboo SDK. 
 To run the demo, you'll need a Temboo account, and oAuth credentials for both Twitter and Fitbit.
 
 The demo uses Temboo SDK functions to retrieve the number of "steps" you've taken today from the Fitbit API,
 and based on whether or not you've reached a predefined goal for the number of steps you aimed to take, sends
 out a Tweet either proclaiming your success or admitting your shortfall.
 
  @author aaronjennings
*/

public class TweetMyFitbitStatus {
	
	/**********************************************************************************************
	 * UPDATE THE VALUES OF THESE CONSTANTS WITH YOUR OWN CREDENTIALS AND MESSAGES
	 *********************************************************************************************/

	
	// Okay, first things first -- set the messages that will be Tweeted based on whether or not you
	// met your goal. (Remember, Tweets are limited to 140 characters.)
	private static final String GOAL_MET_MESSAGE = "Iron Man triathalon, here I come!";
	private static final String GOAL_NOT_MET_MESSAGE = "Today I was a couch potato. Sigh.";
	
	// Specify your benchmark. This represents that amount of steps that you think you should hit.
	// Fitbit defaults to a goal of 10,000, but you can adjust that here if you like.
	private static final int BENCHMARK = 10000;
	
	// Use these constants to define the set of Fitbit oauth credentials that will be used to access your Fitbit account. 
	// If you don't have these yet, go to https://dev.fitbit.com/ and register your app.
	// You will get the OauthConsumerKey and OauthConsumerSecret after registering.
	// Follow the instructions here to retrieve your Token and TokenSecret: https://wiki.fitbit.com/display/API/OAuth+Authentication+in+the+Fitbit+API
	// (Replace with your own Fitbit credentials.)
	private static final String FITBIT_CONSUMER_KEY = "YOUR FITBIT OAUTH CONSUMER KEY";
	private static final String FITBIT_CONSUMER_SECRET = "YOUR FITBIT OAUTH CONSUMER SECRET";
	private static final String FITBIT_ACCESS_TOKEN = "YOUR FITBIT OAUTH TOKEN";
	private static final String FITBIT_ACCSS_TOKEN_SECRET = "YOUR FITBIT OAUTH TOKEN SECRET";
	
	// Use these constants to define the set of Twitter credentials that will be used to access your Twitter account. 
	// If you don't have these yet, sign up for a dev account and register your app here: https://dev.twitter.com/. You will be given the oauth creds that are needed.
	// (Replace with your own Twitter oauth credentials.)
	private static final String TWITTER_CONSUMER_KEY = "YOUR TWITTER OAUTH CONSUMER KEY";
	private static final String TWITTER_CONSUMER_SECRET = "YOUR TWITTER OAUTH CONSUMER SECRET";
	private static final String TWITTER_ACCESS_TOKEN = "YOUR TWITTER OAUTH TOKEN";
	private static final String TWITTER_ACCESS_TOKEN_SECRET = "YOUR TWITTER OAUTH TOKEN SECRET";
	
	// Use these constants to define the set of credentials that will be used to connect with Temboo.
	// (Replace with your own Temboo Application Key.)
	private static final String TEMBOO_ACCOUNT_NAME = "YOUR TEMBOO ACCOUNT NAME";
	private static final String TEMBOO_APPLICATIONKEY_NAME = "YOUR TEMBOO APP KEY NAME";
	private static final String TEMBOO_APPLICATIONKEY = "YOUR TEMBOO APP KEY";

	/**********************************************************************************************
	 * END CONSTANTS: NOTHING BELOW THIS POINT SHOULD NEED TO BE CHANGED
	 *********************************************************************************************/

	/**
	* Main method: use the Temboo SDK to retrieve Fitbit time series data for today, parse the amount of steps you've taken today, compare that number to
	* pre-defined benchmark, and update your Twitter feed with a message based on the result.
	* 
	* @param args
	* @throws Exception
	*/
	public static void main(String args[]) throws Exception {

		// Create a new Temboo session, that will be used to run Temboo SDK choreos.
		// (Replace with your own Temboo Account Name, Application Key Name, and Application Key key).
		TembooSession session = new TembooSession(TEMBOO_ACCOUNT_NAME, TEMBOO_APPLICATIONKEY_NAME, TEMBOO_APPLICATIONKEY);
		
		// Instantiate the Fitbit.GetTimeSeriesByPeriod choreo, using the Temboo session object.
		GetTimeSeriesByPeriod getTimeSeries = new com.temboo.Library.Fitbit.GetTimeSeriesByPeriod(session);
		   
		// Get an InputSet object for the GetTimeSeriesByPeriod choreo, and populate the inputs. This choreo takes inputs
		// specifying some time period params, a Fitbit resource path, and your Fitbit oauth credentials.
		GetTimeSeriesByPeriodInputSet getTimeSeriesInput = getTimeSeries.newInputSet();
		   
		getTimeSeriesInput.set_EndDate("today");          
		getTimeSeriesInput.set_ConsumerKey(FITBIT_CONSUMER_KEY);
		getTimeSeriesInput.set_ConsumerSecret(FITBIT_CONSUMER_SECRET);
		getTimeSeriesInput.set_AccessToken(FITBIT_ACCESS_TOKEN);
		getTimeSeriesInput.set_AccessTokenSecret(FITBIT_ACCSS_TOKEN_SECRET);          
		getTimeSeriesInput.set_Period("1d");
		getTimeSeriesInput.set_ResourcePath("activities/steps");
		   
		// Run the GetTimeSeriesByPeriod choreo, to retrieve the amount of steps that you've taken today from FitBit.
		GetTimeSeriesByPeriodResultSet getTimeSeriesResults = getTimeSeries.execute(getTimeSeriesInput);
		   
		// Print some status info, to make sure this thing is working
		System.out.println("Retrieved XML from Fitbit");
		
		// Convert the data into XML.
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);

		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(
				new InputSource(
						new ByteArrayInputStream(
								getTimeSeriesResults.get_Response().getBytes("utf-8"))));
	 			
		// Extract the <value> element inside <activities-steps>.
		NodeList steps = doc.getElementsByTagName("value");
		Element step = (Element) steps.item(0);
		String stepsValue = step.getTextContent();
		
		// Print out the amount of steps we parsed from the Fitbit XML response.
		System.out.println("Fitbit says that you have walked " + stepsValue + " steps!");
		
		// Based on the amount of steps we retrieved from Fitbit, we'll tweet your shameful or braggy message.
		int i = Integer.parseInt(stepsValue);
		if (i < BENCHMARK) {
            
			// Do a little logging to make sure this thing is working.
			System.out.println("For shame!");
			
			// Create tweet (by calling the StatusesUpdate method, below)
			// Use the Shameful message since the steps retrieved from Fitbit are < than the benchmark specified.
			createTweet(session, GOAL_NOT_MET_MESSAGE);
            
		} else if (i >= BENCHMARK) {
            
			// Do a little logging to make sure this thing is working.
			System.out.println("Wow, you're good!");
			
			// Create tweet (by calling the StatusesUpdate method, below)
			// Use the Braggy message since the steps retrieved from Fitbit are >= than the benchmark specified.
			createTweet(session, GOAL_MET_MESSAGE);
		}
	}
  
	
	/**
	* This convenience function is used to update your Twitter feed (using a Temboo SDK choreo).
	* 
	* @param session - A valid Temboo Session object
	* @param message - The message that will be used for the tweet
	* @throws TembooException
	*/ 
	private static void createTweet(TembooSession session, String message) throws TembooException {
            
		// Create a Twitter.StatusesUpdate choreo, that will be used to update your Twitter feed, using the session object (as always)
		StatusesUpdate statusesUpdate = new com.temboo.Library.Twitter.Tweets.StatusesUpdate(session);
		                
		// Get an InputSet object for Twitter.StatusesUpdate, and configure it
		StatusesUpdateInputSet statusesUpdateInput = statusesUpdate.newInputSet();
		statusesUpdateInput.set_ConsumerKey(TWITTER_CONSUMER_KEY);
		statusesUpdateInput.set_ConsumerSecret(TWITTER_CONSUMER_SECRET);
		statusesUpdateInput.set_AccessToken(TWITTER_ACCESS_TOKEN);
		statusesUpdateInput.set_AccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);          
		statusesUpdateInput.set_StatusUpdate(message);
            
		try {
			// Run the choreo to update your twitter feed (Note that in this case, we don't care about the results returned by the choreo)
			statusesUpdate.execute(statusesUpdateInput);
			
			System.out.println("Successfully tweeted: " + message);
		} catch(TembooException e) {
			System.out.println("Uh-oh! Something went wrong trying to update your Twitter status. The error from the choreo was: " + e.getMessage());
			throw e;
		}
	}
}

