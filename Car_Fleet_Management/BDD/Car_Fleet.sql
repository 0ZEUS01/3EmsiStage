DROP DATABASE IF EXISTS Car_Fleet
GO
CREATE DATABASE Car_Fleet
GO
USE Car_Fleet
GO
-- CREATE TABLE Models
-- (
	-- id_model BIGINT IDENTITY,
	-- model NVARCHAR(MAX),
	-- CONSTRAINT PK_MODEL PRIMARY KEY (id_model)
-- )
-- GO
-- CREATE TABLE Companies
-- (
	-- id_company BIGINT IDENTITY,
	-- company NVARCHAR(MAX),
	-- CONSTRAINT PK_COMPANY PRIMARY KEY (id_company)
-- )
-- GO
-- CREATE TABLE Colors
-- (
	-- id_color BIGINT IDENTITY,
	-- color NVARCHAR(MAX),
	-- CONSTRAINT PK_COLORS PRIMARY KEY (id_color)
-- )
-- GO
-- CREATE TABLE Car_Types
-- (
	-- id_type BIGINT IDENTITY,
	-- type NVARCHAR(MAX),
	-- CONSTRAINT PK_CAR_TYPES PRIMARY KEY (type)
-- )
GO
CREATE TABLE cars
(
	id_car BIGINT IDENTITY, -- PRIMARY KEY
	registration_plate_car NVARCHAR(200) UNIQUE, -- UNIQUE
	-- password_car NVARCHAR(MAX),
	name_car NVARCHAR(MAX),
	-- model_car BIGINT, -- TABLE
	-- company_car BIGINT, -- TABLE
	-- color_car BIGINT, -- TABLE
	-- type_car BIGINT, -- TABLE
	isDeleted BIT,
	CONSTRAINT PK_CARS PRIMARY KEY (id_car),
)
GO
CREATE TABLE nationalities
(
	id_nationality BIGINT IDENTITY,
	nationality NVARCHAR(MAX),
	CONSTRAINT PK_NATIONALITIES PRIMARY KEY (id_nationality)
)
GO
CREATE TABLE users
(
	id_user BIGINT IDENTITY, -- PRIMARY KEY
	--picture_user VARBINARY(MAX),
	fname_user NVARCHAR(MAX),
	lname_user NVARCHAR(MAX),
	birthdate_user DATE,
	username_user NVARCHAR(MAX),
	email_user NVARCHAR(MAX),
	password_user NVARCHAR(MAX),
	nationality_user BIGINT, -- FOREIGN KEY
	-- isFounder BIT,
	isDeleted BIT,
	CONSTRAINT PK_USERS PRIMARY KEY (id_user),
	CONSTRAINT FK_USERS_NATIONALITIES FOREIGN KEY (nationality_user) REFERENCES nationalities(id_nationality)
)
GO
CREATE TABLE locations
(
	id_location BIGINT IDENTITY,
	latitude_location NVARCHAR(MAX),
	longitude_location NVARCHAR(MAX),
	date_location DATE,
	time_location TIME,
	id_car BIGINT, -- FOREIGN KEY
	CONSTRAINT PK_LOCATIONS PRIMARY KEY (id_location),
	CONSTRAINT FK_LOCATIONS_CARS FOREIGN KEY (id_car) REFERENCES cars(id_car)
)
GO
CREATE TABLE locations_history(
	id_history BIGINT IDENTITY,
	latitude_history NVARCHAR(MAX),
	longitude_history NVARCHAR(MAX),
	date_history DATE,
	time_history TIME,
	id_car BIGINT, -- FOREIGN KEY
	CONSTRAINT PK_LOCATIONS_HISTORY PRIMARY KEY (id_history),
	CONSTRAINT FK_LOCATIONS_HISTORY_CARS FOREIGN KEY (id_car) REFERENCES cars(id_car)
)
GO
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
