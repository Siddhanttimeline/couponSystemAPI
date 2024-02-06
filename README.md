# couponSystemAPI

## Please refer to the API documentation for the request and response structure, as well as information on discount criteria and logic.
[API Documentation]( https://www.notion.so/Coupon-System-API-0402639953b243e7be4aa70cef7bb396?pvs=4 )



## Tech and Framework used
1. Java version: 20.0.1
2. Spring Boot (version 2.5.4)
3. Maven (version 3.8.5)
4. Log4j2 (2.14.1)
5. IDE : Eclipse IDE  (Version: 2023-09 (4.29.0)) 

## Rest API

- Retrieve the product details by calling the third-party JSON API.
- Convert the prices to INR if the price is not in INR using exchangeratesapi.io.
- Save the JSON file (named output.json) into the target folder for backup of the applied discounts (Here we can gather other required data if necessary according to the company using the API - In Development).
- Created a REST controller for the endpoint api/products/{promotionSet}.
- PromotionSet can take values: promotionSetA and promotionSetB to apply the respective discount coupons.
- When no promotionSet is provided, the API will continue without applying any discount coupons.
- In ProductService, fetch the product lists and exchange rates by hitting the mentioned URLs.
- Perform the expected operations on the results.
- Created a method to write the response: This private method writes the given data to a JSON file located at the specified directory (D:/). It first creates the necessary directories if they do not exist, then writes the data to the file named output.json.
- Use java -jar coverself-0.0.1-SNAPSHOT.jar promotionSetA or java -jar coverself-0.0.1-SNAPSHOT.jar promotionSetB to run the JAR.
