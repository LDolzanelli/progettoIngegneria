package it.unibs.ingsw.destinazioni;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DestinazioniApplication {
    public static void main(String[] args) {
        SpringApplication.run(DestinazioniApplication.class, args);
    }
}
