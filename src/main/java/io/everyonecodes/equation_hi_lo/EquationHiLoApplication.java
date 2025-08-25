package io.everyonecodes.equation_hi_lo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EquationHiLoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EquationHiLoApplication.class, args);

    }


}
