insert into Level(name,next_id,previous_id,description) values ('A1',2,null,' Debutant ');
insert into Level(name,next_id,previous_id,description) values ('A2',3,1,'moyen');
insert into Level(name,next_id,previous_id,description) values ('B1',4,2,'moyen');
insert into Level(name,next_id,previous_id,description) values ('B2',5,3,'expert');
insert into Level(name,next_id,previous_id,description) values ('C1',6,4,'expert');
insert into Level(name,next_id,previous_id,description) values ('C2',null,5,'expert');


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

insert into Civility(name) values ( 'Mme' );
insert into Civility(name) values ( 'Mr' );

insert into Country(name ,isoCode) values ('France', 'FR');
insert into Country(name , isoCode) values ('Syrie' , 'SYR');
insert into Country(name , isoCode) values ('Afganistan' , 'Afg');
insert into Country(name , isoCode) values ('Germany' , 'Gr');
insert into Country(name , isoCode) values ('Iraque' , 'Irq');


insert into Language(name) values('Anglais');
insert into Language(name) values('Arabe');
insert into Language(name) values('Dari');


insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('Science Po', '27 rue Saint Guillaume', '75006', 'Paris', 48.8540952, 2.3261858, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Elyse","phoneNumber":"00331234567","mailAddress":"Elyse@gmail.com"}', 1);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('La Sorbonne', '43 rue des écoles', '75005', 'Paris', 48.8489456, 2.3445896, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1, '{"name":"Paul","phoneNumber":"00331234765","mailAddress":"paul@gmail.com"}', 1);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('Université Paris Est', '61 avenue du général de Gaulle', '94000', 'Créteil', 48.7880672, 2.4432152, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 1);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('Alliance française','101 boulevard Raspail', '75006', 'Paris', 48.8462748, 2.3263393, 'Eic0NyBSdWUgZGVzIMOJY29sZXMsIDc1MDA1IFBhcmlzLCBGcmFuY2U', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 2);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('Singa', '73 rue d''Amsterdam', '75008', 'Paris', 48.8816194, 2.3246769, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 2);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('JRS', '14 rue d''Assas', '75006', 'Paris', 48.8493583, 2.3260384, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 2);
insert into Organisation(name, street1, postalCode, locality, lat, lng, googleMapId, country_id, contact, category_id) 
	values ('Pole emploi', '10 rue Brancion', '75015', 'Paris', 48.8357559, 2.3035182, 'ChIJgT3BP9Zx5kcRGTe9PIEMNHM', 1, '{"name":"Nour","phoneNumber":"00337651234","mailAddress":"nour@gmail.com"}', 2);

update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e4', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='o@o.o' where id=1;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e2', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='sorbonne@cpafrance.fr' where id=2;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e3', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='upec@cpafrance.fr' where id=3;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e5', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='alliance@cpafrance.fr' where id=4;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e6', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='singa@cpafrance.fr' where id=5;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e7', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='jrs@cpafrance.fr' where id=6;
update Organisation set accessKey='O-d6daffe2-01ed-4e40-bf1e-b2b102c873e8', password='f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', mailAddress='pole.emploi@cpafrance.fr' where id=7;

update Organisation set additionalInformations='{"Horaires d''ouverture":"Du lundi au vendredi de 9h00 à 19h00"}' where category_id =2;


/* learning programs */

insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, type_id) values(now() + interval '-60 day', 4,1,'L', 1);
insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, type_id) values(now() + interval '-30 day', 4,3,'L', 3);
insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, type_id) values(now(), 6,1,'L', 2);
insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, type_id) values(now() + interval '30 day', 6,3,'L', 1);
insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, domain_id) values(now() + interval '60 day', 5,1,'P', 1);
insert into AbstractLearningProgram(startDate, organisation_id,level_id, DTYPE, domain_id) values(now() + interval '90 day', 7,3,'P', 2);

update AbstractLearningProgram set 
	link='http://www.jrsfrance.org/',
	registrationOpeningDate=startDate + INTERVAL '-120 day',
	registrationClosingDate=startDate + INTERVAL '-30 day',
	endDate=startDate + INTERVAL '90 day';

update AbstractLearningProgram
	set 
	street1=o.street1,
	street2=o.street2,
	locality=o.locality,
	postalCode=o.postalCode,
	country_id=o.country_id,
	lat=o.lat,
	lng=o.lng,
	googleMapId=o.googleMapId
from AbstractLearningProgram a join Organisation o on a.organisation_id=o.id;


alter table Teaching drop column licence; 
alter table Teaching drop column master;
alter table Teaching add licence boolean;
alter table Teaching add master boolean;


/* teachings */

insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (true, false, 1, 2, 1, now() + interval '-1 MONTH');
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (false,true,2,1,2, now() + interval '-1 MONTH');
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (true,false,3,3,3, now() + interval '-1 MONTH');
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (false,true,4,2,1, now() + interval '-1 MONTH');
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (false,true,5,2,2, now() + interval '-1 MONTH');
insert into Teaching(licence,master,fieldOfStudy_id,languageLevelRequired_id,organisation_id, registrationOpeningDate) values (true,true,6,2,3, now() + interval '-1 MONTH');

update Teaching set registrationClosingDate = now() + interval '7 DAY';

update Teaching 
	set 
	contact=o.contact, 
	link = 'http://jrsfrance.org' 
from Teaching t join Organisation o on t.organisation_id=o.id;

/*Administrator */

insert into Administrator(firstName,lastName,mailAddress,accessKey,role,phoneNumber,password,civility_id) 
	values('Alaric','Hermant','alaric_hermant@yahoo.fr','xyz','ADMIN','07123456','123456789',2);
insert into Administrator(firstName,lastName,mailAddress,accessKey,role,phoneNumber,password,civility_id) 
	values('Irinda','riquelme','irinda.r@gmail.com','xyz','ADMIN','07123456','123456789',1);


/*Refugee*/
insert into Refugee (firstName,lastName, fieldOfStudy_id, birthDate,mailAddress,phoneNumber,accessKey,password)
	values ('Alaric', 'Hermant', 1,  NULL, 'r@r.r', NULL, 'R-a871ce00-e7d2-497e-8a4e-d272b8b5b520', 'f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9');

insert into Refugee_Language(refugee_id, language_id) values(1,1);


/*events*/
insert into AbstractEvent(audience, subject, startDate,organisation_id ,type_id ,DTYPE)
	values ('REFUGEE', 'Comprendre les éléctions présidentielles', now() + INTERVAL '15 DAY', 2, 1, 'O-E');
insert into AbstractEvent(audience, subject, startDate,organisation_id ,type_id ,DTYPE)
	values ('REFUGEE', 'Chercher un emploi',now() + INTERVAL '30 DAY',4,1,'O-E');
insert into AbstractEvent(audience, subject, startDate,organisation_id ,type_id ,DTYPE)
	values ('REFUGEE', 'Visite du musée du Louvre',now() + INTERVAL '45 DAY',5,2,'O-E');
insert into AbstractEvent(audience, subject, startDate,organisation_id ,type_id ,DTYPE)
	values ('VOLUNTEER', 'Initiation FLE', now() + INTERVAL '60 DAY',5,2,'O-E');

