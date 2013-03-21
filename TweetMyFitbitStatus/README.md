
###USING THE TEMBOO SDK: TWEET MY FITBIT STATUS EXAMPLE (Java)

This example is a working Java application that uses the Temboo SDK to retrieve the number of steps you've logged 
today from the Fitbit API and, based on whether you've reached a pre-defined exercise goal, sends out a message
via Twitter announcing your success or failure to meet that goal. 

###TO RUN THIS EXAMPLE:

 * Sign up for a free Temboo account (if you don't already have one) and Download the Temboo Java SDK
at https://www.temboo.com/download. Add the SDK as a library to your Java project. You can find instructions
for this process on the Temboo site, under "getting started" (https://www.temboo.com/public/support/getting-started).

 * Create a Fitbit account (if you don't already have one) and register your app at https://dev.fitbit.com/ to get the Fitbit
OauthConsumerKey and OauthConsumerSecret, that you'll need to configure the example. 

 * Follow the instructions at https://wiki.fitbit.com/display/API/OAuth+Authentication+in+the+Fitbit+API to get your
Fitbit Token and TokenSecret, that you'll need to configure the example.

 * Sign up for a Twitter developer account (if you don't already have one) and register your app at https://dev.twitter.com/. 
You will be given the Oauth creds that you'll need to configure the example. 

 * Edit the Java code to contain your Temboo, Fitbit, and Twitter credentials. 

 * Run it!

###ABOUT TEMBOO

The Temboo SDK Library allows you to implement complex interactions with 3rd party services 
without worrying about the specific syntax of a 3rd-party API, by providing simple, 
native-language functions that trigger Temboo choreos. Temboo choreos are reusable
code snippets that can do almost anything, from updating your status on Facebook, to creating
a new Amazon RDS DB instance, to checking the weather in your neighborhood. 
