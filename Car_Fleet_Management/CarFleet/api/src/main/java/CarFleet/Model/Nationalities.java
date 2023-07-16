package CarFleet.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name= "nationalities")
public class Nationalities {
	                                       /* PRIVATE ATTRIBUTE */
	@Id
	@Column(name = "id_nationality")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Column(name = "nationality")
	private String nationality;
	
	                                       /* PUBLIC ATTRIBUTE */
	
	                                        /* CONSTRUCTORS */
	
	public Nationalities(){}
	
	public Nationalities(Long id, String nationality){
		this.id = id;
		this.nationality = nationality; 
	}
	
	
	                                          /* GETTERS */
	public Long getId() {
		return id;
	}
	
	public String getNationality() {
		return nationality;
	}
	
	                                          /* SETTERS */
	
	public Boolean setNationality(String nationality) {
		if(nationality.isBlank()) {
			return false;
		}
		else {
			this.nationality = nationality;
			return true;
		}
	}
	
}
