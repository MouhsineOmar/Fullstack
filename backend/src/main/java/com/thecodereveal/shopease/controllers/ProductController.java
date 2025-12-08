package com.thecodereveal.shopease.controllers;

import com.thecodereveal.shopease.dto.ProductDto;
import com.thecodereveal.shopease.entities.Product;
import com.thecodereveal.shopease.services.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils; // Import gardé même si non utilisé explicitement ici
import org.springframework.http.HttpStatus; // AJOUTÉ : Nécessaire pour HttpStatus.OK
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    private final ProductService productService; // "final" est une bonne pratique, mais optionnel

    // CORRECTION 1 : Assignation du service dans le constructeur
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID typeId,
            @RequestParam(required = false) String slug,
            HttpServletResponse response
    ){
        List<ProductDto> productList = new ArrayList<>();

        // CORRECTION 2 & 3 : Suppression du doublon de méthode et ajout du IF manquant
        if (slug != null) {
            ProductDto productDto = productService.getProductBySlug(slug);
            if (productDto != null) {
                productList.add(productDto);
            }
        } else {
            // Cette méthode doit exister dans votre ProductService avec ces 2 arguments
            productList = productService.getAllProducts(categoryId, typeId);
        }

        // Gestion du header (votre logique)
        response.setHeader("Content-Range", String.valueOf(productList.size()));

        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id){
        ProductDto productDto = productService.getProductById(id);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    // create Product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDto productDto){
        Product product = productService.addProduct(productDto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody ProductDto productDto, @PathVariable UUID id){
        Product product = productService.updateProduct(productDto, id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
}