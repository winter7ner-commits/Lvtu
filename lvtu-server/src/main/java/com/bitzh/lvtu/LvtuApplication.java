package com.bitzh.lvtu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.bitzh.lvtu.mapper")
@SpringBootApplication
public class LvtuApplication {

	public static void main(String[] args) {
		SpringApplication.run(LvtuApplication.class, args);
	}

}
