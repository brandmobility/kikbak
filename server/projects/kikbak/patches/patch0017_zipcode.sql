create table zipcode (
  id BIGINT NOT NULL AUTO_INCREMENT,
  merchant_id BIGINT NOT NULL,
  zipcode INT NOT NULL,
  PRIMARY KEY(id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;

create index zipcode_key ON zipcode (zipcode ASC);

