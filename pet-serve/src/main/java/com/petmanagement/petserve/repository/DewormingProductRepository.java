package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.DewormingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DewormingProductRepository extends JpaRepository<DewormingProduct, Integer> {
}
