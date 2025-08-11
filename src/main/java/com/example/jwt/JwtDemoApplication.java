package com.example.jwt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot JWT 演示应用主类
 * 
 * @author example
 * @since 2024-01-01
 */
@SpringBootApplication
@MapperScan("com.example.jwt.mapper")
public class JwtDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtDemoApplication.class, args);
        System.out.println("\n" +
                "   _____ _____  _____  _____ _   _  _____   ____   ____   ____  _______ \n" +
                "  / ____|  __ \\|  __ \\|_   _| \\ | |/ ____| |  _ \\ / __ \\ / __ \\|__   __|\n" +
                " | (___ | |__) | |__) | | | |  \\| | |  __  | |_) | |  | | |  | |  | |   \n" +
                "  \\___ \\|  ___/|  _  /  | | | . ` | | |_ | |  _ <| |  | | |  | |  | |   \n" +
                "  ____) | |    | | \\ \\ _| |_| |\\  | |__| | | |_) | |__| | |__| |  | |   \n" +
                " |_____/|_|    |_|  \\_\\_____|_| \\_|\\_____| |____/ \\____/ \\____/   |_|   \n" +
                "\n" +
                " :: JWT Demo Application ::                           (v1.0.0)\n" +
                " :: 访问地址: http://localhost:8080/api               \n" +
                " :: Swagger文档: http://localhost:8080/api/swagger-ui.html\n");
    }
}