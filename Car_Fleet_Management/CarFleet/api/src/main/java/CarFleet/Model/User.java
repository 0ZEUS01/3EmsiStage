package CarFleet.Model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name= "users")
public class User {
	                                      /* PRIVATE ATTRIBUTE */
	@Id
	@Column(name = "id_user")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Column(name = "fname_user")
	private String fname;
	@Column(name = "lname_user")
	private String lname;
	// GENDER NEED TO BE ADDED !!!
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@Column(name = "birthdate_user")
	private LocalDate birthdate;
	@Column(name = "username_user")
	private String username;
	@Column(name = "email_user")
	private String email;
	@Column(name = "password_user")
	private String password;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "nationality_user", referencedColumnName = "id")
	private Nationality nationality;
	@Column(name = "isDeleted")
	private Boolean isDeleted;
	
	                                       /* PUBLIC ATTRIBUTE */
	
                                             /* CONSTRUCTORS */
	public User(){}
	
	public User(Long id, String fname, String lname, LocalDate birthdate, String username, String email, String password, Nationality nationality, Boolean isDeleted){
		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.birthdate = birthdate;
		this.username = username;
		this.email = email;
		this.password = password;
		this.nationality = nationality;
		this.isDeleted = isDeleted;
	}
	
	                                           /* GETTERS */
	public Long getId() {
		return id;
	}
	
	public String getFname() {
		return fname;
	}
	
	public String getLname() {
		return lname;
	}
	
	public LocalDate getBirthdate() {
		return birthdate;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password; // WE NEED TO HIDE THE PASSWORD SHOULD NOT BE DISPLAYED !!!
	}
	
	public Nationality getNationality() {
		return nationality;
	}
	
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	
                                              /* SETTERS */
	public Boolean setFname(String fname) {
		if(fname.isBlank()) {
			return false;
		}
		else {
			this.fname = fname;
			return true;
		}
	}
	
	public Boolean setLname(String lname) {
		if(lname.isBlank()) {
			return false;
		}
		else {
			this.lname = lname;
			return true;
		}
	}
	
	public Boolean setBirthdate(LocalDate birthdate) {
		if(birthdate.isEqual(LocalDate.now()) || birthdate.isAfter(LocalDate.now())) {
			return false;
		}
		else {
			this.birthdate = birthdate;
			return true;
		}
	}
	
	public Boolean setUsername(String username) {
		if(username.isBlank()) {
			return false;
		}
		else {
			this.username = username;
			return true;
		}
	}
	
	public Boolean setEmail(String email) {
		if(email.isBlank()) {
			return false;
		}
		else {
			this.email = email;
			return true;
		}
	}
	
	public Boolean setPassword(String password) {
		if(password.isBlank()) {
			return false;
		}
		else {
			this.password = password;  // WE NEED TO CRYPT (HASH) THE PASSWORD SHOULD NOT BE INSERTED LIKE THAT !!!
			return true;
		}
	}
	
	public Boolean setNationality(Nationality nationality) {
		if(nationality.getNationality().isBlank()) {
			return false;
		}
		else {
			this.nationality = nationality;
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

	                                         /* METHODS */
	public String ToString() {
		return id + " " + fname + " " + lname + " " + birthdate + " " + username + " " + email + " " + password + " " + nationality.getNationality() + " " + isDeleted;
	}
}
