
###USING THE TEMBOO SDK: FLICKR BACKUP EXAMPLE (Java)

This example is a working Java application that uses the Temboo SDK to back up photos in your 
Flickr account to folders on Dropbox which correspond to the Flickr photo sets that contain the photos. The application:

 * Connects to Flickr and retrieves a list of photo sets 
 * Iterates over the photo set list, and retrieves a list of photos within each photo set
 * Iterates over the list of photos, and downloads each one, then uploads it to Dropbox

###TO RUN THIS EXAMPLE:

 * Sign up for a free Temboo account (if you don't already have one) and Download the Temboo Java SDK
at https://www.temboo.com/download. Add the SDK as a library to your Java project. You can find instructions
for this process on the Temboo site, under "getting started" (https://www.temboo.com/public/support/getting-started).

 * Create a Flickr account and a Dropbox account (if you don't already have one). 
In the Java code, you'll need to supply your Flickr and Dropbox oAuth credentials.
You can find more information on how to obtain Flickr and Dropbox oAuth credentials at these pages:
http://www.flickr.com/services/api/auth.oauth.html
https://www.dropbox.com/developers/reference/api

 * Edit the Java code to contain your Temboo, Flickr, and Dropbox credentials. 

 * Run it!

###ABOUT TEMBOO

The Temboo SDK Library allows you to implement complex interactions with 3rd party services 
without worrying about the specific syntax of a 3rd-party API, by providing simple, 
native-language functions that trigger Temboo choreos. Temboo choreos are reusable
code snippets that can do almost anything, from updating your status on Facebook, to creating
a new Amazon RDS DB instance, to checking the weather in your neighborhood. 
