package com.revature.controllers;

import com.revature.annotations.Authorized;
import com.revature.dtos.ProductInfo;
import com.revature.models.Product;
import com.revature.models.User;
import com.revature.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Authorized
    @GetMapping
    public ResponseEntity<List<Product>> getInventory() {
        return ResponseEntity.ok(productService.findAll());
    }

    @Authorized
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") int id) {
        Optional<Product> optional = productService.findById(id);

        if(!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(optional.get());
    }

    @Authorized
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") int id, @RequestBody Product product, HttpSession httpSession){
        User u=(User) httpSession.getAttribute("user");
        Optional<Product> p= productService.findById(id);
        if(u.getRole().toString()=="Admin"&&p!=null){
            product.setId(id);
            product.setSale(p.get().getSale());
            product.setFeatured(p.get().isFeatured());
            return ResponseEntity.ok(productService.save(product));

        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @Authorized
    @PutMapping
    public ResponseEntity<Product> upsert(@RequestBody Product product, HttpSession session) {
        User u= (User) session.getAttribute("user");
        if(u.getRole().toString()=="Admin") {
            productService.save(product);
            product.setSale(0.00);
            product.setFeatured(false);
            return ResponseEntity.ok(productService.save(product));

        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @Authorized
    @PatchMapping
    public ResponseEntity<List<Product>> purchase(@RequestBody List<ProductInfo> metadata) { 	
    	List<Product> productList = new ArrayList<Product>();
    	
    	for (int i = 0; i < metadata.size(); i++) {
    		Optional<Product> optional = productService.findById(metadata.get(i).getId());

    		if(!optional.isPresent()) {
    			return ResponseEntity.notFound().build();
    		}

    		Product product = optional.get();

    		if(product.getQuantity() - metadata.get(i).getQuantity() < 0) {
    			return ResponseEntity.badRequest().build();
    		}
    		
    		product.setQuantity(product.getQuantity() - metadata.get(i).getQuantity());
    		productList.add(product);
    	}
        
        productService.saveAll(productList, metadata);

        return ResponseEntity.ok(productList);
    }

    @Authorized
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") int id, HttpSession session) {
        Optional<Product> optional = productService.findById(id);
        User u= (User) session.getAttribute("user");
        if (!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        if(u.getRole().toString()=="Admin") {
            productService.delete(id);
        }
        else{
            return ResponseEntity.ok("Must be logged in as Admin to perform this action");
        }
        return ResponseEntity.ok("Product is deleted");
    }
    @Authorized
    @PutMapping("/featured/{id}")
    public ResponseEntity<String> addFeaturedProduct(@PathVariable("id") int id, HttpSession session){
        User u= (User) session.getAttribute("user");
        if(u.getRole().toString()=="Admin"){
            return ResponseEntity.ok(productService.addProductToFeaturedList(id));
        }
        else {
            return ResponseEntity.ok("Must be logged in as Admin to perform this action");
        }
    }
    @Authorized
    @DeleteMapping("/featured/{id}")
    public ResponseEntity<String> deleteFeaturedProduct(@PathVariable("id") int id, HttpSession session){
        User u= (User) session.getAttribute("user");
        if(u.getRole().toString()=="Admin"){
            return ResponseEntity.ok(productService.deleteProductToFeaturedList(id));
        }
        else {
            //return ResponseEntity.notFound().build();
            return ResponseEntity.ok("Must be logged in as Admin to perform this action");
        }
    }
    @Authorized
    @GetMapping("/featured")
    public ResponseEntity<List<Product>> getFeaturedProducts() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }

    @Authorized
    @PostMapping("/sale")
    public ResponseEntity<String> updateSale(@RequestBody Map<String, Object> dto, HttpSession session){
        User u= (User) session.getAttribute("user");
        if(u.getRole().toString()=="Admin"){
            productService.updateSale(dto);
        }
        else {
            return ResponseEntity.ok("Must be logged in as Admin to perform this action");
        }
        return ResponseEntity.ok("This sale is updated");

    }

    @Authorized
    @GetMapping("/sale")
    public ResponseEntity<List<Product>> getProductsOnSale() {
        return ResponseEntity.ok(productService.getProductsOnSale());
    }

    @Authorized
    @GetMapping("/stocked")
    public ResponseEntity<List<Product>> getProductsOverZero(){
        return ResponseEntity.ok(productService.getProductsOverZero());
    }
}
