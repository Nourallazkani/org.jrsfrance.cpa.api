
SELECT * FROM babel.country;

insert into level(name,next_id,previous_id,description) values ('A1',2,null,' Debutant ');
insert into level(name,next_id,previous_id,description) values ('A2',3,1,'moyen');
insert into level(name,next_id,previous_id,description) values ('B1',4,2,'moyen');
insert into level(name,next_id,previous_id,description) values ('B2',null,3,'expaire');

insert into fieldofstudy(name) values ('IT engineering');
insert into fieldofstudy(name) values ('business');
insert into fieldofstudy(name) values ('economic');


insert into education(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,0,1,2,1);
insert into education(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,2,1,3);
insert into education(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (1,0,3,3,5);
insert into education(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id) values (0,1,1,2,4);





insert into civility(name) values ( 'Mme' );
insert into civility(name) values ( 'Mr' );

insert into Country(name ,isoCode) values ('france', 'FR');
insert into Country(name , isoCode) values ('Syrie' , 'SYR');

insert into organisation(name,street1,country_id,zipcode) values ('science po','1 rue Pasteur',1,'75006');
insert into organisation(name,street1,country_id,zipcode) values ('JRS','5 rue assas', 1 , '75005');
insert into organisation(name,street1,country_id,zipcode) values ('SRS','8 boulvard Mazzeh', 2 , '099');
insert into organisation(name,street1,country_id,zipcode) values ('DamasLanguageCenter','10 rue Damas', 2 , '099');

insert into cursus(name,country_id,organisation_id,level_id) values('A1',1,1,1);
insert into cursus(name,country_id,organisation_id,level_id) values('B1',2,3,3);
insert into course(name, zipcode , country_id , cursus_id, translatorRequired) values('A1.1','75006',1,1,true );
insert into course(id,name, zipcode , country_id , cursus_id, translatorRequired) values(2,'B1.1','099',1,2,false );

/* trying to delete an item from a table*/
insert into course(name, city) values('A2', 'Toulouse');
delete from course where course.id = 1;
delete from course where course.id = 2;
delete from cursus where cursus.id = 1;
delete from cursus where cursus.id = 2;
/* end of deleting*/



insert into student( firstname , lastname ,nationality_id, mail, password, civility_id ) values ( 'Karam', 'ALI', 1 , 'hasan.ali@gmail.com' , '123456' , 1 );
update nationality set nationality.name='Francaise' where id=0;
update nationality set nationality.shortcut='LIB' where id=1;
update organisation set organisation.city='Paris' where id=1;
update organisation set organisation.city='Damas' where id=4;
update cursus set organisation_id=2 where id=1;
update cursus set organisation_id=1 where id=2;
update course set course.city='paris' where id=1;
update course set course.city='damas' where id=2;

SELECT course.id, cursus.name, organisation.city, country.name from course, cursus , organisation,country
where course.cursus_id = cursus.id and cursus.organisation_id= organisation.id
and organisation.country_id = country.id
order by organisation.name;

select course.id , cursus.name , organisation.city, country.name from course
join cursus on course.cursus_id = cursus.id
join organisation on cursus.organisation_id = organisation.id
join country on organisation.country_id = country.id
order by organisation.name
;

alter table course add cursus_id int;
alter table organisation change country country_id int;


select course.id, cursus.name, organisation.country_id  from course,cursus,organisation ;

select cursus.name, organisation.name, country.name
from cursus,country, organisation
where cursus.country_id=country.id and cursus.organisation_id= organisation.id
and cursus.name like '%A%'
;

select id, name from organisation where organisation.name like '%cours%';


select cursus.name, organisation.name, country.name
from cursus
join country on cursus.country_id=country.id
join organisation on cursus.organisation_id= organisation.id
where organisation.name like '%%'
order by country.name;

update cursus set cursus.city = 'Paris' where id=1;
update cursus set cursus.city = 'toulouse' where id=2;

select country.name ,organisation.id , organisation.name , street1, street2, zipcode,city from organisation
join country on organisation.country_id= country.id
where country.name like '%france%'
;

insert into Country(name) values( 'Afghanistan');
insert into Country(name) values( 'Afrique du Sud');
insert into Country(name) values( 'Albanie');
insert into Country(name) values( 'Algérie');
insert into Country(name) values( 'Allemagne');
insert into Country(name) values( 'Andorre');
insert into Country(name) values( 'Angola');
insert into Country(name) values( 'Anguilla');
insert into Country(name) values( 'Antarctique, Base Mawson');
insert into Country(name) values( 'Antarctique, Base Scott');
insert into Country(name) values( 'Antigua-et-Barbuda');
insert into Country(name) values( 'Antilles Françaises (Saint Martin);');
insert into Country(name) values( 'Antilles Néerlandaises (Saint Martin);');
insert into Country(name) values( 'Arabie saoudite');
insert into Country(name) values( 'Argentine');
insert into Country(name) values( 'Arménie');
insert into Country(name) values( 'Aruba');
insert into Country(name) values( 'Australie');
insert into Country(name) values( 'Autriche');
insert into Country(name) values( 'Azerbaïdjan');
insert into Country(name) values( 'Bahamas');
insert into Country(name) values( 'Bahreïn');
insert into Country(name) values( 'Bangladesh');
insert into Country(name) values( 'Barbade');
insert into Country(name) values( 'Belgique');
insert into Country(name) values( 'Belize');
insert into Country(name) values( 'Bénin');
insert into Country(name) values( 'Bermudes');
insert into Country(name) values( 'Bhoutan');
insert into Country(name) values( 'Bielorussie');
insert into Country(name) values( 'Birmanie (Myanmar);');
insert into Country(name) values( 'Bolivie');
insert into Country(name) values( 'Bosnie-Herzégovine');
insert into Country(name) values( 'Botswana');
insert into Country(name) values( 'Brésil');
insert into Country(name) values( 'Brunei');
insert into Country(name) values( 'Bulgarie');
insert into Country(name) values( 'Burkina Faso');
insert into Country(name) values( 'Burundi');
insert into Country(name) values( 'Cambodge');
insert into Country(name) values( 'Cameroun');
insert into Country(name) values( 'Canada');
insert into Country(name) values( 'Cap-Vert');
insert into Country(name) values( 'Chili');
insert into Country(name) values( 'Chine');
insert into Country(name) values( 'Chypre');
insert into Country(name) values( 'Colombie');
insert into Country(name) values( 'Comores');
insert into Country(name) values( 'Congo');
insert into Country(name) values( 'Corée du Nord');
insert into Country(name) values( 'Corée du Sud');
insert into Country(name) values( 'Costa Rica');
insert into Country(name) values( 'Côte d''Ivoire');
insert into Country(name) values( 'Crète');
insert into Country(name) values( 'Croatie');
insert into Country(name) values( 'Cuba');
insert into Country(name) values( 'Danemark');
insert into Country(name) values( 'Djibouti');
insert into Country(name) values( 'Dominique');
insert into Country(name) values( 'Égypte');
insert into Country(name) values( 'Émirats arabes unis');
insert into Country(name) values( 'Équateur');
insert into Country(name) values( 'Érythrée');
insert into Country(name) values( 'Espagne');
insert into Country(name) values( 'Estonie');
insert into Country(name) values( 'États-Unis');
insert into Country(name) values( 'Éthiopie');
insert into Country(name) values( 'Fidji');
insert into Country(name) values( 'Finlande');
insert into Country(name) values( 'France');
insert into Country(name) values( 'Gabon');
insert into Country(name) values( 'Gambie');
insert into Country(name) values( 'Géorgie');
insert into Country(name) values( 'Ghana');
insert into Country(name) values( 'Gibraltar');
insert into Country(name) values( 'Grèce');
insert into Country(name) values( 'Grenade');
insert into Country(name) values( 'Groënland');
insert into Country(name) values( 'Guadeloupe');
insert into Country(name) values( 'Guam');
insert into Country(name) values( 'Guantanamo');
insert into Country(name) values( 'Guatemala');
insert into Country(name) values( 'Guinée');
insert into Country(name) values( 'Guinée équatoriale');
insert into Country(name) values( 'Guinée-Bissao');
insert into Country(name) values( 'Guyana');
insert into Country(name) values( 'Guyane Française');
insert into Country(name) values( 'Haïti');
insert into Country(name) values( 'Honduras');
insert into Country(name) values( 'Hong Kong');
insert into Country(name) values( 'Hongrie');
insert into Country(name) values( 'Iles Ascension');
insert into Country(name) values( 'Iles des Canaries');
insert into Country(name) values( 'Iles Cayman');
insert into Country(name) values( 'Ile Chatham');
insert into Country(name) values( 'Ile Christmas');
insert into Country(name) values( 'Iles Cocos');
insert into Country(name) values( 'Iles Cook');
insert into Country(name) values( 'Ile de la Réunion');
insert into Country(name) values( 'Iles de Paques');
insert into Country(name) values( 'Iles Falkland');
insert into Country(name) values( 'Iles Faroe');
insert into Country(name) values( 'Iles Marshall');
insert into Country(name) values( 'Ile Mariana');
insert into Country(name) values( 'Ile Maurice');
insert into Country(name) values( 'Iles Midway');
insert into Country(name) values( 'Ile de Montserrat');
insert into Country(name) values( 'Ile Norfolk');
insert into Country(name) values( 'Iles Salomon');
insert into Country(name) values( 'Iles Turks et Caicos');
insert into Country(name) values( 'Iles Vierges Britaniques');
insert into Country(name) values( 'Iles Vierges US');
insert into Country(name) values( 'Ile Wake');
insert into Country(name) values( 'Iles Wallis et Futuna');
insert into Country(name) values( 'Inde');
insert into Country(name) values( 'Indonésie');
insert into Country(name) values( 'Iran');
insert into Country(name) values( 'Iraq');
insert into Country(name) values( 'Irlande');
insert into Country(name) values( 'Islande');
insert into Country(name) values( 'Israël');
insert into Country(name) values( 'Italie');
insert into Country(name) values( 'Jamaïque');
insert into Country(name) values( 'Japon');
insert into Country(name) values( 'Jérusalem');
insert into Country(name) values( 'Jordanie');
insert into Country(name) values( 'Kazakhstan');
insert into Country(name) values( 'Kenya');
insert into Country(name) values( 'Kirghizistan');
insert into Country(name) values( 'Kiribati');
insert into Country(name) values( 'Koweït');
insert into Country(name) values( 'Laos');
insert into Country(name) values( 'Lesotho');
insert into Country(name) values( 'Lettonie');
insert into Country(name) values( 'Liban');
insert into Country(name) values( 'Liberia');
insert into Country(name) values( 'Libye');
insert into Country(name) values( 'Liechtenstein');
insert into Country(name) values( 'Lituanie');
insert into Country(name) values( 'Luxembourg');
insert into Country(name) values( 'Macao');
insert into Country(name) values( 'Macédoine');
insert into Country(name) values( 'Madagascar');
insert into Country(name) values( 'Malaisie');
insert into Country(name) values( 'Malawi');
insert into Country(name) values( 'Maldives');
insert into Country(name) values( 'Mali');
insert into Country(name) values( 'Malte');
insert into Country(name) values( 'Maroc');
insert into Country(name) values( 'Martinique');
insert into Country(name) values( 'Mauritanie');
insert into Country(name) values( 'Mayotte');
insert into Country(name) values( 'Mexique');
insert into Country(name) values( 'Micronésie');
insert into Country(name) values( 'Moldavie');
insert into Country(name) values( 'Monaco (Monte Carlo);');
insert into Country(name) values( 'Mongolie');
insert into Country(name) values( 'Montenegro');
insert into Country(name) values( 'Mozambique');
insert into Country(name) values( 'Namibie');
insert into Country(name) values( 'Nauru');
insert into Country(name) values( 'Népal');
insert into Country(name) values( 'Nicaragua');
insert into Country(name) values( 'Niger');
insert into Country(name) values( 'Nigeria');
insert into Country(name) values( 'Niue');
insert into Country(name) values( 'Norvège');
insert into Country(name) values( 'Nouvelle Calédonie');
insert into Country(name) values( 'Nouvelle-Zélande');
insert into Country(name) values( 'Ouganda');
insert into Country(name) values( 'Ouzbékistan');
insert into Country(name) values( 'Pakistan');
insert into Country(name) values( 'Palau (Palaos);');
insert into Country(name) values( 'Panama');
insert into Country(name) values( 'Papouasie - Nouvelle Guinée');
insert into Country(name) values( 'Paraguay');
insert into Country(name) values( 'Pays-Bas (Hollande);');
insert into Country(name) values( 'Pérou');
insert into Country(name) values( 'Philippines');
insert into Country(name) values( 'Pologne');
insert into Country(name) values( 'Polynésie Française (Archipel des Tuamotu);');
insert into Country(name) values( 'Porto Rico (Puerto Rico);');
insert into Country(name) values( 'Portugal');
insert into Country(name) values( 'Qatar');
insert into Country(name) values( 'République centrafricaine');
insert into Country(name) values( 'République démocratique du Congo');
insert into Country(name) values( 'République dominicaine');
insert into Country(name) values( 'République Tchèque (Tchéquie);');
insert into Country(name) values( 'Roumanie');
insert into Country(name) values( 'Royaume-Uni');
insert into Country(name) values( 'Russie (Fédération de);');
insert into Country(name) values( 'Rwanda');
insert into Country(name) values( 'Saint-Christophe-et-Niévès (St Kitts et Nevis);');
insert into Country(name) values( 'Sainte Hélène');
insert into Country(name) values( 'Sainte-Lucie');
insert into Country(name) values( 'Saint-Marin');
insert into Country(name) values( 'Saint Pierre et Miquelon');
insert into Country(name) values( 'Saint-Vincent-et-les Grenadines');
insert into Country(name) values( 'Salvador');
insert into Country(name) values( 'Samoa Américaine');
insert into Country(name) values( 'Samoa Occidentale');
insert into Country(name) values( 'Sao Tomé-et-Principe');
insert into Country(name) values( 'Sénégal');
insert into Country(name) values( 'Serbie');
insert into Country(name) values( 'Seychelles');
insert into Country(name) values( 'Sierra Leone');
insert into Country(name) values( 'Singapour');
insert into Country(name) values( 'Slovaquie');
insert into Country(name) values( 'Slovénie');
insert into Country(name) values( 'Somalie');
insert into Country(name) values( 'Soudan');
insert into Country(name) values( 'Sri Lanka');
insert into Country(name) values( 'Suède');
insert into Country(name) values( 'Suisse');
insert into Country(name) values( 'Suriname');
insert into Country(name) values( 'Swaziland');
insert into Country(name) values( 'Syrie');
insert into Country(name) values( 'Tadjikistan');
insert into Country(name) values( 'Taïwan');
insert into Country(name) values( 'Tanzanie');
insert into Country(name) values( 'Tchad');
insert into Country(name) values( 'Territoires Palestiniens');
insert into Country(name) values( 'Thaïlande');
insert into Country(name) values( 'Togo');
insert into Country(name) values( 'Tokelau');
insert into Country(name) values( 'Tonga');
insert into Country(name) values( 'Trinité-et-Tobago');
insert into Country(name) values( 'Tunisie');
insert into Country(name) values( 'Turkménistan');
insert into Country(name) values( 'Turquie');
insert into Country(name) values( 'Tuvalu');
insert into Country(name) values( 'Ukraine');
insert into Country(name) values( 'Uruguay');
insert into Country(name) values( 'Vanuatu');
insert into Country(name) values( 'Vatican');
insert into Country(name) values( 'Venezuela');
insert into Country(name) values( 'Viêt Nam');
insert into Country(name) values( 'Yémen');
insert into Country(name) values( 'Yougoslavie');
insert into Country(name) values( 'Zaïre voir Congo (la Rép. dém. du);');
insert into Country(name) values( 'Zambie');
insert into Country(name) values( 'Zanzibar');
insert into Country(name) values( 'Zimbabwe');