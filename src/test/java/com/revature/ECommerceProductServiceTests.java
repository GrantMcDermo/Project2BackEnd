package com.revature;

import com.revature.dtos.ProductInfo;
import com.revature.models.Product;
import com.revature.repositories.ProductRepository;
import com.revature.services.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)

public class ECommerceProductServiceTests {

    @Mock ProductRepository productRepository;
    @Mock Product mockProduct;
    @Mock List<Product> mockProductList;
    @Mock List<ProductInfo> mockProductInfo;

    private ProductService productService;

    @BeforeEach void setUp(){
        this.productService = new ProductService(this.productRepository);
    }

    @Test void getAllProducts(){
        productService.findAll();
        verify(productRepository).findAll();
    }

    @Test void whenIdExists_findByIdReturnsProduct(){
        // findById only returns an Optional<Product>, so we have to wrap mockProduct in Optional.ofNullable
        Mockito.when(productRepository.findById(1)).thenReturn(Optional.ofNullable(mockProduct));
        // save the Optional<Product> returned from productService.findById()
        Optional<Product> returnedProduct = productService.findById(1);
        // .get() returns the value from the optional, in this case we want it to be our mockProduct.
        assertTrue(returnedProduct.get().equals(mockProduct));
    }

    @Test void whenIdDoesNotExist_findByIdReturnsEmpty(){
        Mockito.when(productRepository.findById(1)).thenReturn(Optional.empty());
        Optional<Product> returnedProduct = productService.findById(1);
        assertTrue(!returnedProduct.isPresent());
    }

    @Test void saveProduct(){
        productService.save(mockProduct);
        verify(productRepository).save(mockProduct);
    }

    @Test void saveAllProducts(){
        productService.saveAll(mockProductList, mockProductInfo);
        verify(productRepository).saveAll(mockProductList);
    }

    @Test void deleteProduct(){
        Mockito.when(productRepository.existsById(1)).thenReturn(true);
        productService.delete(1);
        verify(productRepository).deleteById(1);
    }
    @Test void whenProductDoesNotExist_doNotDeleteProduct(){
        //Mockito.when(mockProduct.getId()).thenReturn(1);
        Mockito.when(productRepository.existsById(1)).thenReturn(false);
        productService.delete(1);
        verify(productRepository, never()).deleteById(1);
    }


    @Test void getFeaturedProducts(){
        productService.getFeaturedProducts();
        verify(productRepository).getFeaturedProducts();
    }
    @Test void getProductsOnSale(){
        productService.getProductsOnSale();
        verify(productRepository).getProductsOnSale();
    }

    @Test void getProductsOverZero(){
        productService.getProductsOverZero();
        verify(productRepository).getProductsOverZero();
    }

}