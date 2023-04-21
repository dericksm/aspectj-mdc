package com.github.dericksm.aspectjmdc;

import com.github.dericksm.aspectjmdc.aspectj.Sensitive;
import java.io.Serializable;

public class ProductResponse implements Serializable {

    @Sensitive
    private Product product;

    public ProductResponse(Product product) {
        this.product = product;
    }

    public ProductResponse(){}

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
