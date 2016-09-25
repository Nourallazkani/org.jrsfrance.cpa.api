drop database if exists cpa;

create database cpa 
	DEFAULT CHARACTER SET utf8
	DEFAULT COLLATE utf8_general_ci;

use cpa;
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
    name varchar (50)
);
create table Level (
    id int AUTO_INCREMENT PRIMARY KEY,
    name varchar(50),
    next_id int,
    previous_id int,
    description varchar(100)
);



create table FieldOfStudy (
	id int auto_increment primary key,
	name varchar(50)
);

create table OrganisationCategory(
	id int auto_increment primary key ,
	name varchar(50),
	stereotype varchar(25)
);

create table Organisation(
    id int not null auto_increment primary key,
    name varchar(255) null,
	password varchar(200),
    street1 varchar(255) null,
    street2 varchar(255) null,
    postalCode varchar(255) null,
    locality varchar(255) null,
    lat decimal(10, 8) null, 
    lng DECIMAL(11, 8) null,
    googleMapId varchar(255) null,
    accessKey varchar (255) null,
    contact varchar(512),
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
	contact varchar(512),
    registrationOpeningDate date,
    registrationClosingDate date,
    fieldOfStudy_id int not null,
	languageLevelRequired_id int, 
	organisation_id int null,
	foreign key (organisation_id) references Organisation(id),
	foreign key (languageLevelRequired_id) references Level(id),
	foreign key (fieldOfStudy_id) references FieldOfStudy(id)
);

create table ProfessionalLearningProgramDomain(
	id int AUTO_INCREMENT PRIMARY KEY,
	name varchar(250) NULL
);

create table LanguageLearningProgramType(
	id int AUTO_INCREMENT PRIMARY KEY,
	name varchar(250) NULL
);

create table AbstractLearningProgram (
    id int AUTO_INCREMENT PRIMARY KEY,
    name varchar(250) NULL,
    street1 varchar(50),
    street2 varchar(50),
    postalCode varchar(50),
    locality varchar(50),
    lat decimal(10, 8) null, 
    lng decimal(11, 8) null,
    googleMapId varchar(255) null,
    country_id int,
    registrationOpeningDate date,
    registrationClosingDate date,
    contact varchar(512),
    link varchar(512),
    organisation_id int,
    level_id int,
    startDate date,
    endDate date,
	domain_id int,	/* only if DTYPE = P (ProfessionalLearningProgram)  */
	type_id int,	/* only if DTYPE = L (LanguageLearningProgram) */
	DTYPE varchar(1) not NULL,
    foreign key (country_id) references Country (id),
    foreign key (organisation_id) references Organisation(id),
    foreign key (level_id) references Level (id),
	foreign key (type_id) references ProfessionalLearningProgramDomain(id),
	foreign key (domain_id) references LanguageLearningProgramType(id)
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
    street1 varchar(255),
    street2 varchar(255),
    postalCode varchar(255),
    locality varchar(255),
    lat decimal(10, 8) null, 
    lng DECIMAL(11, 8) null,
    googleMapId varchar(255) null,
    availableForConversation boolean null,
	availableForInterpreting boolean null,
	availableForSupportInStudies boolean null,
	availableForActivities boolean null,
	activities varchar(255),
    civility_id int,
    country_id int,
	nationality_id int,
	organisation_id int,
    foreign key (country_id) references Country(id),
	foreign key (civility_id) references Civility (id),
	foreign key (nationality_id) references Country (id),
	foreign key (organisation_id) references Organisation (id)
);


create table Volunteer_Language(
	volunteer_id int not null,
    language_id int not null,
    foreign key (volunteer_id) references Volunteer(id),
    foreign key (language_id) references Language(id)
);

create table Volunteer_FieldOfStudy (
	volunteer_id int not null,
	fieldOfStudy_id int not null,
	foreign key (volunteer_id) references Volunteer(id),
	foreign key (fieldOfStudy_id) references FieldOfStudy(id)
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

create table EventType (
	id int not null auto_increment primary key,
	name varchar(50),
	stereotype varchar(25)
);

create table AbstractEvent (
	id int auto_increment PRIMARY key ,
	audience varchar(15) not null,
    street1 varchar(255) null,
    street2 varchar(255) null,
    postalCode varchar(255) null,
    locality varchar(255) null,
    lat decimal(10, 8) null, 
    lng DECIMAL(11, 8) null,
    googleMapId varchar(255) null,
    startDate datetime default now(),
	endDate datetime default now(),
	contact varchar(512),
    registrationOpeningDate date,
    registrationClosingDate date,
    registrationStartDate date,
	subject_defaultText varchar(255) null,
	subject_textEn varchar(255) null,
	subject_textAr varchar(255) null,
	subject_textPrs varchar(255) null,
	link varchar(255) null,
	description_defaultText varchar(255) null,
	description_textEn varchar(255) null,
	description_textAr varchar(255) null,
	description_textPrs varchar(255) null,
    country_id int /*not*/ null,
    type_id int null,
    organisation_id int null,
    volunteer_id int null,
    DTYPE varchar(4),
    foreign key (type_id) References EventType(id),
    foreign key (volunteer_id) References Volunteer(id),
    foreign key (organisation_id) References Organisation(id),
    foreign key (country_id) References Country(id)
);
alter table AbstractEvent modify column description_defaultText varchar(8000);
alter table AbstractEvent modify column description_defaultText varchar(8000);
alter table AbstractEvent modify column description_defaultText varchar(8000);
alter table AbstractEvent modify column description_defaultText varchar(8000);

create table Refugee(
	id int auto_increment primary key,
    firstName varchar (255),
	lastName varchar(255),
	birthDate date,
	mailAddress varchar (255),
	phoneNumber varchar (50),    
	accessKey varchar(255),
	password varchar (255),
    
    street1 varchar(255),
    street2 varchar(255),
    postalCode varchar(255),
    locality varchar(255),
    lat decimal(10, 8) null, 
    lng DECIMAL(11, 8) null,
    googleMapId varchar(255) null,
    country_id int,
    
    firstLanguage_id int null,
    fieldOfStudy_id int null,
    civility_id int,
	nationality_id int,
    foreign key (firstLanguage_id) references Language(id),
    foreign key (country_id) references Country(id),
	foreign key (civility_id) references Civility (id),
	foreign key (nationality_id) references Country (id)
);

create table Refugee_Language(
	refugee_id int not null,
    language_id int not null,
    foreign key (Refugee_id) references Refugee(id),
    foreign key (language_id) references Language(id)
);

create table MeetingRequest(
	id int PRIMARY key auto_increment,
	startDate date null,
	endDate date null,
    reason varchar(20),
	additionalInformations varchar (500),
    postDate datetime not null default now(),
    acceptedDate datetime null,
	street1 varchar(255),
    street2 varchar(255),
    postalCode varchar(255),
    locality varchar(255),
    lat decimal(10, 8) null, 
    lng DECIMAL(11, 8) null,
    matchesCount int not null,
    googleMapId varchar(255) null,
   	refugee_id int not null,
   	volunteer_id int null,
    country_id int,
    foreign key (Refugee_id) references Refugee(id),
    foreign key (Volunteer_id) references Volunteer(id)
);