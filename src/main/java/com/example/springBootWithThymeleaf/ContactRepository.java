package com.example.springBootWithThymeleaf;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContactRepository extends PagingAndSortingRepository<Contact,Long>, JpaSpecificationExecutor<Contact> {
    
}
