create table lib_library (
    id bigint(10) auto_increment primary key,
	ooid mediumtext,
	data mediumblob,
	userid int(10)
) charset=utf8 engine=InnoDB;
create table lib_groups (
    id bigint(10) auto_increment primary key,
	groupid mediumtext,
	name mediumtext,
	referencesystemid mediumtext,
	referencesystem varchar(200),
	userid int(10),
	types mediumtext
) charset=utf8 engine=InnoDB;