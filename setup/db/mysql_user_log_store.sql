start transaction;

-- create tables

create table ula_logsession(
	sessionid varchar(255) primary key,
	login varchar(100),
	start datetime,
	stop datetime,
	type varchar(100)
) charset=utf8 engine = innodb;

create table ula_logevent(
	eventid int not null auto_increment primary key,
	sessionid varchar(300),
	sequencenumber int,
	eventtimestamp datetime,
	eventtimestampms bigint,
	eventlocaltimestamp datetime,
	eventlocaltimestampms bigint,
	name varchar(100)
) charset=utf8 engine = innodb;

create table ula_logeventparams(
	eventid int,
	paramname varchar(100),
	paramvalue mediumtext,
	sequence int,
	primary key (eventid, paramname, sequence)
) charset=utf8 engine = innodb;

create table ula_logeventconceptmap(
	eventname varchar(100),
	conceptlevel varchar(100),
	primary key (eventname, conceptlevel)
) charset=utf8 engine = innodb;


-- insert event ontology 

-- to do

commit;