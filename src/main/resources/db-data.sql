use babel;

insert into Level(name,next_id,previous_id,description) values ('A1',2,null,' Debutant ');
insert into Level(name,next_id,previous_id,description) values ('A2',3,1,'moyen');
insert into Level(name,next_id,previous_id,description) values ('B1',4,2,'moyen');
insert into Level(name,next_id,previous_id,description) values ('B2',5,3,'expaire');
insert into Level(name,next_id,previous_id,description) values ('C1',6,4,'expaire');
insert into Level(name,next_id,previous_id,description) values ('C2',null,5,'expaire');


insert into EventType (name, stereotype) values ('atelier socio linguistique', 'WORKSHOP');

insert into FieldOfStudy(name) values ('IT engineering');
insert into FieldOfStudy(name) values ('Business');
insert into FieldOfStudy(name) values ('Economic');
insert into FieldOfStudy(name) values ('Droit');
insert into FieldOfStudy(name) values ('Mathématiques');
insert into FieldOfStudy(name) values ('Physique et chimie');


insert into OrganisationCategory(name, stereotype) values ('université', 'UNIVERSITY');
insert into OrganisationCategory(name, stereotype) values ('bibliothéque', 'LIBRARY');
insert into OrganisationCategory(name, stereotype) values ('association', 'NGO');

insert into Civility(name) values ( 'Mme' );
insert into Civility(name) values ( 'Mr' );

insert into Country(name ,isoCode) values ('France', 'FR');
insert into Country(name , isoCode) values ('Syrie' , 'SYR');
insert into Country(name , isoCode) values ('Afganistan' , 'Afg');
insert into Country(name , isoCode) values ('Germany' , 'Gr');
insert into Country(name , isoCode) values ('Iraque' , 'Irq');


insert into Language(name) values('Français');
insert into Language(name) values('Anglais');
insert into Language(name) values('Arabe');
insert into Language(name) values('Dari');


insert into Organisation(name, street1, country_id, postalCode, locality, contact, category_id) 
	values ('science po','27 Rue Saint-Guillaume',1,'75007', 'Paris', '{"name":"Elyse","phoneNumber":"00331234567","mailAddress":"Elyse@gmail.com"}', 1);
insert into Organisation(name, street1, country_id, postalCode, locality, contact, category_id) 
	values ('JRS','14 rue assas', 1, '75006', 'Paris','{"name":"Irinda","phoneNumber":"00337654321","mailAddress":"Irinda@gmail.com"}', 2);
insert into Organisation(name, street1, country_id, postalCode, locality, contact, category_id) 
	values ('Singa','8 boulvard Mazzeh', 1, '31555', 'Toulouse', '{"name":"Paul","phoneNumber":"00331234765","mailAddress":"paul@gmail.com"}', 3);
insert into Organisation(name, street1, country_id, postalCode, locality, contact, category_id) 
	values ('CPA','10 rue Damas', 1, '69123', 'Lyon', '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 1);

insert into Cursus(name, startDate, endDate, locality, country_id, organisation_id,level_id) 
	values('A1', now(), DATE_ADD(now(),INTERVAL 30 DAY), 'Paris', 1,1,1);
insert into Cursus(name, startDate, endDate, locality, country_id, organisation_id,level_id) 
	values('B1',now(), DATE_ADD(now(),INTERVAL 30 DAY), 'Paris', 1,4,3);
insert into Cursus(name, startDate, endDate, locality, country_id, organisation_id,level_id) 
	values('A2', now(), DATE_ADD(now(),INTERVAL 30 DAY), 'Paris', 1,3,2);
insert into Cursus(name, startDate, endDate, locality, country_id, organisation_id,level_id) 
	values('B2', now(), DATE_ADD(now(),INTERVAL 30 DAY), 'Paris', 1,2,4);
insert into Cursus(name, startDate, endDate, locality, country_id, organisation_id,level_id) 
	values('C1', now(), DATE_ADD(now(),INTERVAL 30 DAY), 'Paris', 1,3,5);
insert into Cursus(name, startDate, endDate, locality, country_id, organisation_id,level_id) 
	values('C2', now(), DATE_ADD(now(),INTERVAL 30 DAY), 'Paris', 1,2,6);

insert into Cursus_courses(Cursus_id, level_id, translatorRequired) values(1, 1, true );
insert into Cursus_courses(Cursus_id, level_id, translatorRequired) values(2, 1, false );

insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,0,1,2,1);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,2,1,2);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,0,3,3,3);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,4,2,4);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,5,2,2);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,1,6,2,1);

