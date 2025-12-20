package com.ming.s2s;

import com.ming.s2s.common.utils.PrLogo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * S2S Application - SQL to SpringBoot Code Generator
 */
@SpringBootApplication
public class S2SApplication {

    public static void main(String[] args) {
        SpringApplication.run(S2SApplication.class, args);
        PrLogo.prLogo();
    }
}
