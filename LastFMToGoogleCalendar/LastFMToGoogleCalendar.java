
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.temboo.Library.Google.Calendar.CreateEvent;
import com.temboo.Library.Google.Calendar.SearchCalendarsByName;
import com.temboo.Library.Google.Calendar.CreateEvent.CreateEventInputSet;
import com.temboo.Library.Google.Calendar.SearchCalendarsByName.SearchCalendarsByNameInputSet;
import com.temboo.Library.Google.Calendar.SearchCalendarsByName.SearchCalendarsByNameResultSet;
import com.temboo.Library.LastFm.Artist.GetEvents;
import com.temboo.Library.LastFm.Artist.GetEvents.GetEventsInputSet;
import com.temboo.Library.LastFm.Artist.GetEvents.GetEventsResultSet;
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


This simple Java application demonstrates how to get started building apps that integrate Last.fm and Google Calendar. 
To run the demo, you'll need a Temboo account, a Last.fm API Key, and oAuth 2.0 credentials for Google Calendar.

The demo uses Temboo SDK functions to retrieve an XML list of Last.fm "events" associated with a list of your favorite bands, 
extracts the artist name, venue, city, and date for each event item, 
and adds an event to your Google Calendar if the event occurs in the city that you specify.

@author aaronjennings
*/

public class LastFMToGoogleCalendar {

	// Use this constant to specify your city. If events are found in this city, it will be added to your Google Calendar.
	private static final String MY_CITY = "New York";

	// For this constant, specify a list of band names that may have events on Last.fm that you want to add to Google Calendar.
	private static String [] BAND_NAMES = {"First Aid Kit", "Slow Club", "Beach House"};

	// Use this constant to define your Last.fm API KEY.
	// You can apply for an API Key by going here: http://www.last.fm/api/account.
	// (Replace with your own Last.fm API Key.)
	private static final String LAST_FM_API_KEY = "YOUR LAST.FM API KEY";

	// Use this constant to define your Google oAuth 2.0 credentials.
	// If you don't already have the oAuth credentials associated with your Google account, login to your Google account, 
	// create a project and generate your oAuth 2.0 ClientID and ClientSecret here https://code.google.com/apis/console/.
	// After doing that, use Google's oAuth playground to generate your AccessToken and RefreshToken here: https://code.google.com/oauthplayground/.
	private static final String GOOGLE_CLIENT_ID = "YOUR GOOGLE CLIENT ID";
	private static final String GOOGLE_CLIENT_SECRET = "YOUR GOOGLE CLIENT SECRET";
	private static final String GOOGLE_ACCESS_TOKEN = "YOUR GOOGLE ACCESS TOKEN";
	private static final String GOOGLE_REFRESH_TOKEN = "YOUR GOOGLE REFRESH TOKEN";

	// Set your calendar name here. Make sure you provide the name of an existing Google calendar.
	// Note, if there are multiple calendars with the same name, the first one returned will be used.
	private static final String GOOGLE_CALENDAR_NAME = "MyConcerts";
	
	// Use these constants to define the set of credentials that will be used to connect with Temboo.
	// (Replace with your own Temboo Application Key.)
	private static final String TEMBOO_ACCOUNT_NAME = "YOUR TEMBOO ACCOUNT NAME";
	private static final String TEMBOO_APPLICATIONKEY_NAME = "YOUR TEMBOO APP KEY NAME";
	private static final String TEMBOO_APPLICATIONKEY = "YOUR TEMBOO APP KEY";
	
	/**********************************************************************************************
	 * END CONSTANTS: NOTHING BELOW THIS POINT SHOULD NEED TO BE CHANGED
	 *********************************************************************************************/

	
	/**
	 * Main method: use the Temboo SDK to retrieve events from Last.fm, search for events for a particular artist, in a particular city,
	 * and create Google Calendar events that correspond to these events.
	 * 
	 * @param args
	 * @throws Exception
	 */
	
