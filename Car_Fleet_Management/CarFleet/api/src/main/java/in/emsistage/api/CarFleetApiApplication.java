package in.emsistage.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"CarFleet.Controller", "CarFleet.Service"})
public class CarFleetApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarFleetApiApplication.class, args);
	}
}
	