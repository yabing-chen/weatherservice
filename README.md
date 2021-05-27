# weatherservice
Weather Restful API Design

Main components
•	Controller – the entry point of the restful service
o	It returns a Callable service, which make the service not blocking to any further incoming requests.
o	It also calls the validator below before invoking business logic.
•	Validator – To validate the request and header (x-api-key)
o	Checking if x-api-key is missing
o	Checking if x-api-key is invalid or not (predefined in DB table APIKey)
o	Check if the rate limit of the x-api-key is still available.
•	Service – the business layer to decide when to call DB or Open Weather service
o	The cached weather data in DB will be expired as pre-configured 2 hours in application.properties. (Assume weather changes every 2 hours). 
o	APIKeyService provides rate limit bucket per API Key (using Bucket4J), header X-Rate-Limit-Remaining is returned in API response to indicate how many limits remaining in that hour when there is any remaining.
•	DBRepository – to read and update weather to DB table Weather.
•	OpenWeatherRepository – to invoke OpenWeather web service

Test cases (all working):
o	Unit test cases created for the controller and the 2 main services classes
o	Integration test cases created for end-to-end test from controller to DB.

High level sequence diagram

 

Instruction to run:
o	Unzip the zip file and run the command in the unzipped folder: ./gradlew bootRun
o	Import the unzipped folder to IntelliJ and build as gradle project, and run the Application.java.
o	Once service started, use postman or curl command as below:
curl --location --request GET 'http://localhost:8080/api/v1/weather/au/Melbourne' \
--header 'x-api-key: 35959dcc-b67e-4107-bee3-c6a4db60fb45'
![Uploading image.png…]()
