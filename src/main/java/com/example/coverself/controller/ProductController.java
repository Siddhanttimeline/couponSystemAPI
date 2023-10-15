package com.example.coverself.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.coverself.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {  
	
    private static final Logger log = LogManager.getLogger(ProductController.class);

	private final ProductService productService;
	
	@Autowired
	public ProductController(ProductService productService) {
	    this.productService = productService;
	}
	
	@GetMapping("/products/{promotionSet}")
	public String getProductData(@PathVariable String promotionSet) {
		log.info("getProductData Controller called");
	    System.out.println(" :::::::::::::::::: IN getProductData CONTROLLER::::::::::::::::::::");
	    System.out.println("promotionSet : " + promotionSet);
	    return productService.getProductDataAndConvertToINR(promotionSet);
	}


}