update Teaching t, Organisation o set t.contact=o.contact where t.organisation_id=o.id;


insert into Volunteer(firstname, lastname, birthdate, mailAddress, phoneNumber, accessKey, password, role, locality, civility_id, nationality_id, comments)
	values ('Abiir','ZATAR', date(now()), 'Alaric@gmail.com', '07908756','xyz','123456789','VOLUNTEER', 'bordeaux', 1, 1, 'working for JRS');
insert into Volunteer(firstname, lastname, birthdate, mailAddress, phoneNumber, accessKey, password, role, locality, civility_id, nationality_id, comments)
	values ('lucile','BALOU', date(now()), 'lucile@gmail.com', '07765432', 'xyz', '123456789', 'VOLUNTEER', 'Toulouse', 1, 1, 'working for JRS');
insert into Volunteer(firstname, lastname, birthdate, mailAddress, phoneNumber, accessKey, password, role, locality, civility_id, nationality_id, comments)
	values ('Nour', 'BADAN', date(now()), 'Nour@gmail.com', '07652436', 'xyz', '123456789', 'VOLUNTEER', 'Lyon', 2, 1, 'working for JRS');
insert into Volunteer(firstname, lastname, birthdate, mailAddress, phoneNumber, accessKey, password, role, locality, civility_id, nationality_id, comments)
	values ('jawad', 'DODO', date(now()), 'jawad@gmail.com', '07765432', 'xyz', '123456789', 'VOLUNTEER', 'Paris', 2, 1, 'working for JRS');
insert into Volunteer(firstname, lastname, birthdate, mailAddress, phoneNumber, accessKey, password, role, locality, civility_id, nationality_id, comments)
	values ('ABD', 'BADAN', date(now()), 'Nour@gmail.com', '07765432', 'xyz', '123456789', 'VOLUNTEER', 'bordeaux ', 2, 1, 'working for JRS');
insert into Volunteer(firstname, lastname, birthdate, mailAddress, phoneNumber, accessKey, password, role, locality, civility_id, nationality_id, comments)
	values ('Alaric', 'COUCOU', date(now()), 'Nour@gmail.com', '07908756', 'xyz', '123456789', 'VOLUNTEER', 'Renne', 2, 1, 'working for JRS');

insert into Volunteer_Language(Volunteer_id, language_id) values(1,1);
insert into Volunteer_Language(Volunteer_id, language_id) values(1,2);
insert into Volunteer_FieldOfStudy(Volunteer_id, fieldOfStudy_id) values(1,2);
insert into Volunteer_Language(Volunteer_id, language_id) values(3,1);
insert into Volunteer_Language(Volunteer_id, language_id) values(4,2);
insert into Volunteer_FieldOfStudy(Volunteer_id, fieldOfStudy_id) values(5,2);
insert into Volunteer_Language(Volunteer_id, language_id) values(2,1);
insert into Volunteer_Language(Volunteer_id, language_id) values(2,2);
insert into Volunteer_FieldOfStudy(Volunteer_id, fieldOfStudy_id) values(1,2);


	
insert into Administrator(firstName,lastName,mailAddress,accessKey,role,phoneNumber,password,civility_id) 
	values('Alaric','Hermant','alaric_hermant@yahoo.fr','xyz','ADMIN','07123456','123456789',2);
insert into Administrator(firstName,lastName,mailAddress,accessKey,role,phoneNumber,password,civility_id) 
	values('Irinda','riquelme','irinda.r@gmail.com','xyz','ADMIN','07123456','123456789',1);

insert into Refugee (firstName,lastName,birthDate,mailAddress,phoneNumber,accessKey,password)
values ('Alaric', 'Hermant', NULL, 'az', NULL, 'R-a871ce00-e7d2-497e-8a4e-d272b8b5b520', 'f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9');

insert into AbstractEvent(street1, country_id, postalCode, subject,description,organisation_id ,type_id ,DTYPE)
values ('14 rue d''assas',1,'75006','Politique','  ',2,1,'O-E');

update AbstractEvent a, Organisation o set a.contact=o.contact where a.organisation_id=o.id;