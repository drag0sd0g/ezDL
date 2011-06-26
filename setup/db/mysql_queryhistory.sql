create table qha_queryhistory (
    id int(10) auto_increment primary key,
	query mediumtext,
	timestamp datetime,
	userid int(10)
) charset=utf8 engine=InnoDB;