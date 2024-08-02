package com.labs.desafio.model;

import java.math.BigDecimal;

public class Product {
    private Integer productId;
    private BigDecimal value;

    public Product(Integer productId, BigDecimal value) {
        this.productId = productId;
        this.value = value;
    }
    
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
