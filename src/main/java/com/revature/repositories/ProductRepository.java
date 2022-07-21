package com.revature.repositories;

import com.revature.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("FROM Product WHERE featured = 'true' and quantity > 0")
    List<Product> getFeaturedProducts();

    @Query("FROM Product WHERE sale > 0 and quantity > 0")
    List<Product> getProductsOnSale();

    @Query("FROM Product WHERE quantity > 0")
    List<Product> getProductsOverZero();

    @Query("FROM Product WHERE id = :id")
    Optional<Product> findById(int id);
}
