package CarFleet.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name= "locations")
public class Locations {
	                                    /* PRIVATE ATTRIBUTE */
	@Id
	@Column(name = "id_location")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Column(name = "latitude_location")
	private BigDecimal latitude;
	@Column(name = "longitude_location")
	private BigDecimal longitude;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@Column(name = "date_location")
	private LocalDate date;
	@Column(name = "time_location")
	private LocalTime time;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_car", referencedColumnName = "id")
	private Cars car;
	
	                                     /* PUBLIC ATTRIBUTE */
	
	                                       /* CONSTRUCTORS */
	public Locations(){}
	
	public Locations(Long id, BigDecimal latitude, BigDecimal longitude, LocalDate date, LocalTime time, Cars car){
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.date = date;
		this.time = time;
		this.car = car;
	}
	
	                                          /* GETTERS */
	public Long getId() {
		return id;
	}
	
	public BigDecimal getLatitude() {
		return latitude;
	}
	
	public BigDecimal getLongitude() {
		return longitude;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public LocalTime getTime() {
		return time;
	}
	
	public Cars getCar() {
        return car;
    }
	
	                                          /* SETTERS */
	
	public Boolean setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
		return true;
	}
	
	public Boolean setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
		return true;
	}
	
	public Boolean setDate(LocalDate date) {
		if(date.isAfter(LocalDate.now())) {
			return false;
		}
		else {
			this.date = date;
			return true;
		}
	}
	
	public Boolean setTime(LocalTime time) {
		if(time.isAfter(LocalTime.now())) {
			return false;
		}
		else {
			this.time = time;
			return true;
		}
	}
	
	public Boolean setCar(Cars car) {
		if(car.getNameCar().isBlank()) {
			return false;
		}
		else {
			this.car = car;
			return true;
		}
	}
}
