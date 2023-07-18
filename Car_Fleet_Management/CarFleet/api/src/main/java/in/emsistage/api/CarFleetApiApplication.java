package in.emsistage.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan(basePackages = {"CarFleet"})
public class CarFleetApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarFleetApiApplication.class, args);
	}
}
