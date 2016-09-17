use cpa;

insert into Level(name,next_id,previous_id,description) values ('A1',2,null,' Debutant ');
insert into Level(name,next_id,previous_id,description) values ('A2',3,1,'moyen');
insert into Level(name,next_id,previous_id,description) values ('B1',4,2,'moyen');
insert into Level(name,next_id,previous_id,description) values ('B2',5,3,'expaire');
insert into Level(name,next_id,previous_id,description) values ('C1',6,4,'expaire');
insert into Level(name,next_id,previous_id,description) values ('C2',null,5,'expaire');


insert into EventType (name, stereotype) values ('atelier socio linguistique', 'WORKSHOP');
insert into EventType (name) values ('visite de musée');

insert into FieldOfStudy(name) values ('IT engineering');
insert into FieldOfStudy(name) values ('Business');
insert into FieldOfStudy(name) values ('Economic');
insert into FieldOfStudy(name) values ('Droit');
insert into FieldOfStudy(name) values ('Mathématiques');
insert into FieldOfStudy(name) values ('Physique et chimie');

insert into ProfessionalLearningProgramDomain(name) values ('Electricité');
insert into ProfessionalLearningProgramDomain(name) values ('Plomberie');
insert into ProfessionalLearningProgramDomain(name) values ('Mécaniaqe');

insert into LanguageLearningProgramType(name) values ('Francais pour reprendre des etudes');
insert into LanguageLearningProgramType(name) values ('Francais pour suivre une formation');
insert into LanguageLearningProgramType(name) values ('Francais');


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


insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('SCIENCE PO', '16 rue Saint Guillaume', '75006', 'Paris', 48.85537310000001, 2.329008599999952, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Elyse","phoneNumber":"00331234567","mailAddress":"Elyse@gmail.com"}', 1);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('SINGA', '43 rue des écoles', '75005', 'Paris', 48.8497584, 2.344234300000039, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1, '{"name":"Paul","phoneNumber":"00331234765","mailAddress":"paul@gmail.com"}', 3);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('CPA', '16 rue Saint Guillaume', '75006', 'Paris', 48.85537310000001, 2.329008599999952, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 1);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('CENTRE POMPIDOU','43 rue des écoles', '75005', 'Paris', 48.8497584, 2.344234300000039, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 2);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('BNF', '16 rue Saint Guillaume', '75006', 'Paris', 48.85537310000001, 2.329008599999952, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 2);


insert into AbstractLearningProgram(registrationStartDate, startDate, endDate, street1, postalCode, locality, lat, lng, googleMapId, country_id, organisation_id,level_id, DTYPE, type_id) 
	values(DATE_ADD(now(),INTERVAL -180 DAY), DATE_ADD(now(),INTERVAL -120 DAY), DATE_ADD(now(),INTERVAL -60 DAY), '16 rue Saint Guillaume', '75006', 'Paris', 48.85537310000001, 2.329008599999952, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1,1,1,'L', 1);
insert into AbstractLearningProgram(registrationStartDate, startDate, endDate, street1, postalCode, locality, lat, lng, googleMapId, country_id, organisation_id,level_id, DTYPE, type_id) 
	values(DATE_ADD(now(),INTERVAL -120 DAY), DATE_ADD(now(),INTERVAL -60 DAY), now(), '43 rue des écoles', '75005', 'Paris', 48.8497584, 2.344234300000039, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1,4,3,'L', 3);
insert into AbstractLearningProgram(registrationStartDate, startDate, endDate, street1, postalCode, locality, lat, lng, googleMapId, country_id, organisation_id,level_id, DTYPE, type_id)
	values(DATE_ADD(now(),INTERVAL -60 DAY), now(), DATE_ADD(now(),INTERVAL 60 DAY), '16 rue Saint Guillaume', '75006', 'Paris', 48.85537310000001, 2.329008599999952, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1,1,1,'L', 2);
insert into AbstractLearningProgram(registrationStartDate, startDate, endDate, street1, postalCode, locality, lat, lng, googleMapId, country_id, organisation_id,level_id, DTYPE, type_id) 
	values(now(), DATE_ADD(now(),INTERVAL 60 DAY), DATE_ADD(now(),INTERVAL 120 DAY), '43 rue des écoles', '75005', 'Paris', 48.8497584, 2.344234300000039, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1,4,3,'L', 1);
insert into AbstractLearningProgram(registrationStartDate, startDate, endDate, street1, postalCode, locality, lat, lng, googleMapId, country_id, organisation_id,level_id, DTYPE, domain_id) 
	values(DATE_ADD(now(),INTERVAL 60 DAY), DATE_ADD(now(),INTERVAL 120 DAY), DATE_ADD(now(),INTERVAL 180 DAY), '16 rue Saint Guillaume', '75006', 'Paris', 48.85537310000001, 2.329008599999952, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1,1,1,'P', 1);
insert into AbstractLearningProgram(registrationStartDate, startDate, endDate, street1, postalCode, locality, lat, lng, googleMapId, country_id, organisation_id,level_id, DTYPE, domain_id) 
	values(DATE_ADD(now(),INTERVAL 120 DAY), DATE_ADD(now(),INTERVAL 180 DAY), DATE_ADD(now(),INTERVAL 240 DAY), '43 rue des écoles', '75005', 'Paris', 48.8497584, 2.344234300000039, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1,4,3,'P', 2);

	
insert into AbstractLearningProgram_courses(LearningProgram_id, level_id, translatorRequired) values(1, 1, true );
insert into AbstractLearningProgram_courses(LearningProgram_id, level_id, translatorRequired) values(2, 1, false );

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

insert into Volunteer_Language(volunteer_id, language_id) values(1,1);
insert into Volunteer_Language(volunteer_id, language_id) values(1,2);
insert into Volunteer_FieldOfStudy(volunteer_id, fieldOfStudy_id) values(1,2);
insert into Volunteer_Language(volunteer_id, language_id) values(3,1);
insert into Volunteer_Language(volunteer_id, language_id) values(4,2);
insert into Volunteer_FieldOfStudy(volunteer_id, fieldOfStudy_id) values(5,2);
insert into Volunteer_Language(volunteer_id, language_id) values(2,1);
insert into Volunteer_Language(volunteer_id, language_id) values(2,2);
insert into Volunteer_FieldOfStudy(volunteer_id, fieldOfStudy_id) values(1,2);


	
insert into Administrator(firstName,lastName,mailAddress,accessKey,role,phoneNumber,password,civility_id) 
	values('Alaric','Hermant','alaric_hermant@yahoo.fr','xyz','ADMIN','07123456','123456789',2);
insert into Administrator(firstName,lastName,mailAddress,accessKey,role,phoneNumber,password,civility_id) 
	values('Irinda','riquelme','irinda.r@gmail.com','xyz','ADMIN','07123456','123456789',1);

insert into Refugee (firstName,lastName,birthDate,mailAddress,phoneNumber,accessKey,password)
values ('Alaric', 'Hermant', NULL, 'az', NULL, 'R-a871ce00-e7d2-497e-8a4e-d272b8b5b520', 'f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9');

insert into Refugee_languageSkills(Refugee_id, language_id,level_id) values(1,1,1);
insert into Refugee_FieldOfStudy(refugee_id, fieldOfStudy_id) values(1,1);

insert into AbstractEvent(audience, street1, postalCode, locality, lat, lng, googleMapId, country_id, subject,description,startDate,endDate,organisation_id ,type_id ,DTYPE)
values ('REFUGEE', '16 rue Saint Guillaume', '75006', 'Paris', 48.85537310000001, 2.329008599999952, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, 'Comprendre les éléctions présidentielles','  ',DATE_ADD(now(),INTERVAL 120 DAY), DATE_ADD(now(),INTERVAL 180 DAY),2,1,'O-E');
insert into AbstractEvent(audience, street1, postalCode, locality, lat, lng, googleMapId, country_id, subject,description,startDate,endDate,organisation_id ,type_id ,DTYPE)
values ('REFUGEE', '16 rue Saint Guillaume', '75006', 'Paris', 48.85537310000001, 2.329008599999952, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, 'Chercher un emploi','  ',DATE_ADD(now(),INTERVAL 180 DAY), DATE_ADD(now(),INTERVAL 240 DAY),4,1,'O-E');
insert into AbstractEvent(audience, street1, postalCode, locality, lat, lng, googleMapId, country_id, subject,description,startDate,endDate,volunteer_id ,type_id ,DTYPE)
values ('REFUGEE', '43 rue des écoles', '75005', 'Paris', 48.8497584, 2.344234300000039, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1,'Visite du musée du Louvre','  ',DATE_ADD(now(),INTERVAL 180 DAY), DATE_ADD(now(),INTERVAL 240 DAY),5,2,'V-E');
insert into AbstractEvent(audience, street1, postalCode, locality, lat, lng, googleMapId, country_id, subject,description,startDate,endDate,organisation_id ,type_id ,DTYPE)
values ('VOLUNTEER', '43 rue des écoles', '75005', 'Paris', 48.8497584, 2.344234300000039, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1,'Initiation FLE','  ',DATE_ADD(now(),INTERVAL 180 DAY), DATE_ADD(now(),INTERVAL 240 DAY),5,2,'O-E');

update AbstractEvent a, Organisation o set a.contact=o.contact where a.organisation_id=o.id;
update AbstractEvent set registrationStartDate=date_add(startDate, interval -30 day), description='Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.';
