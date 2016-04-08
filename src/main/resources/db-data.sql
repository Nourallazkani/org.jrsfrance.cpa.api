
insert into Level(name,next_id,previous_id,description) values ('A1',2,null,' Debutant ');
insert into Level(name,next_id,previous_id,description) values ('A2',3,1,'moyen');
insert into Level(name,next_id,previous_id,description) values ('B1',4,2,'moyen');
insert into Level(name,next_id,previous_id,description) values ('B2',null,3,'expaire');

insert into FieldOfStudy(name) values ('IT engineering');
insert into FieldOfStudy(name) values ('business');
insert into FieldOfStudy(name) values ('economic');

insert into OrganisationCategory(name) values ('université');
insert into OrganisationCategory(name) values ('bibliothéque');
insert into OrganisationCategory(name) values ('assosiation');

insert into Civility(name) values ( 'Mme' );
insert into Civility(name) values ( 'Mr' );

insert into Country(name ,isoCode) values ('France', 'FR');
insert into Country(name , isoCode) values ('Syrie' , 'SYR');

insert into Language(name) values('Français');
insert into Language(name) values('Anglais');
insert into Language(name) values('Arabe');
insert into Language(name) values('Dari');


insert into Organisation(name, street1, country_id, zipcode, contact, category_id) values ('science po','1 rue Pasteur',1,'75006', '{"name":"a","phoneNumber":"b","mailAddress":"c"}', 1);
insert into Organisation(name, street1, country_id, zipcode, contact, category_id) values ('JRS','5 rue assas', 1, '75005','{"name":"e","phoneNumber":"f","mailAddress":"g"}', 2);
insert into Organisation(name, street1, country_id, zipcode, contact, category_id) values ('SRS','8 boulvard Mazzeh', 2, '099', '{"name":"i","phoneNumber":"j","mailAddress":"k"}', 3);
insert into Organisation(name, street1, country_id, zipcode, contact, category_id) values ('DamasLanguageCenter','10 rue Damas', 2, '099', '{"name":"l","phoneNumber":"m","mailAddress":"n"}', 1);

insert into Cursus(name,country_id,organisation_id,level_id) values('A1',1,1,1);
insert into Cursus(name,country_id,organisation_id,level_id) values('B1',2,3,3);

insert into Cursus_courses(Cursus_id, level_id, translatorRequired) values(1, 1, true );
insert into Cursus_courses(Cursus_id, level_id, translatorRequired) values(2, 1, false );

insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,0,1,2,1);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,2,1,2);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,0,3,3,3);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,1,2,1);

insert into Volunteer(firstname,lastname,birthdate,mailAddress,phoneNumber,accessKey,password,role,civility_id,nationality_id,comments)
values ('Nour','ALLAZKANI',date(now()),'nourallazkani@gmail.com','0782836691','xyz','123456789','ADMIN',2,1,'working for JRS');

insert into Administrator(firstName,lastName,mailAddress,accessKey,role,phoneNumber,password,civility_id) 
values('Alaric','Hermant','alaric_hermant@yahoo.fr','xyz','ADMIN','07123456','123456789',2);
insert into Administrator(firstName,lastName,mailAddress,accessKey,role,phoneNumber,password,civility_id) 
values('Irinda','riquelme','irinda.r@gmail.com','xyz','ADMIN','07123456','123456789',1);

insert into Volunteer_Language(Volunteer_id, language_id) values(1,1);
insert into Volunteer_Language(Volunteer_id, language_id) values(1,2);

update Teaching set contactName = 'Irinda' , contactPhone = '07123456', contactMailAddress='irinda.r@gmail.com' where id =1 ;
update Teaching set contactName = 'Nour' , contactPhone = '07123456', contactMailAddress='nourallazkani@gmail.com' where id =2 ;
update Teaching set contactName = 'Irinda' ,contactPhone = '07123456', contactMailAddress='alaric_hermant@yahoo.fr' where id =3 ;

insert into Volunteer_Language(Volunteer_id, language_id) values(1,1);
insert into Volunteer_Language(Volunteer_id, language_id) values(1,2);

insert into Volunteer(firstname,lastname,birthdate,mailAddress,phoneNumber,accessKey,password,role,civility_id,nationality_id,comments)
values ('lucile','flo',date(now()),'nourallazkani@gmail.com','0782836691','xyz','123456789','ADMIN',1,1,'working for JRS');
insert into Volunteer(firstname,lastname,birthdate,mailAddress,phoneNumber,accessKey,password,role,civility_id,nationality_id,comments)
values ('jawad','dsa',date(now()),'nourallazkani@gmail.com','0782836691','xyz','123456789','ADMIN',2,1,'working for JRS');
