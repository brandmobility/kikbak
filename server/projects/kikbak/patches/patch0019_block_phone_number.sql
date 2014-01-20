create table blockednumber (
  id BIGINT NOT NULL AUTO_INCREMENT,
  offer_id BIGINT NOT NULL,
  phone_number varchar(64) NOT NULL,
  PRIMARY KEY(id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;

create index offer_id_key ON blockednumber (offer_id ASC);
create index phone_number_key ON blockednumber (phone_number ASC);