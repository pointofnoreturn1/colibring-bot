package io.vaku;

import io.vaku.util.DateUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.format.datetime.DateFormatter;

import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public DateUtils dateValidator() {
        return new DateUtils("dd.MM.yyyy");
    }
}
