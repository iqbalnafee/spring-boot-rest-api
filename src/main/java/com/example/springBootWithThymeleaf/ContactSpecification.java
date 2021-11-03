package com.example.springBootWithThymeleaf;

//To specify the conditions of the invoked database query, we need to create
//        a new implementation of Specification<T>: ContactSpecification class.


import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;

public class ContactSpecification implements Specification<Contact> {

    private Contact filter;

    public ContactSpecification(Contact filter) {
        super();
        this.filter = filter;
    }
    @Override
    public Predicate toPredicate(Root<Contact> root, CriteriaQuery<?> cq,
                                 CriteriaBuilder cb) {

        Predicate p = cb.disjunction();

        if (filter.getName() != null) {
            p.getExpressions().add(cb.like(root.get("name"), "%" + filter.getName() + "%"));
        }

        if (filter.getPhone()!= null) {
            p.getExpressions().add(cb.like(root.get("phone"), "%" + filter.getPhone() + "%"));
        }

        return p;
    }


}
