package com.co.flypass.payments.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.co.flypass.payments.bff.config")
public class PaymentsBffApplication {
  public static void main(String[] args) {
    SpringApplication.run(PaymentsBffApplication.class, args);
  }
}