	// Create a new Temboo session, that will be used to run Temboo SDK choreos.
	// (Replace with your own Temboo Application Key name + key.)
	public static void main(String[] args) throws Exception {
		
		// Create a new Temboo session, that will be used to run Temboo SDK choreos.
		// (Replace with your own Temboo Account Name, Application Key Name, and Application Key key).
		TembooSession session = new TembooSession(TEMBOO_ACCOUNT_NAME, TEMBOO_APPLICATIONKEY_NAME, TEMBOO_APPLICATIONKEY);
	
		// To create a calendar event, we need the id of the calendart to which it should be added. Execute the SearchCalendarsByName choreo to get this id.
		// Instantiate the Google.Calendar.SearchCalendarsByName Choreo, using the session object
		// See https://live.temboo.com/library/Library/Google/Calendar/SearchCalendarsByName for detailed documentation
		SearchCalendarsByName searchCals = new com.temboo.Library.Google.Calendar.SearchCalendarsByName(session);
		
		// Get an InputSet object for the SearchCalendarsByName, and populate the inputs. This choreo takes inputs
		// specifying a calendar name and your oAuth 2.0 credentials.
		SearchCalendarsByNameInputSet searchCalsInput = searchCals.newInputSet();
		
		searchCalsInput.set_AccessToken(GOOGLE_ACCESS_TOKEN);
		searchCalsInput.set_CalendarName(GOOGLE_CALENDAR_NAME);
		searchCalsInput.set_ClientID(GOOGLE_CLIENT_ID);
		searchCalsInput.set_ClientSecret(GOOGLE_CLIENT_SECRET);
		searchCalsInput.set_RefreshToken(GOOGLE_REFRESH_TOKEN);
		
		// Execute SearchCalendarsByName and retrieve the results from Google Calendar.
		SearchCalendarsByNameResultSet searchCalsResults = searchCals.execute(searchCalsInput);
		
		// Print some debugging info with the calendar id that was returned.
		System.out.println("Retrieved calendar id for " + GOOGLE_CALENDAR_NAME + ": " + searchCalsResults.get_CalendarId());

		// Use this flag to keep track of whether we found any events to add to Google Cal
		boolean eventAdded = false;
		
		// Iterate over band names.
		for( String bandName : BAND_NAMES ) {

			// Instantiate the LastFM.Artists.GetEvents Choreo, using the session object.
			// See https://live.temboo.com/library/Library/LastFm/Artist/GetEvents for detailed documentation
			GetEvents getEvents = new com.temboo.Library.LastFm.Artist.GetEvents(session);
			
			// Get an InputSet object for GetEvents, and populate the inputs. This choreo takes inputs
			// specifying a band name, a limit of events to return, and your Last.fm API Key.
			GetEventsInputSet getEventsInput = getEvents.newInputSet();
			
			getEventsInput.set_APIKey(LAST_FM_API_KEY);
			getEventsInput.set_Artist(bandName);
			getEventsInput.set_Limit(50);
			
			// Execute GetEvents, and retrieve event results from Last.fm.
			GetEventsResultSet getEventsResults = getEvents.execute(getEventsInput);
			
			// Print some debugging info to make sure that this is working.
			System.out.println("Retrieved XML results for " + bandName + " events from Last.fm");
			
			// Convert the Last.fm data into XML.
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(
					new InputSource(
							new ByteArrayInputStream(
									getEventsResults.get_Response().getBytes("utf-8"))));

			// Extract the set of elements from the result XML.
			NodeList event = doc.getElementsByTagName("event");
			
			// Loop over the set of <event> elements, and extract startDate, artist, city, and venue name from each event.
			for(int i=0; i < event.getLength(); i++) {			

				Element eventEntry = (Element) event.item(i);
				
				// Extract the <startDate> element for this status.
				NodeList eventDates = eventEntry.getElementsByTagName("startDate");
				Element eventDate = (Element) eventDates.item(0);
				String formattedDate = eventDate.getTextContent();
				
				// Extract the <artist> element for this status.
				NodeList artists = eventEntry.getElementsByTagName("artist");
				Element artist = (Element) artists.item(0);
				String artistName = artist.getTextContent();
				
				// Extract the <city> element for this status.
				NodeList cities = eventEntry.getElementsByTagName("city");
				Element city = (Element) cities.item(0);
				String cityName = city.getTextContent();
				
				// Extract the <venue><name> element for this status.
				NodeList venues = eventEntry.getElementsByTagName("name");
				Element venue = (Element) venues.item(0);
				String venueName = venue.getTextContent();
				
				// If the value of <city> = _MY_CITY (i.e. New York) then retrieve the calendar id by searching calendars by name and create a new Google Calendar event.
				if (cityName.contains(MY_CITY))
				{

					// Print out some debugging info.
					System.out.println("Found an event for " + bandName + " in my city!");
					
					// The date that was parsed from the Last.fm response needs to be formatted differently when it is passed to the Google Calendar CreateEvent Choreo. 
					// Create SimpleDateFormat object with source string date format.
					SimpleDateFormat dateSource = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
					
					// Parse the string into Date object. Note that formattedDate is the variable that we stored the Last.fm event date in.
					Date date = dateSource.parse(formattedDate);
					
					// Create SimpleDateFormat object with desired date and time format.
					// Note that you might need to adjust this depending on what timezone you're in.
					SimpleDateFormat dateTarget = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat timeTarget = new SimpleDateFormat("HH:mm:ss");
					
					// Parse the date and time into different formats.
					formattedDate = dateTarget.format(date);	      
					String formattedTime = timeTarget.format(date);
					
					// Print the date and time formatting result for debugging purposes.
					System.out.println("Converted date is: " + formattedDate);
					System.out.println("Converted time is: " + formattedTime);
					
					// Now we're ready to create the event in the Google Calendar.
					// Instantiate the Google.Calendar.CreateEvent Choreo, using the session object.
					// See https://live.temboo.com/library/Library/Google/Calendar/CreateEvent for detailed documentation
					CreateEvent createEvent = new com.temboo.Library.Google.Calendar.CreateEvent(session);
					
					// Get an InputSet object for the CreateEvent, and populate the inputs. This choreo takes inputs
					// specifying calendar id (which we retrieved already), start/end dates and times, event description, event title, and your oAuth 2.0 credentials.
					// Note that we'll just pass in the same date and time for start and end dates because we don't really know how long the concert will last.
					CreateEventInputSet createEventInput = createEvent.newInputSet();
					
					createEventInput.set_AccessToken(GOOGLE_ACCESS_TOKEN);
					createEventInput.set_CalendarID(searchCalsResults.get_CalendarId());
					createEventInput.set_ClientID(GOOGLE_CLIENT_ID);
					createEventInput.set_ClientSecret(GOOGLE_CLIENT_SECRET);
					createEventInput.set_EndDate(formattedDate);
					createEventInput.set_EndTime(formattedTime);
					createEventInput.set_EventDescription(venueName);
					createEventInput.set_EventTitle(artistName);
					createEventInput.set_RefreshToken(GOOGLE_REFRESH_TOKEN);
					createEventInput.set_StartDate(formattedDate);
					createEventInput.set_StartTime(formattedTime);
					
					try {
						// Execute CreateEvent (Note that in this case, we don't care about the results returned by the choreo).
						createEvent.execute(createEventInput);

						// Update flag to reflect that we've added an event
						eventAdded = true;
						
						System.out.println("Created a Google Calendar event for " + bandName + " on " + formattedDate + " at " + formattedTime);
					} catch(TembooException e) {
						System.out.println("Uh-oh! Something went wrong created the event in the Google Calendar. The error from the choreo was: " + e.getMessage());
					}

				}
			}
		}
		
		if(!eventAdded)
			System.out.println("Oops! We didn't find any Last.FM events that match the specified band name(s) and city. Nothing was added to Google Calendar.");

	}
}