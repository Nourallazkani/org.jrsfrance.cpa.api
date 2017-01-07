delete from abstractevent_registrations;
delete from abstractevent;
delete from abstractlearningProgram_registrations;
delete from abstractlearningProgram;
delete from teaching_registrations;
delete from teaching;
delete from meetingRequest_messages;
delete from meetingRequest_volunteer;
delete from meetingRequest;

delete from volunteer_fieldOfStudy;
delete from volunteer_language;
delete from volunteer;

delete from refugee_language;
delete from refugee;

delete from organisation;
delete from administrator;

delete from civility;
delete from country;
delete from eventtype;
delete from fieldOfStudy;
delete from language;
delete from languagelearningProgramType;
delete from level;
delete from organisationcategory;
delete from professionallearningprogramdomain;


alter sequence abstractevent_id_seq restart with 1;
alter sequence abstractlearningProgram_id_seq restart with 1;
alter sequence teaching_id_seq restart with 1;
alter sequence meetingRequest_id_seq restart with 1;

alter sequence volunteer_id_seq restart with 1;
alter sequence refugee_id_seq restart with 1;
alter sequence organisation_id_seq restart with 1;
alter sequence administrator_id_seq restart with 1;

alter sequence civility_id_seq restart with 1;
alter sequence country_id_seq restart with 1;
alter sequence eventtype_id_seq restart with 1;
alter sequence fieldOfStudy_id_seq restart with 1;
alter sequence language_id_seq restart with 1;
alter sequence languagelearningProgramType_id_seq restart with 1;
alter sequence level_id_seq restart with 1;
alter sequence organisationcategory_id_seq restart with 1;
alter sequence professionallearningprogramdomain_id_seq restart with 1;

