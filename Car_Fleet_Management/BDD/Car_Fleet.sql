                                                      /*==============================================================*/
                                                      /*                    SQL Script :  Car_Fleet                   */
                                                      /*               Date and Time :  13/07/2023 19:55:30           */
													  /*             Created by:  [Mohamed Hakkou & Yahya Zini]       */
                                                      /*==============================================================*/


-- Drop the Car_Fleet database if it exists
DROP DATABASE IF EXISTS Car_Fleet
GO

-- Create the Car_Fleet database
CREATE DATABASE Car_Fleet
GO

-- Switch to the Car_Fleet database
USE Car_Fleet
GO


-- Create the 'cars' table
CREATE TABLE cars
(
	id_car BIGINT IDENTITY, -- PRIMARY KEY
	registration_plate_car NVARCHAR(200) UNIQUE NOT NULL, -- UNIQUE
	name_car NVARCHAR(MAX) NOT NULL,
	isDeleted BIT NOT NULL,
	CONSTRAINT PK_CARS PRIMARY KEY (id_car),
)
GO

-- Create the 'nationalities' table
CREATE TABLE nationalities
(
	id_nationality BIGINT IDENTITY,
	nationality NVARCHAR(MAX) NOT NULL,
	CONSTRAINT PK_NATIONALITIES PRIMARY KEY (id_nationality)
)
GO

-- Create the 'users' table
CREATE TABLE users
(
	id_user BIGINT IDENTITY, -- PRIMARY KEY
	fname_user NVARCHAR(MAX) NOT NULL,
	lname_user NVARCHAR(MAX) NOT NULL,
	birthdate_user DATE NOT NULL,
	username_user NVARCHAR(MAX) NOT NULL,
	email_user NVARCHAR(MAX) NOT NULL,
	password_user NVARCHAR(MAX) NOT NULL,
	nationality_user BIGINT NOT NULL, -- FOREIGN KEY
	isDeleted BIT NOT NULL,
	CONSTRAINT PK_USERS PRIMARY KEY (id_user),
	CONSTRAINT FK_USERS_NATIONALITIES FOREIGN KEY (nationality_user) REFERENCES nationalities(id_nationality)
)
GO

-- Create the 'locations' table
CREATE TABLE locations
(
	id_location BIGINT IDENTITY,
	latitude_location NVARCHAR(MAX) NOT NULL,
	longitude_location NVARCHAR(MAX) NOT NULL,
	date_location DATE NOT NULL,
	time_location TIME NOT NULL,
	id_car BIGINT NOT NULL, -- FOREIGN KEY
	CONSTRAINT PK_LOCATIONS PRIMARY KEY (id_location),
	CONSTRAINT FK_LOCATIONS_CARS FOREIGN KEY (id_car) REFERENCES cars(id_car)
)
GO

-- Create the 'locations_history' table
CREATE TABLE locations_history(
	id_history BIGINT IDENTITY,
	latitude_history NVARCHAR(MAX) NOT NULL,
	longitude_history NVARCHAR(MAX) NOT NULL,
	date_history DATE NOT NULL,
	time_history TIME NOT NULL,
	id_car BIGINT NOT NULL, -- FOREIGN KEY
	CONSTRAINT PK_LOCATIONS_HISTORY PRIMARY KEY (id_history),
	CONSTRAINT FK_LOCATIONS_HISTORY_CARS FOREIGN KEY (id_car) REFERENCES cars(id_car)
)
GO

-- Create a Trigger 'Trg_LocationUpdates' to capture location updates
CREATE OR ALTER TRIGGER Trg_LocationUpdates 
ON locations 
AFTER UPDATE
AS
BEGIN
    DECLARE @old_latitude NVARCHAR(MAX), @old_longitude NVARCHAR(MAX), @old_date DATE, @old_time TIME, @old_id_car BIGINT;

    SELECT @old_latitude = latitude_location,
           @old_longitude = longitude_location,
           @old_date = date_location,
           @old_time = time_location,
           @old_id_car = id_car
    FROM DELETED;

    INSERT INTO locations_history (latitude_history, longitude_history, date_history, time_history, id_car)
    VALUES (@old_latitude, @old_longitude, @old_date, @old_time, @old_id_car);
END;
GO

-- Inserting Mohamed Hakkou informations
INSERT INTO users(fname_user, lname_user, birthdate_user, username_user, email_user, password_user, nationality_user, isDeleted) VALUES('Mohamed', 'Hakkou', '2001-11-23', 'hakkoumohamed23', 'hakkoumohamed23@gmail.com', '$2a$10$oTEnERBhpwaq/t0rAfR4ceTvVqel/X4Ev0uSEAXArDpcYnGsu/.oe', 149, 'False')
GO

-- Inserting Yahya Zini informations
INSERT INTO users(fname_user, lname_user, birthdate_user, username_user, email_user, password_user, nationality_user, isDeleted) VALUES('Yahya', 'Zini', '2001-10-10', 'zeus', 'zini.yahya@emsi-edu.ma', '$2a$10$9tuVvqLFii8MYCG4NnUSzujD8qRf13d0Ks89KZUs/WNH4f5Pw57EK', 149, 'False')
GO

