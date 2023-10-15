package com.example.coverself;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.coverself.controller.ProductController;
import com.example.coverself.service.ProductService;

@SpringBootApplication
public class CoverselfApplication {

    private static final Logger log = LogManager.getLogger(CoverselfApplication.class);

    private static ProductService productService;

    @Autowired
    public CoverselfApplication(ProductService productService) {
        this.productService = productService;
    }

    public static void main(String[] args) {
    	
    	String promotion = "";
    	 
        if (args.length > 0) {
            promotion = args[0];
        }else {
        	promotion = "";
        	log.info("Promotion is not specified. Proceeding without coupon.");
        }

        SpringApplication.run(CoverselfApplication.class, args);

        try {
            productService.getProductDataAndConvertToINR(promotion);
        } catch (Exception e) {
            // Handle specific exceptions here
            e.printStackTrace();
        }
    }
}
