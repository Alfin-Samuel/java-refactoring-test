package com.sap.refactoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = "com.sap.refactoring.entity")
@SpringBootApplication
public class JavaRefactoringTestApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(JavaRefactoringTestApplication.class, args);
	}

}
