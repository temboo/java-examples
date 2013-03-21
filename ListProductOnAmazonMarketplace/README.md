
###USING THE TEMBOO SDK: LIST PRODUCT ON AMAZON MARKETPLACE EXAMPLE (Java)

This example is a working Java application that uses the Temboo SDK to list a new product on the Amazon Marketplace. The example 
uses Temboo SDK functions to submit a new listing to the Amazon Marketplace (MWS) webservice, and polls the MWS API to confirm
that the item was added successfully. The example then uploads an image for that product to Amazon S3, and makes another
call to MWS to link the image with the product.

###TO RUN THIS EXAMPLE:

 * Sign up for a free Temboo account (if you don't already have one) and Download the Temboo Java SDK
at https://www.temboo.com/download. Add the SDK as a library to your Java project. You can find instructions
for this process on the Temboo site, under "getting started" (https://www.temboo.com/public/support/getting-started).

 * Create an Amazon Seller Central account (if you don't already have one) at https://sellercentral.amazon.com/gp/homepage.html and
retrieve your Amazon Secret Key, Amazon Access Key, Amazon Seller ID and Marketplace ID -- you'll need to supply these in the example code.

 * Create an Amazon Web Services account (if you don't already have one) at https://aws.amazon.com and retrieve your 
Amazon AWS Secret Key and an Amazon AWS Access Key.

 * Create a new bucket in Amazon S3, that will be used to store the product image file. (Note that the bucket needs to have public
read-access, so that Amazon Marketplace can display the image.)

 * Edit the Java code to contain your Temboo and Amazon credentials. 

 * Run it!

###ABOUT TEMBOO

The Temboo SDK Library allows you to implement complex interactions with 3rd party services 
without worrying about the specific syntax of a 3rd-party API, by providing simple, 
native-language functions that trigger Temboo choreos. Temboo choreos are reusable
code snippets that can do almost anything, from updating your status on Facebook, to creating
a new Amazon RDS DB instance, to checking the weather in your neighborhood. 
