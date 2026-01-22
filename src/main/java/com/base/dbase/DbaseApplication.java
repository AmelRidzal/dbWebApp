package com.base.dbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DbaseApplication  {

    static void main(String[] args) {
		SpringApplication.run(DbaseApplication.class, args);
	}

}
