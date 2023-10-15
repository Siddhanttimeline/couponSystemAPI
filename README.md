# coverSelfAssignment

## Please find the attached api documentation below which contains the request and response structure.
[https://documenter.getpostman.com/view/21660346/2s9YR6ZZ3o]

## Tech and Framework used
1. Java version: 20.0.1
2. Spring Boot (version 2.5.4)
3. Maven (version 3.8.5)
4. Log4j2 (2.14.1)
5. IDE : Eclipse IDE  (Version: 2023-09 (4.29.0)) 

## Rest API
1. Created a rest controller for the end point api/products/{promotionSet}
2. promotionSet can take values : promotionSetA and promotionSetB to apply the respective discount coupouns.
3. When no promotionSet is given then the api will continue without applying any discount coupon.
4. In ProductService, hitting the mentioned urls to get the product lists and exchange rate.
5. Performing the expected operations on the results.
6. Created a mehtod to write the response : This private method writes the given data to a JSON file located at the specified directory (D:/). It first creates the necessary directories if they do not exist, then writes the data to the file named output.json.
7. java -jar coverself-0.0.1-SNAPSHOT.jar promotionSetA / java -jar coverself-0.0.1-SNAPSHOT.jar promotionSetB to run the jar.
