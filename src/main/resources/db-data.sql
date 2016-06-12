use babel;

insert into Level(name,next_id,previous_id,description) values ('A1',2,null,' Debutant ');
insert into Level(name,next_id,previous_id,description) values ('A2',3,1,'moyen');
insert into Level(name,next_id,previous_id,description) values ('B1',4,2,'moyen');
insert into Level(name,next_id,previous_id,description) values ('B2',null,3,'expaire');

insert into EventType (name, stereotype) values ('atelier socio linguistique', 'WORKSHOP');

insert into FieldOfStudy(name) values ('IT engineering');
insert into FieldOfStudy(name) values ('business');
insert into FieldOfStudy(name) values ('economic');

insert into OrganisationCategory(name, stereotype) values ('université', 'UNIVERSITY');
insert into OrganisationCategory(name, stereotype) values ('bibliothéque', 'LIBRARY');
insert into OrganisationCategory(name, stereotype) values ('association', 'NGO');

insert into Civility(name) values ( 'Mme' );
insert into Civility(name) values ( 'Mr' );

insert into Country(name ,isoCode) values ('France', 'FR');
insert into Country(name , isoCode) values ('Syrie' , 'SYR');

insert into Language(name) values('Français');
insert into Language(name) values('Anglais');
insert into Language(name) values('Arabe');
insert into Language(name) values('Dari');


insert into Organisation(name, street1, country_id, zipcode, city, contact, category_id) 
	values ('science po','1 rue Pasteur',1,'75006', 'Paris', '{"name":"a","phoneNumber":"b","mailAddress":"c"}', 1);
insert into Organisation(name, street1, country_id, zipcode, city, contact, category_id) 
	values ('JRS','5 rue assas', 1, '75005', 'Paris','{"name":"e","phoneNumber":"f","mailAddress":"g"}', 2);
insert into Organisation(name, street1, country_id, zipcode, city, contact, category_id) 
	values ('SRS','8 boulvard Mazzeh', 2, '099', 'Toulouse', '{"name":"i","phoneNumber":"j","mailAddress":"k"}', 3);
insert into Organisation(name, street1, country_id, zipcode, city, contact, category_id) 
	values ('DamasLanguageCenter','10 rue Damas', 2, '099', 'Lyon', '{"name":"l","phoneNumber":"m","mailAddress":"n"}', 1);

insert into Cursus(name, startDate, endDate, city, country_id, organisation_id,level_id) 
	values('A1', now(), DATE_ADD(now(),INTERVAL 30 DAY), 'Paris', 1,1,1);
insert into Cursus(name, startDate, endDate, city, country_id, organisation_id,level_id) 
	values('B1',now(), DATE_ADD(now(),INTERVAL 30 DAY), 'Paris', 2,3,3);

insert into Cursus_courses(Cursus_id, level_id, translatorRequired) values(1, 1, true );
insert into Cursus_courses(Cursus_id, level_id, translatorRequired) values(2, 1, false );

insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,0,1,2,1);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,2,1,2);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,0,3,3,3);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,1,2,1);

update Teaching t, Organisation o set t.contact=o.contact where t.organisation_id=o.id;


insert into Volunteer(firstname, lastname, birthdate, mailAddress, phoneNumber, accessKey, password, role, city, civility_id, nationality_id, comments)
	values ('Nour','ALLAZKANI', date(now()), 'nourallazkani@gmail.com', '0782836691','xyz','123456789','VOLUNTEER', 'Paris', 2, 1, 'working for JRS');
insert into Volunteer(firstname, lastname, birthdate, mailAddress, phoneNumber, accessKey, password, role, city, civility_id, nationality_id, comments)
	values ('lucile','flo', date(now()), 'nourallazkani@gmail.com', '0782836691', 'xyz', '123456789', 'VOLUNTEER', 'Paris', 1, 1, 'working for JRS');
insert into Volunteer(firstname, lastname, birthdate, mailAddress, phoneNumber, accessKey, password, role, city, civility_id, nationality_id, comments)
	values ('jawad', 'dsa', date(now()), 'nourallazkani@gmail.com', '0782836691', 'xyz', '123456789', 'VOLUNTEER', 'Lyon', 2, 1, 'working for JRS');

insert into Volunteer_Language(Volunteer_id, language_id) values(1,1);
insert into Volunteer_Language(Volunteer_id, language_id) values(1,2);
insert into Volunteer_FieldOfStudy(Volunteer_id, fieldOfStudy_id) values(1,2);
	
insert into Administrator(firstName,lastName,mailAddress,accessKey,role,phoneNumber,password,civility_id) 
	values('Alaric','Hermant','alaric_hermant@yahoo.fr','xyz','ADMIN','07123456','123456789',2);
insert into Administrator(firstName,lastName,mailAddress,accessKey,role,phoneNumber,password,civility_id) 
	values('Irinda','riquelme','irinda.r@gmail.com','xyz','ADMIN','07123456','123456789',1);


insert into AbstractEvent(street1, country_id, zipcode, subject,description,organisation_id ,type_id ,DTYPE)
values ('14 rue d''assas',1,'75006','Politique','  ',2,1,'O-E');

update AbstractEvent a, Organisation o set a.contact=o.contact where a.organisation_id=o.id;