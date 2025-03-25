package org.ha.ckh637;

import org.ha.ckh637.service.AppIniService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        AppIniService.readJsonConfigFile(args);
        SpringApplication.run(Main.class, args);
//        System.out.println("Hello, World!");
    }
}