
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




insert into Organisation(name, street1, country_id, zipcode, category_id) values ('science po','1 rue Pasteur',1,'75006', 1);
insert into Organisation(name, street1, country_id, zipcode, category_id) values ('JRS','5 rue assas', 1, '75005',2);
insert into Organisation(name, street1, country_id, zipcode, category_id) values ('SRS','8 boulvard Mazzeh', 2, '099', 3);
insert into Organisation(name, street1, country_id, zipcode, category_id) values ('DamasLanguageCenter','10 rue Damas', 2, '099', 1);

insert into Cursus(name,country_id,organisation_id,level_id) values('A1',1,1,1);
insert into Cursus(name,country_id,organisation_id,level_id) values('B1',2,3,3);

insert into Cursus_courses(Cursus_id, level_id, translatorRequired) values(1, 1, true );
insert into Cursus_courses(Cursus_id, level_id, translatorRequired) values(2, 1, false );

insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,0,1,2,1);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,2,1,2);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,0,3,3,3);
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,1,2,1);

insert into volunteer(firstname,lastname,birthdate,mail,phoneNumber,accessKey,password,role,civility_id,nationality_id,comments)
values ('Nour','ALLAZKANI',date(now()),'nourallazkani@gmail.com',0782836691,'xyz','123456789','ADMIN',2,1,'working for JRS');