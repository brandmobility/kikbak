create table redeemzipcode (
  id BIGINT NOT NULL AUTO_INCREMENT,
  zipcode INT NOT NULL,
  PRIMARY KEY(id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;

create index zipcode_key ON redeemzipcode (zipcode ASC);