-- Inserting a list of nationalities into the 'nationalities' table
INSERT INTO nationalities (nationality)
VALUES 
('Afghan'),
('Albanian'),
('Algerian'),
('American Samoan'),
('Andorran'),
('Angolan'),
('Anguillan'),
('Antarctic'),
('Antiguan and Barbudan'),
('Argentine'),
('Armenian'),
('Aruban'),
('Australian'),
('Austrian'),
('Azerbaijani'),
('Bahamian'),
('Bahraini'),
('Bangladeshi'),
('Barbadian'),
('Belarusian'),
('Belgian'),
('Belizean'),
('Beninese'),
('Bermudian'),
('Bhutanese'),
('Bolivian'),
('Bonaire, Sint Eustatius and Saba'),
('Bosnian and Herzegovinian'),
('Motswana'),
('Bouvet Island'),
('Brazilian'),
('British Indian Ocean Territory'),
('Bruneian'),
('Bulgarian'),
('Burkinab�'),
('Burundian'),
('Cabo Verdean'),
('Cambodian'),
('Cameroonian'),
('Canadian'),
('Caymanian'),
('Central African'),
('Chadian'),
('Chilean'),
('Chinese'),
('Christmas Island'),
('Cocos Islander'),
('Colombian'),
('Comoran'),
('Congolese'),
('Congolese'),
('Cook Islander'),
('Costa Rican'),
('Croatian'),
('Cuban'),
('Cura�aoan'),
('Cypriot'),
('Czech'),
('Ivorian'),
('Danish'),
('Djiboutian'),
('Dominican'),
('Dominican'),
('Ecuadorian'),
('Egyptian'),
('Salvadoran'),
('Equatorial Guinean'),
('Eritrean'),
('Estonian'),
('Eswatini'),
('Ethiopian'),
('Falkland Islander'),
('Faroese'),
('Fijian'),
('Finnish'),
('French'),
('French Guianese'),
('French Polynesian'),
('French Southern Territories'),
('Gabonese'),
('Gambian'),
('Georgian'),
('German'),
('Ghanaian'),
('Gibraltar'),
('Greek'),
('Greenlandic'),
('Grenadian'),
('Guadeloupean'),
('Guamanian'),
('Guatemalan'),
('Channel Islander'),
('Guinean'),
('Bissau-Guinean'),
('Guyanese'),
('Haitian'),
('Heard Island and McDonald Islands'),
('Vatican'),
('Honduran'),
('Hong Kong'),
('Hungarian'),
('Icelandic'),
('Indian'),
('Indonesian'),
('Iranian'),
('Iraqi'),
('Irish'),
('Manx'),
('Italian'),
('Jamaican'),
('Japanese'),
('Jersey'),
('Jordanian'),
('Kazakhstani'),
('Kenyan'),
('I-Kiribati'),
('North Korean'),
('South Korean'),
('Kuwaiti'),
('Kyrgyzstani'),
('Laotian'),
('Latvian'),
('Lebanese'),
('Mosotho'),
('Liberian'),
('Libyan'),
('Liechtensteiner'),
('Lithuanian'),
('Luxembourger'),
('Macanese'),
('Malagasy'),
('Malawian'),
('Malaysian'),
('Maldivian'),
('Malian'),
('Maltese'),
('Marshallese'),
('Martiniquais'),
('Mauritanian'),
('Mauritian'),
('Mahoran'),
('Mexican'),
('Micronesian'),
('Moldovan'),
('Mon�gasque'),
('Mongolian'),
('Montenegrin'),
('Montserratian'),
('Moroccan'),
('Mozambican'),
('Myanmarian'),
('Namibian'),
('Nauruan'),
('Nepali'),
('Dutch'),
('New Caledonian'),
('New Zealand'),
('Nicaraguan'),
('Nigerien'),
('Nigerian'),
('Niuean'),
('Norfolk Islander'),
('North Macedonian'),
('Northern Mariana Islander'),
('Norwegian'),
('Omani'),
('Pakistani'),
('Palauan'),
('Palestinian'),
('Panamanian'),
('Papua New Guinean'),
('Paraguayan'),
('Peruvian'),
('Filipino'),
('Pitcairn Islander'),
('Polish'),
('Portuguese'),
('Puerto Rican'),
('Qatari'),
('Romanian'),
('Russian'),
('Rwandan'),
('R�unionese'),
('Saint Barth�lemy Islander'),
('Saint Helenian'),
('Saint Kitts and Nevisian'),
('Saint Lucian'),
('Saint Martin Islander'),
('Saint Pierre and Miquelon'),
('Saint Vincentian'),
('Samoan'),
('San Marinese'),
('S�o Tom�an'),
('Saudi Arabian'),
('Senegalese'),
('Serbian'),
('Seychellois'),
('Sierra Leonean'),
('Singaporean'),
('Sint Maarten Islander'),
('Slovak'),
('Slovenian'),
('Solomon Islander'),
('Somali'),
('South African'),
('South Georgia Islander'),
('South Sudanese'),
('Spanish'),
('Sri Lankan'),
('Sudanese'),
('Surinamese'),
('Svalbard and Jan Mayen Islander'),
('Swedish'),
('Swiss'),
('Syrian'),
('Taiwanese'),
('Tajikistani'),
('Tanzanian'),
('Thai'),
('Timorese'),
('Togolese'),
('Tokelauan'),
('Tongan'),
('Trinidadian or Tobagonian'),
('Tunisian'),
('Turkish'),
('Turkmen'),
('Turks and Caicos Islander'),
('Tuvaluan'),
('Ugandan'),
('Ukrainian'),
('Emirati'),
('British'),
('American'),
('US Virgin Islander'),
('Uruguayan'),
('Uzbekistani'),
('Ni-Vanuatu'),
('Venezuelan'),
('Vietnamese'),
('Wallisian and Futunan'),
('Sahrawi'),
('Yemeni'),
('Zambian'),
('Zimbabwean');
GO