update AbstractEvent set
	endDate = startDate + interval '2 hour',
	link='http://www.jrsfrance.org/',
	registrationOpeningDate=startDate + interval '-40 day', 
	registrationClosingDate=startDate + interval '-5 day', 
	description='Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',
	descriptionI18n = json_build_object(
		'textEn', 
		'But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?',
		'textAr', 'ثم مايو وترك المتساقطة، ضرب, هذا في تطوير المنتصر الأمريكي, يبق مرجع مسؤولية ما. بها إذ مئات ألمانيا. كان ان زهاء موالية ويكيبيديا،. وقد هو الشرق، الأولية بالإنزال, على من وصافرات إتفاقية, قبل أي اللا كردة تطوير. ثم فكان وزارة وقد, ان جديدة سليمان، قام. بخطوط ضمنها الساحة شيء في, لعملة وإعلان أخذ بـ.
أم الا فهرست قتيل،, تطوير الطريق وتزويده عدد تم. أن وصل وإيطالي اليميني. أواخر السيء بأضرار يكن ان, ما تحت أحدث نهاية ومطالبة. عن به، شموليةً الأوروبيّون, يعبأ ومضى سقطت تحت بل, ٣٠ أجزاء اكتوبر الموسوعة أسر. يكن بالسيطرة المتاخمة الدولارات أن, خطّة اكتوبر وبالرغم حدى أن. تعد و غرّة، وايرلندا.
هو وأزيز الأرض ويكيبيديا، مكن, كردة تصرّف لكل أن. لم جُل والفلبين ماليزيا، تشيكوسلوفاكيا, بال ان تمهيد الأراضي. وحتى الشتاء أن لكل, حلّت ديسمبر اليابان، مع يتم. ومضى الإتفاقية ان بحث. ذات عل إنطلاق جديداً الثقيلة, وزارة والكوري المؤلّفة فعل قد.
وصل طوكيو لمحاكم الفرنسية بل, نفس جمعت سابق جسيمة كل. بها قدما الشهير المتّبعة لم, عل عرفها الآلاف الاندونيسية بها, هذه بل أخرى لفرنسا المتحدة. بلا وبعدما المسرح ما, و أضف كثيرة الشّعبين واندونيسيا،, أم بحث الإطلاق الأراضي. عن فصل شدّت والروسية.
تم على أصقاع الدنمارك, الثانية المتّبعة إذ لكل. ما تطوير فقامت إيطاليا أخر, ثم دون قتيل، المسرح, هامش الإقتصادي بحث تم. المبرمة بالإنزال الأوروبية، قد غير. شرسة سقطت لليابان دول مع.
في تمهيد وكسبت العناد غير. اكتوبر الغالي كما ثم, ٠٨٠٤ الصعداء ذات قد, ذلك ٣٠ غريمه يتمكن وإيطالي. لم ولم أراض وبعدما, عل فكانت اعلان بريطانيا، نفس. تعد ان الشرق، الشمال, تسمّى ليرتفع أم وفي. في قبضتهم اكتوبر الشهيرة حيث, تم تعد أواخر إحتار.
هذه هو طوكيو الأرض. عن الدمج وحرمان كلا, مع كان اتّجة ومطالبة. أي ثمّة أراضي مما. في يتم حلّت الجنرال, مئات الجنود وايرلندا في دنو. ضرب وصافرات التغييرات من, بل جهة تزامناً الثالث، الولايات, فصل إذ بالرّد الفرنسي. ثمّة يعبأ ان قام, بمباركة المزيفة الإمتعاض عن وقد.
حكومة تحرّكت ولكسمبورغ أما في, مقاومة للسيطرة مما في, انه خيار رجوعهم عل. لفرنسا محاولات قام أن. ولم وحتى لإعادة إستعمل ثم, جنوب ومضى اليابان وقد ان. ان الا يرتبط فرنسية ولاتّساع, من انه فمرّ الأولى الجنوبي, أي عجّل بأضرار حدى. أمّا ليركز السفن دول قد, إذ ذلك بهيئة الصينية.
ومضى بمعارضة كل ذلك, الصين الحكومة استمرار بل يتم. في أخر وبعض الضغوط. تم قِبل يتسنّى باستخدام به،. أن مشارف بالحرب استراليا، وتم. لأداء أعلنت لتقليعة فصل و.
تلك الأجل الخاسرة واشتدّت إذ, انتباه الحيلولة ما ذات, وحرمان المعاهدات مع دنو. عُقر إنطلاق اليابان ان فعل, دار في إبّان الأسيوي التقليدي. التي الأمور المتاخمة بـ لمّ, مدن تم الآلاف يتعلّق. أخرى فرنسية مما في, أسيا الآخر أراضي من دنو. تُصب ليبين الشهير أم هذا, أي غينيا الوزراء الأوروبي فصل, ٢٠٠٤ كثيرة واندونيسيا، ما شيء.
');

update AbstractEvent
	set 
	contact=o.contact,
	street1=o.street1,
	street2=o.street2,
	locality=o.locality,
	postalCode=o.postalCode,
	country_id=o.country_id,
	lat=o.lat,
	lng=o.lng,
	googleMapId=o.googleMapId    
from AbstractEvent a join Organisation o on a.organisation_id=o.id;

insert into Volunteer (firstName, lastName, birthDate, mailAddress, phoneNumber, accessKey, password, street1, street2, postalCode, locality, lat, lng, googleMapId, availableForConversation, availableForInterpreting, availableForSupportInStudies, availableForActivities, activities, civility_id, country_id, nationality_id, organisation_id)
values('Alaric', 'Hermant', NULL, 'v@v.v', NULL, 'V-41eed0a4-0bbb-4594-a1cf-f8ab3ff810ec', 'f2d81a260dea8a100dd517984e53c56a7523d96942a834b9cdc249bd4e8c7aa9', NULL, NULL, '75007', 'Paris', 48.85433450, 2.31340290, NULL, NULL, true, true, false, NULL, NULL, '1', NULL, NULL);

insert into MeetingRequest ( dateConstraint, reason, additionalInformations, postDate, acceptationDate, street1, street2, postalCode, locality, lat, lng, googleMapId, refugee_id, volunteer_id, country_id)
	values('Lundi 24 octobre', 'SUPPORT_IN_STUDIES', 'Malheureusement, mon portable (ne marche pas)! ', '2016-10-17 07:06:45', '2016-10-17 07:37:19', NULL, NULL, '75001', 'Paris', 48.86404930, 2.33105260, NULL, 1, 1, 1);

insert into MeetingRequest_messages (MeetingRequest_id,volunteer_id,text,direction,postedDate,readDate) 
	values (1,1,'bonjour','VOLUNTEER_TO_REFUGEE', now(), null);

insert into AbstractLearningProgram_registrations( refugee_id, AbstractLearningProgram_id) values (1,5);

