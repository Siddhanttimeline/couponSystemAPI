package com.example.coverself.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ProductService {
	
    private static final Logger log = LogManager.getLogger(ProductService.class);
    
    @Value("${product.list.url}")
    private String PRODUCT_LIST_URL;

    @Value("${exchange.rate.url}")
    private String EXCHANGE_RATE_URL;

	private final ObjectMapper objectMapper; 
	private final RestTemplate restTemplate;

	public ProductService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.restTemplate = new RestTemplate();
	}

	public String getProductDataAndConvertToINR(String promotion) {
		System.out.println(" :::::::::::::::::: IN getProductDataAndConvertToINR ::::::::::::::::::::");
		
	    log.info("getProductDataAndConvertToINR Service Called");
		
		// Make a GET request to the PRODUCT_LIST_URL endpoint and retrieve the product list.
		String productList = restTemplate.getForObject(PRODUCT_LIST_URL, String.class);

		// Make a GET request to the EXCHANGE_RATE_URL endpoint and retrieve the rate list.		
		String lastestCurrancyRates = restTemplate.getForObject(EXCHANGE_RATE_URL, String.class);

		JsonNode jsonNode = null;
		JsonNode productListsNode = null;
		
		// Parsing latestCurrency Rates
		try {
			jsonNode = objectMapper.readTree(lastestCurrancyRates);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		jsonNode = jsonNode.get("rates");
		String ratesValue = jsonNode.toString();
		System.out.println("Rates: " + ratesValue);

		try {
			jsonNode = objectMapper.readTree(ratesValue);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Parsing product lists
		try {
			productListsNode = objectMapper.readTree(productList);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<JsonNode> modifiedProductList = new ArrayList<>();

		for (JsonNode product : productListsNode) {
			if (!product.get("currency").asText().equals("INR")) {
				String currency = product.get("currency").asText();
				double price = product.get("price").asDouble();
				double rate = jsonNode.get(currency).asDouble(); // from latest rates
				double newPrice = price * rate;
				((ObjectNode) product).put("price", newPrice);
				((ObjectNode) product).put("currency", "INR");

				// Create a new ObjectNode with modified price and currency
				ObjectNode modifiedProduct = ((ObjectNode) product).deepCopy();
				modifiedProduct.put("price", newPrice);
				modifiedProduct.put("currency", "INR");
				modifiedProductList.add(modifiedProduct);
			} else {
				// If the currency is already INR, add the product without modification
				modifiedProductList.add(product);
			}
		}
		
		String discountResponse = applyDiscount(modifiedProductList, promotion);

		return discountResponse;
	}

	private String applyDiscount(List<JsonNode> modifiedProductList, String promotion) {
		
	    log.info("applyDiscount Called");

		for (JsonNode product : modifiedProductList) {
			double discountOriginAmount = 0;
			double discountRatingAmount = 0;
			double discountCategoryAmount = 0;
			double maxDiscountAmount = 0;
			double discountInventoryAmount = 0;
			double discountArrivalAmount = 0;


			String origin = product.has("origin") ? product.get("origin").asText() : "";
			double price = product.has("price") ? product.get("price").asDouble() : 0;
			double rating = product.has("rating") ? product.get("rating").asDouble() : 0;
			double inventory = product.has("inventory") ? product.get("inventory").asDouble() : 0;
			String category = product.has("category") ? product.get("category").asText() : "";
			String arrival = product.has("arrival") ? product.get("arrival").asText() : "";
					
		    StringBuilder discountAppliedStringBuilderSETA = new StringBuilder("");

			
			if (promotion.equalsIgnoreCase("promotionSetA")) {
			    log.info("promotionSetA applied");
			    

			    
				if (origin.equalsIgnoreCase("Africa")) {
					discountOriginAmount = 0.07 * price;
					discountAppliedStringBuilderSETA.append("Origin Discount Applied for 7%");

				}

				if (rating == 2) {
					discountRatingAmount = 0.04 * price;
					discountAppliedStringBuilderSETA.append(", Rating Discount Applied for 4%");
				}

				if (rating < 2) {
					discountRatingAmount = 0.08 * price;
					discountAppliedStringBuilderSETA.append(", Rating Discount Applied for 8%");
				}

				if ((category.equals("electronics") || category.equals("furnishing")) && price >= 500) {
					discountCategoryAmount = 100; // Ensure the discount doesn't exceed the product price needs to be done
					discountAppliedStringBuilderSETA.append(", Category Discount Applied for 100INR");
				}

				maxDiscountAmount = Math.max(discountOriginAmount,
						Math.max(discountRatingAmount, discountCategoryAmount));
				
				

			}else if(promotion.equalsIgnoreCase("promotionSetB")){
			    log.info("promotionSetB applied");

				if(inventory > 20) {
					discountInventoryAmount = 0.12 * price;
					discountAppliedStringBuilderSETA.append("Inventory Discount Applied for 12%");
				}
				
				if(arrival.equalsIgnoreCase("NEW")) {
					discountArrivalAmount = 0.07 * price;
					discountAppliedStringBuilderSETA.append("Arrival Discount Applied for 7%");
				}
				
				maxDiscountAmount = Math.max(discountInventoryAmount, discountArrivalAmount);
			}else {
				log.info("No promotion set applied");
			}

			// when no discount coupon is applied and price is > 1000
			if(price > 1000 && maxDiscountAmount == 0 ) {
				maxDiscountAmount = 0.02 * price;
				discountAppliedStringBuilderSETA.append("Discount applied for 2%");
			}
			
	        ObjectNode discountObject = JsonNodeFactory.instance.objectNode();
	        discountObject.put("amount", maxDiscountAmount );
	        discountObject.put("discountTag",discountAppliedStringBuilderSETA.toString());
			
			((ObjectNode) product).put("price", price);
	        ((ObjectNode) product).set("discount", discountObject);
	        
		}
		
		String writeToFile = writeToFile(modifiedProductList.toString());
		return writeToFile;
	}

	private String writeToFile(String data) {
	    log.info("writeToFile Called");

	    String targetDirectory = "D:/";
	    String fileName = "output.json";
	    try {
	        Files.createDirectories(Paths.get(targetDirectory));
	    } catch (IOException e) {
	        log.error("Error creating directories: {}", e.getMessage());
	        return "Error creating directories";
	    }
	    String filePath = Paths.get(targetDirectory, fileName).toString();

	    try {
	        Files.write(Paths.get(filePath), data.getBytes());
	        log.info("Data written to file: {}", filePath);
	        return "Data written successfully to file: " + filePath;
	    } catch (IOException e) {
	        log.error("Error writing to file: {}", e.getMessage());
	        return "Error writing to file";
	    }
	}

}
