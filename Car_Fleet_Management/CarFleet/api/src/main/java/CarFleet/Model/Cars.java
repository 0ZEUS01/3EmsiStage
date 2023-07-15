package CarFleet.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name= "cars")
public class Cars {
	                                      /* PRIVATE ATTRIBUTE */
	@Id
	@Column(name = "id_car")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Column(name = "registration_plate_car")
	private String registration_plate;
	@Column(name = "name_car")
	private String name;
	@Column(name = "isDeleted")
	private Boolean isDeleted;
	
	                                      /* PUBLIC ATTRIBUTE */
	
	                                        /* CONSTRUCTORS */
	public Cars(){}
	
	public Cars(Long id, String registration_plate, String name, Boolean isDeleted){
		this.id = id;
		this.registration_plate= registration_plate;
		this.name = name;
		this.isDeleted = isDeleted;
	}
	
	                                          /* GETTERS */
	public Long getId() {
		return id;
	}
	
	public String getRegistrationPlate() {
		return registration_plate;
	}
	
	public String getNameCar() {
		return name;
	}
	
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	
	                                          /* SETTERS */
	public Boolean setRegistrationPlate(String registration_plate) {
		if(registration_plate.isBlank()) {
			return false;
		}
		else {
			this.registration_plate = registration_plate;
			return true;
		}
	}
	
	public Boolean setNameCar(String name) {
		if(name.isBlank()) {
			return false;
		}
		else {
			this.name = name;
			return true;
		}
	}
	
	public Boolean setIsDeleted(Boolean isDeleted) {
		if(this.isDeleted == isDeleted) {
			return false;
		}
		else {
			this.isDeleted = isDeleted;
			return true;
		}
	}
}
