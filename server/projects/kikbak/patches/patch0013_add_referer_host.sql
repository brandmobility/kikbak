create table landingHost (
  id bigint not null auto_increment,
  referer_host varchar(255) not null,
  count bigint default 0,
  block tinyint(1) default 0,
  primary key (id),
  unique key hostname (referer_host)
) engine = InnoDB default character set latin1 collate latin1_bin;

