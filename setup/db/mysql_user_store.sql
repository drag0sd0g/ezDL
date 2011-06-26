start transaction;

-- create tables

create table ua_user(
	id int(10) not null auto_increment primary key, 
	lastname varchar(50) not null,
	firstname varchar(50) not null,
	createdate datetime not null,
	login varchar(100) unique not null,
	password varchar(40) not null
) charset=utf8 engine=innodb;

create table ua_role(
	id int(10) not null auto_increment primary key,
	name varchar(50) not null unique
) charset=utf8 engine=innodb;

create table ua_privilege(
	id int(10) not null auto_increment primary key, 
	name varchar(100) not null unique
) charset=utf8 engine=innodb;

create table ua_user2role(
	uid int(10), 
	rid int(10),
	primary key (uid, rid),
	constraint foreign key (uid) references ua_user(id),
	constraint foreign key (rid) references ua_role(id)
) charset=utf8 engine=innodb;

create table ua_role2privilege(
	rid int(10),
	pid int(10),
	primary key (rid, pid),
	constraint foreign key (rid) references ua_role(id),
	constraint foreign key (pid) references ua_privilege(id)
) charset=utf8 engine=innodb;

create table ua_sessionhistory(
	id int(10) not null auto_increment primary key, 
	sessionid varchar(255) not null unique key,
	uid int(10),
	timestamp datetime not null,
	constraint foreign key (uid) references ua_user(id)
) charset=utf8 engine=innodb;


-- insert user data

insert into ua_user (lastname, firstname, login, password, createdate) values ('visitor', 'visitor', 'visitor', sha1('visitor'), now());
insert into ua_user (lastname, firstname, login, password, createdate) values ('admin', 'admin', 'admin', sha1('admin_38*'), now());

-- insert role/privilege data  

insert into ua_role (Name) VALUES('admin');
set @rid = last_insert_id();
insert into ua_privilege (Name) VALUES('ADMINISTER_USERS');
set @pid = last_insert_id();
insert into ua_role2privilege (rid, pid) VALUES (@rid, @pid);

commit;