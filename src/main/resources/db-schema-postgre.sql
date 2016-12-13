create table Country (
    id serial primary KEY,
    name varchar(50),
    name_i18n jsonb null,
    isoCode varchar(3)
);

create table Language (
	id serial primary KEY,
    name varchar(50),
	name_i18n jsonb null,
);

create table Civility (
	id serial primary key,
    name varchar (50),
    name_i18n jsonb null
);

create table Level (
    id serial primary KEY,
    name varchar(50),
	name_i18n jsonb null,
    next_id int,
    previous_id int,
    description varchar(100)
);

create table FieldOfStudy (
	id serial primary key,
	name varchar(50),
	name_i18n jsonb null
);

create table OrganisationCategory(
	id serial primary key ,
	name varchar(50),
	name_i18n jsonb null,
	stereotype varchar(25),
	additionalInformations varchar(255)
);

create table Organisation(
    id serial primary key,
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
	additionalInformations varchar(500),
    category_id int not null,
    country_id int null,
    foreign key (country_id) references Country (id),
    foreign key (category_id) references OrganisationCategory(id)
);

create table Teaching (
	id serial primary key,
	licence boolean,
	master boolean,
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
	id serial primary key,
	name varchar(250) null,
	name_i18n jsonb null
);

create table LanguageLearningProgramType(
	id serial primary key,
	name varchar(250) null,
	name_i18n jsonb null
);

create table AbstractLearningProgram (
    id serial primary key,
    name varchar(250) null,
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
    groupSize int,
	domain_id int,	/* only if DTYPE = P (ProfessionalLearningProgram)  */
	type_id int,	/* only if DTYPE = L (LanguageLearningProgram) */
	DTYPE varchar(1) not null,
    foreign key (country_id) references Country (id),
    foreign key (organisation_id) references Organisation(id),
    foreign key (level_id) references Level (id),
	foreign key (type_id) references ProfessionalLearningProgramDomain(id),
	foreign key (domain_id) references LanguageLearningProgramType(id)
);

create table Volunteer(
	id serial primary key,
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
	Volunteer_id int not null,
    Language_id int not null,
    foreign key (Volunteer_id) references Volunteer(id),
    foreign key (Language_id) references Language(id)
);

create table Volunteer_FieldOfStudy (
	Volunteer_id int not null,
	FieldOfStudy_id int not null,
	foreign key (Volunteer_id) references Volunteer(id),
	foreign key (FieldOfStudy_id) references FieldOfStudy(id)
);
create table Administrator (
	id serial primary key ,
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
	id serial primary key,
	name varchar(50),
	name_i18n jsonb null,
	stereotype varchar(25)
);

create table AbstractEvent (
	id serial primary key ,
	audience varchar(15) not null,
    street1 varchar(255) null,
    street2 varchar(255) null,
    postalCode varchar(255) null,
    locality varchar(255) null,
    lat decimal(10, 8) null, 
    lng DECIMAL(11, 8) null,
    googleMapId varchar(255) null,
    startDate timestamp default now(),
	endDate timestamp default now(),
	contact varchar(512),
    registrationOpeningDate date,
    registrationClosingDate date,
    registrationStartDate date,
    subject varchar(255) null,
	subjectI18n json null,	
	link varchar(255) null,
    description varchar(255) null,
	descriptionI18n json null,
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

create table Refugee(
	id serial primary key,
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
    hostCountryLanguageLevel_id int null,
    fieldOfStudy_id int null,
    civility_id int,
	nationality_id int,
    foreign key (firstLanguage_id) references Language(id),
    foreign key (country_id) references Country(id),
    foreign key (civility_id) references Civility (id),
	foreign key (nationality_id) references Country (id),
	foreign key (hostCountryLanguageLevel_id) references Level(id)
);

create table Refugee_Language(
	Refugee_id int not null,
    Language_id int not null,
    foreign key (Refugee_id) references Refugee(id),
    foreign key (Language_id) references Language(id)
);

create table MeetingRequest(
	id serial PRIMARY key,
	dateConstraint varchar(255) null,
    reason varchar(20),
    firstContact varchar(25),
	additionalInformations varchar (500),
    postDate timestamp not null default now(),
    acceptationDate timestamp null,
    confirmationDate  timestamp null,
	street1 varchar(255),
    street2 varchar(255),
    postalCode varchar(255),
    locality varchar(255),
    lat decimal(10, 8) null, 
    lng DECIMAL(11, 8) null,
    googleMapId varchar(255) null,
   	refugee_id int not null,
   	volunteer_id int null,
    country_id int,
    foreign key (refugee_id) references Refugee(id),
    foreign key (volunteer_id) references Volunteer(id)
);

create table MeetingRequest_Volunteer(
	MeetingRequest_id int not null,
	Volunteer_id int not null,
	foreign key (MeetingRequest_id) references MeetingRequest(id),
    foreign key (Volunteer_id) references Volunteer(id)
);

create table MeetingRequest_messages (
	MeetingRequest_id int not null,
    volunteer_id int,
	text varchar(500),
    direction varchar(25),
	postedDate timestamp NOT null DEFAULT NOW(),
	readDate timestamp,
	foreign key (volunteer_id) references Volunteer(id),
	foreign key (MeetingRequest_id) references MeetingRequest(id)
);

create table AbstractLearningProgram_registrations(
	AbstractLearningProgram_id int not null,
   	refugee_id int not null,
    registrationDate timestamp default now(),
 	accepted boolean null,
    foreign key (refugee_id) references Refugee(id),
    foreign key (AbstractLearningProgram_id) references AbstractLearningProgram(id)
);

create table AbstractEvent_registrations(
	AbstractEvent_id int not null,
    refugee_id int not null,
    registrationDate timestamp default now(),
    accepted boolean null,
    foreign key (refugee_id) references Refugee(id),
    foreign key (AbstractEvent_id) references AbstractEvent(id)
);

create table Teaching_registrations(
	Teaching_id int not null,
    refugee_id int not null,
    registrationDate timestamp default now(),
    accepted boolean null,
    foreign key (refugee_id) references Refugee(id),
    foreign key (Teaching_id) references Teaching(id)
);