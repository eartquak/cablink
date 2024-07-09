package com.example.cablink;

import java.math.BigInteger;
import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class CablinkApplication {


	public static void main(String[] args) {
		SpringApplication.run(CablinkApplication.class, args);
	}


}
