package com.revature.services;

import com.revature.dtos.ProductInfo;
import com.revature.models.Product;
import com.revature.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {

        return productRepository.save(product);
    }
    public String update(Product product){
        Optional<Product> optional = productRepository.findById(product.getId());
        Product newProduct = optional.get();
        if(newProduct != null){
            productRepository.save(product);
            return "Product updated";
        }else
            return "Could not update the product, Please check your changes";
    }

    public List<Product> saveAll(List<Product> productList, List<ProductInfo> metadata) {
    	return productRepository.saveAll(productList);
    }

    public void delete(int id) {
        if(productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            System.out.println("Product with that ID does no exist.");
        }
    }
    public String addProductToFeaturedList(int id){
        Optional<Product> optional = productRepository.findById(id);
        Product product = optional.get();
        if(product != null) {
            if(product.isFeatured() == true){
                return "This product is already in the featured list";
            }
            if(product.isFeatured() == false){
                product.setFeatured(true);
                productRepository.save(product);
                return "The product is added the the featured list";
            }
        }
        return "Something went wrong, please check your request";

    }
    public String deleteProductToFeaturedList(int id){
        Optional<Product> optional = productRepository.findById(id);
        Product product = optional.get();
        if(product != null){

            if(product.isFeatured() == false){
                return "This product is not in the featured list";
            }
            if(product.isFeatured() == true){
                product.setFeatured(false);
                productRepository.save(product);
                return "The product is removed from the the featured list";
            }}
        return "Something went wrong, please check your request";
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.getFeaturedProducts();
    }
    public Product updateSale(Map<String, Object> dto){

        Optional<Product> optional = productRepository.findById(Integer.parseInt(dto.get("id").toString()));
        Product product = optional.get();
        if(product != null) {
            final DecimalFormat df = new DecimalFormat("0.00");
            double sale = 0;
            if(Double.parseDouble(dto.get("sale").toString()) > 99)
                sale = 99;
            if(Double.parseDouble(dto.get("sale").toString()) < 0)
                sale = 0;
            if(Double.parseDouble(dto.get("sale").toString()) >= 0 && Double.parseDouble(dto.get("sale").toString()) <= 99)
                sale = Double.parseDouble(dto.get("sale").toString());
            double newSale = (100 - sale)/100;
            double oldSale = (100 - product.getSale())/100;
            double oldPrice = product.getPrice()/oldSale;
            double newPrice = Double.parseDouble(df.format(oldPrice*newSale));
            product.setPrice(newPrice);
            product.setSale(sale);
            productRepository.save(product);
        }

        return product;
    }
    public List<Product> getProductsOnSale() {
        return productRepository.getProductsOnSale();
    }
    public List<Product> getProductsOverZero(){
        return productRepository.getProductsOverZero();
    }
}