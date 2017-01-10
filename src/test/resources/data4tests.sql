insert into Level(name,description) values ('A1',' Debutant ');
insert into Level(name,description) values ('A2','moyen');
insert into Level(name,description) values ('B1','moyen');
insert into Level(name,description) values ('B2','expert');
insert into Level(name,description) values ('C1','expert');
insert into Level(name,description) values ('C2','expert');
insert into EventType (name, stereotype) values ('atelier socio linguistique', 'WORKSHOP');
insert into EventType (name) values ('visite de musée');
insert into EventType (name) values ('autre');
insert into FieldOfStudy(name) values ('Informatique');
insert into FieldOfStudy(name) values ('Commerce');
insert into FieldOfStudy(name) values ('Economie');
insert into FieldOfStudy(name) values ('Droit');
insert into FieldOfStudy(name) values ('Mathématiques');
insert into FieldOfStudy(name) values ('Physique et chimie');
insert into ProfessionalLearningProgramDomain(name) values ('Electricité');
insert into ProfessionalLearningProgramDomain(name) values ('Plomberie');
insert into ProfessionalLearningProgramDomain(name) values ('Mécanique');
insert into LanguageLearningProgramType(name) values ('Francais pour reprendre des etudes');
insert into LanguageLearningProgramType(name) values ('Francais pour suivre une formation');
insert into LanguageLearningProgramType(name) values ('Francais');
insert into OrganisationCategory(name, stereotype) values ('université', 'UNIVERSITY');
insert into OrganisationCategory(name, stereotype, additionalInformations) values ('bibliothéque', 'LIBRARY', '["Horaires d''ouverture", "Initiation par des bénévomes"]');
insert into OrganisationCategory(name, stereotype) values ('association', 'NGO');
insert into Civility(name) values ( 'Mr' );
insert into Civility(name) values ( 'Mme' );
insert into Country(name ,isoCode) values ('France', 'FR');
insert into Country(name , isoCode) values ('Syrie' , 'SYR');
insert into Country(name , isoCode) values ('Afganistan' , 'Afg');
insert into Country(name , isoCode) values ('Germany' , 'Gr');
insert into Country(name , isoCode) values ('Iraque' , 'Irq');
insert into Language(name) values('Anglais');
insert into Language(name) values('Arabe');
insert into Language(name) values('Dari');


insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) values ('Science Po', '27 rue Saint Guillaume', '75006', 'Paris', 48.8540952, 2.3261858, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Elyse","phoneNumber":"00331234567","mailAddress":"Elyse@gmail.com"}', 1);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) values ('La Sorbonne', '43 rue des écoles', '75005', 'Paris', 48.8489456, 2.3445896, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1, '{"name":"Paul","phoneNumber":"00331234765","mailAddress":"paul@gmail.com"}', 1);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) values ('Université Paris Est', '61 avenue du général de Gaulle', '94000', 'Créteil', 48.7880672, 2.4432152, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 1);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) values ('Alliance française','101 boulevard Raspail', '75006', 'Paris', 48.8462748, 2.3263393, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 2);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) values ('Singa', '73 rue d''Amsterdam', '75008', 'Paris', 48.8816194, 2.3246769, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 2);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) values ('JRS', '14 rue d''Assas', '75006', 'Paris', 48.8493583, 2.3260384, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 2);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) values ('Pole emploi', '10 rue Brancion', '75015', 'Paris', 48.8357559, 2.3035182, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 2);
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e4', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='o@o.o' where id=1;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e2', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='sorbonne@cpafrance.fr' where id=2;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e3', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='upec@cpafrance.fr' where id=3;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e5', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='alliance@cpafrance.fr' where id=4;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e6', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='singa@cpafrance.fr' where id=5;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e7', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='jrs@cpafrance.fr' where id=6;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e8', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='pole.emploi@cpafrance.fr' where id=7;
update Organisation set additionalInformations='{"Horaires d''ouverture":"Du lundi au vendredi de 9h00 à 19h00"}' where category_id =2;

insert into Refugee (firstName,lastName, fieldOfStudy_id, civility_id, birthDate,mailAddress,phoneNumber,accessKey,password) values ('Alaric', 'Hermant', 1, 1,  NULL, 'r@r.r', NULL, 'R-a871ce00-e7d2-497e-8a4e-d272b8b5b520', 'f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9');
insert into Refugee_Language(refugee_id, language_id) values(1,1);
insert into Refugee (firstName,lastName, fieldOfStudy_id, civility_id, birthDate,mailAddress,phoneNumber,accessKey,password) values ('Nour', 'Allazkani', 1, 1, NULL, 'r2@r2.r2', NULL, 'R-3b743606-928a-4086-852a-9efd72f83d01', 'f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9');
insert into Refugee_Language(refugee_id, language_id) values(2,1);
insert into Refugee_Language(refugee_id, language_id) values(2,2);

insert into Volunteer (firstName, lastName, birthDate, mailAddress, phoneNumber, accessKey, password, street1, street2, postalCode, locality, lat, lng, googleMapId, availableForConversation, availableForInterpreting, availableForSupportInStudies, availableForActivities, activities, civility_id, country_id, organisation_id) values('Alaric', 'Hermant', NULL, 'v@v.v', NULL, 'V-41eed0a4-0bbb-4594-a1cf-f8ab3ff810ec', 'f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', NULL, NULL, '75007', 'Paris', 48.85433450, 2.31340290, NULL, NULL, true, true, false, NULL, NULL, NULL, NULL);

insert into Administrator(firstName, lastName, mailAddress, accessKey, phoneNumber, password, civility_id)  values('Alaric','Hermant','alaric_hermant@yahoo.fr','A-cfa5be9e-c8dd-477c-9d3d-a3cb452be3f6','07123456','f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9',2);
insert into Administrator(firstName, lastName, mailAddress, accessKey, phoneNumber, password, civility_id)  values('Irinda','riquelme','irinda.r@gmail.com','A-f51d7ea3-1624-45e6-94ab-c85b4c458638','07123456','f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9',1);
	
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (true, false, 1, 2, 1, now() + interval '-1 MONTH');
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (false,true,2,1,2, now() + interval '-1 MONTH');
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (true,false,3,3,3, now() + interval '-1 MONTH');
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (false,true,4,2,1, now() + interval '-1 MONTH');
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (false,true,5,2,2, now() + interval '-1 MONTH');
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (true,true,6,2,3, now() + interval '-1 MONTH');
update Teaching set registrationClosingDate = now() + interval '7 DAY';
update Teaching set contact=o.contact, link = 'http://jrsfrance.org' from Teaching t join Organisation o on t.organisation_id=o.id;
insert into Teaching_registrations( refugee_id, Teaching_id) values (1,1);


insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, type_id) values(now() + interval '-30 day', 4,1,'L', 1);
insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, type_id) values(now(), 4,3,'L', 3);
insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, type_id) values(now() + interval '30 day', 6,1,'L', 2);
insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, type_id) values(now() + interval '60 day', 6,3,'L', 1);
insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, domain_id) values(now() + interval '60 day', 5,1,'P', 1);
insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, domain_id) values(now() + interval '90 day', 7,3,'P', 2);
update AbstractLearningProgram set link='http://www.jrsfrance.org/', registrationOpeningDate=startDate + INTERVAL '-120 day', registrationClosingDate=startDate + INTERVAL '-30 day', endDate=startDate + INTERVAL '90 day';
update AbstractLearningProgram	set street1=o.street1,street2=o.street2, locality=o.locality, postalCode=o.postalCode, country_id=o.country_id, lat=o.lat, lng=o.lng, googleMapId=o.googleMapId from AbstractLearningProgram a join Organisation o on a.organisation_id=o.id;
insert into AbstractLearningProgram_registrations( refugee_id, AbstractLearningProgram_id) values (1,1);
insert into AbstractLearningProgram_registrations( refugee_id, AbstractLearningProgram_id) values (1,5);

insert into AbstractEvent(audience, subject, startDate,organisation_id ,type_id ,DTYPE) values ('REFUGEE', 'Comprendre les éléctions présidentielles', now() + INTERVAL '15 DAY', 2, 1, 'O-E');
insert into AbstractEvent(audience, subject, startDate,organisation_id ,type_id ,DTYPE) values ('REFUGEE', 'Chercher un emploi',now() + INTERVAL '30 DAY',4,1,'O-E');
insert into AbstractEvent(audience, subject, startDate,organisation_id ,type_id ,DTYPE) values ('REFUGEE', 'Visite du musée du Louvre',now() + INTERVAL '45 DAY',5,2,'O-E');
insert into AbstractEvent(audience, subject, startDate,organisation_id ,type_id ,DTYPE) values ('VOLUNTEER', 'Initiation FLE', now() + INTERVAL '60 DAY',5,2,'O-E');
update AbstractEvent set contact=o.contact, street1=o.street1, street2=o.street2, locality=o.locality, postalCode=o.postalCode, country_id=o.country_id, lat=o.lat, lng=o.lng, googleMapId=o.googleMapId from AbstractEvent a join Organisation o on a.organisation_id=o.id;
update AbstractEvent set endDate = startDate + interval '2 hour', link='http://www.jrsfrance.org/', registrationOpeningDate=startDate + interval '-40 day',  registrationClosingDate=startDate + interval '-5 day';
update AbstractEvent set descriptionI18n = json_build_object('textEn', 'xx', 'textAr', 'بـ', 'textPrs', '');
insert into AbstractEvent_registrations( refugee_id, AbstractEvent_id) values (1,1);
insert into AbstractEvent_registrations( refugee_id, AbstractEvent_id) values (1,3);

insert into MeetingRequest ( dateConstraint, reason, additionalInformations, postDate, acceptationDate, street1, street2, postalCode, locality, lat, lng, googleMapId, refugee_id, volunteer_id, country_id) values('Lundi 24 octobre', 'SUPPORT_IN_STUDIES', 'Malheureusement, mon portable (ne marche pas)! ', '2016-10-17 07:06:45', '2016-10-17 07:37:19', NULL, NULL, '75001', 'Paris', 48.86404930, 2.33105260, NULL, 1, 1, 1);

