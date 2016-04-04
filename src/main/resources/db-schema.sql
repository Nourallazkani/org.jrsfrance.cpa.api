drop database if exists babel;

create database babel;

use babel;
/*
drop table if exists
	Country_Language, Student_Language, CursusRegistration, WorkshopRegistration,Volunteer_Language,
	Volunteer, Workshop, Course, Cursus,
    Organisation,
    Student,  
    Civility,
    Country,
    Level, Language;
*/

create table Country (
    id int AUTO_INCREMENT PRIMARY KEY,
    name varchar(50),
    isoCode varchar(3)
);
create table Language (
	id int AUTO_INCREMENT PRIMARY KEY,
    name varchar(50)
);
create table Civility (
	id int auto_increment primary key,
    name varchar (255)
);
create table Level (
    id int AUTO_INCREMENT PRIMARY KEY,
    name varchar(50),
    next_id int,
    previous_id int,
    description varchar(100)
);

create table OrganisationCategory(
	id int auto_increment primary key ,
	name varchar(50)
);

create table FieldOfStudy (
	id int auto_increment primary key,
	name varchar(100)
);


create table Organisation(
    id int not null auto_increment primary key,
    name varchar(255) null,
	password varchar(200),
    street1 varchar(255) null,
    street2 varchar(255) null,
    zipcode varchar(255) null,
    city varchar(255) null,
    accessKey varchar (255) null,
    role varchar (255) null,
    contact varchar(255),
	phoneNumber varchar(255),
	mailAddress varchar(255),
    category_id int not null,
    country_id int null,
    foreign key (country_id) references Country (id),
    foreign key (category_id) references OrganisationCategory(id)
);

create table Teaching (
	id int auto_increment primary key,
	licence bit,
	master bit,
	link varchar(255),
	fieldOfStudy_id int not null,
	languageLevelRequired_id int, 
	organisation_id int null,
	foreign key (organisation_id) references Organisation(id),
	foreign key (languageLevelRequired_id) references Level(id),
	foreign key (fieldOfStudy_id) references FieldOfStudy(id)
);

create table Cursus (
    id int AUTO_INCREMENT PRIMARY KEY,
    name varchar(50) NOT NULL,
    street1 varchar(50),
    street2 varchar(50),
    zipcode varchar(50),
    city varchar(50),
    country_id int,
    lat BIGint,
    lng BIGint,
    organisation_id int,
    level_id int,
    startDate datetime,
    endDate datetime,
    foreign key (country_id) references Country (id),
    foreign key (organisation_id) references Organisation(id),
    foreign key (level_id) references Level (id)
);

create table Cursus_courses (
	Cursus_id int not null,
    startDate datetime default now(),
	endDate datetime default now(),
    level_id  int not null,
    translatorRequired bool default false,
    foreign key (Cursus_id) references Cursus (id),
    foreign key (level_id) references Level (id)
);

create table Volunteer(
	id int auto_increment PRIMARY key,
	firstName varchar (255),
	lastName varchar(255),
	birthDate date,
	mailAddress varchar (255),
	phoneNumber varchar (50),
	accessKey varchar(255),
	password varchar (255),
	role varchar (255),
	civility_id int,
	nationality_id int,
    comments varchar(1000),
	foreign key (civility_id) references Civility (id),
	foreign key (nationality_id) references Country (id)
);

create table Volunteer_Language(
	Volunteer_id int not null,
    language_id int not null,
    foreign key (Volunteer_id) references Volunteer(id),
    foreign key (language_id) references Language(id)
);

create table Administrator (
	id int auto_increment primary key ,
	firstName varchar(255),
	lastName varchar(255),
	mailAddress varchar(255),
	phoneNumber varchar(255),
	password varchar(255),
	accessKey varchar(255),
	role varchar(255),
	civility_id int ,
	foreign key (civility_id) references Civility(id)
);

/*
CREATE TABLE Student (
    id int AUTO_INCREMENT PRIMARY KEY,
    firstname varchar(255),
    lastname varchar(255),
    birthdate DATE,
    mail varchar(255),
    tel int,
    password varchar(255),
    civility_id int,
    level_id int,
    nationality_id int,
    foreign key (civility_id) references civility (id),
    foreign key (level_id) references level (id),
    foreign key (nationality_id) references country (id)
);
create table Student_Language(
	student_id int not null,
    language_id int not null,
    level_id int,
    foreign key (student_id) references Student (id),
    foreign key (language_id) references language(id),
    foreign key (level_id) references level(id)
);
create table Country_Language(
	country_id int not null,
    language_id int not null,
    foreign key (country_id) references country (id),
    foreign key (language_id) references language(id)
);
create table CursusRegistration (
	id int auto_increment primary key,
    registrationDate datetime default now(),
    studentConfirmationDate datetime,
    organisationCofirmationDate datetime,
    accepted bool,
    refusalReason varchar(255),
    cursus_id int not null,
    student_id int not null,
    foreign key (cursus_id) references Cursus (id),
    foreign key (student_id) references Student(id)
);
create table WorkshopRegistration (
	id int auto_increment primary key,
    registrationDate datetime default now(),
    studentConfirmationDate datetime,
    organisationCofirmationDate datetime,
    accepted bool,
    refusalReason varchar(255),
    workshop_id int not null,
    student_id int not null,
    foreign key (workshop_id) references Workshop (id),
    foreign key (student_id) references Student(id)
);





*/