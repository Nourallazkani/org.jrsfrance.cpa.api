drop table if exists
/* deleting the links tables */ 
	Country_Language, Student_Language, CursusRegistration, WorkshopRegistration,Volunteer_Language,
	Volunteer, Workshop, Course, Cursus,
    Organisation,
    Student,  
    Civility,
    Country,
    Level, Language;
    
create table Volunteer (
id INT auto_increment Primary key,
name varchar (50),
lastName varchar(50),
civility_id int,
helpDescription varchar (500),
Coordinate varchar(100)
);
    
create table Country (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    isoCode varchar(3)
);
create table Language (
	id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50)
);
create table Civility (
	id int auto_increment primary key,
    name varchar (255)
);
create table Level (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    next_id INT,
    previous_id INT,
    description VARCHAR(100)
);
create table Organisation(
    id int not null auto_increment primary key,
    name varchar(255) null,
	password varchar(200),
    street1 varchar(255) null,
    street2 varchar(255) null,
    zipcode varchar(255) null,
    city varchar(255) null,
    country_id int null,
    foreign key (country_id) references Country (id)
);
create table Workshop(
    id int not null auto_increment primary key,
	acceptanceCriteria JSON,
	maxRegisrtations int not null default 10,
    startDate datetime default now(),
    endDate datetime default now(),
    name VARCHAR(50) NOT NULL,
    street1 VARCHAR(50),
    street2 VARCHAR(50),
    zipcode VARCHAR(50),
    city VARCHAR(50),
    country_id INT,
    lat BIGINT,
    lng BIGINT,
    organisation_id INT,
    translatorRiquierd bool default true,
    description varchar(255),
    foreign key (country_id) references Country (id),
    foreign key (organisation_id) references Organisation ( id )
);
create table Cursus (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    street1 VARCHAR(50),
    street2 VARCHAR(50),
    zipcode VARCHAR(50),
    city VARCHAR(50),
    country_id INT,
    lat BIGINT,
    lng BIGINT,
    organisation_id INT,
    level_id int,
    foreign key (country_id) references Country (id),
    foreign key (organisation_id) references Organisation(id),
    foreign key (level_id) references level (id)
);
create table Course (
	id int auto_increment primary key,
    name varchar (255),
    startDate datetime default now(),
	endDate datetime default now(),
	street1 varchar(255) null,
    street2 varchar(255) null,
    zipcode varchar(255) null,
    city varchar(255) null,
    country_id int,
    cursus_id int,
    translatorRequired bool default false,
    foreign key (country_id) references Country (id),
    foreign key (cursus_id) references Cursus (id)
);
create table Student(
 id int auto_increment PRIMARY key,
 firstname varchar (255),
 lastname varchar(255),
 birthdate date,
 mail varchar (255),
 tel int,
 password varchar (255),
 civility_id int,
 level_id int,
 nationality_id int,
 foreign key (civility_id) references civility (id),
  foreign key (level_id) references level(id),
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
create table Volunteer(
	id int auto_increment PRIMARY key,
	firstname varchar (255),
	lastname varchar(255),
	birthdate date,
	mail varchar (255),
	tel int,
	password varchar (255),
	civility_id int,
	nationality_id int,
    comments varchar(1000),
	foreign key (civility_id) references civility (id),
	foreign key (nationality_id) references country (id)
);
create table Volunteer_Language(
	volunteer_id int not null,
    language_id int not null,
    foreign key (Volunteer_id) references Volunteer(id),
    foreign key (language_id) references language(id)
);

create table OrganisationCategory(
id Int auto_increment primary key ,
name varchar(50)
);

alter table organisation drop column category_id;
alter table organisation add  category_id INT null after city;
alter table organisation add foreign key (category_id) references organisationcategory(id);
alter table organisation drop column password;
alter table organisation add password varchar(200) after name;

insert into organisationcategory(name) values ('université');
insert into organisationcategory(name) values ('bibliothéque');
insert into organisationcategory(name) values ('assosiation');

update organisation set category_id = 1;
alter table organisation modify column  category_id INT not null;



create table FieldOfStudy (
id INT auto_increment primary key,
name varchar(100)
);

create table Education (
id INT auto_increment primary key,
licence bit,
master bit,
link varchar(255),
fieldOfStudy_id INT not null,
languageLevelRequired_id INT, 
organisation_id INT null,
foreign key (organisation_id) references organisation(id),
foreign key (languageLevelRequired_id) references level(id),
foreign key (fieldOfStudy_id) references FieldOfStudy(id)
);


