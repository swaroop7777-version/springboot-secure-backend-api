package com.amigoscode.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix ="aws.s3.buckets")
public class S3Buckets {


    private String Customer;

    public String getCustomer() {
        return Customer;
    }
    public void setCustomer(String Customer) {
        this.Customer = Customer;
    }
}
