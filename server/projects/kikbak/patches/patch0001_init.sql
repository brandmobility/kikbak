/* Create Tables */

CREATE TABLE `account`
(
	id BIGINT NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(64) CHARACTER SET utf8 NOT NULL,
	last_name VARCHAR(64) CHARACTER SET utf8,
	phone_number VARCHAR(24) NOT NULL,
	email VARCHAR(128) NOT NULL,
	password VARCHAR(256) NOT NULL,
	verified TINYINT,
	account_type_id SMALLINT NOT NULL,
	create_date DATETIME NOT NULL,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;


CREATE TABLE `accounttype`
(
	id SMALLINT NOT NULL AUTO_INCREMENT,
	type VARCHAR(64) NOT NULL UNIQUE,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;

CREATE TABLE `devicetoken`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(1024) NOT NULL,
    platform_type SMALLINT NOT NULL,
    last_update_time DATETIME NOT NULL,
    last_failed_delivery DATETIME,
    PRIMARY KEY(id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;


CREATE TABLE `friend`
(
	id BIGINT(0) NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(64) CHARACTER SET utf8,
	last_name VARCHAR(64) CHARACTER SET utf8,
	facebook_id BIGINT(0),
	username VARCHAR(128) CHARACTER SET utf8,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;


CREATE TABLE `location`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    site_name VARCHAR(32) CHARACTER SET utf8,
    address_1 VARCHAR(128) CHARACTER SET utf8 NOT NULL,
    address_2 VARCHAR(128) CHARACTER SET utf8,
    city VARCHAR(128) CHARACTER SET utf8 NOT NULL,
    state VARCHAR(24) CHARACTER SET utf8 NOT NULL,
    zipcode INT NOT NULL,
    zip_plus_four VARCHAR(4) CHARACTER SET utf8,
    phone_number BIGINT NOT NULL,
    verification_code VARCHAR(8) NOT NULL,
    merchant_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    status varchar(16) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;


CREATE TABLE `credit`
(
	id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	merchant_id BIGINT NOT NULL,
	location_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	offer_id BIGINT NOT NULL,
	kikbak_id BIGINT NOT NULL,
	reward_type VARCHAR (16) CHARACTER SET utf8 NOT NULL,
	begin_date DATETIME NOT NULL,
	end_date DATETIME NOT NULL,
	value DOUBLE NOT NULL,
    redeem_count INT DEFAULT 0,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;

CREATE TABLE `allocatedgift`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    offer_id BIGINT NOT NULL,
    gift_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    friend_user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    shared_id BIGINT NOT NULL,
    value DOUBLE NOT NULL,
    redemption_date DATETIME,
    expiration_date DATETIME NOT NULL,
    PRIMARY KEY(id)
)ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;


CREATE TABLE `merchant`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) CHARACTER SET utf8 NOT NULL,
    description VARCHAR(4096) CHARACTER SET utf8,
    url VARCHAR(256),
    image_url VARCHAR(256),
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;


CREATE TABLE `gift`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    offer_id BIGINT NOT NULL,
    description VARCHAR(20) CHARACTER SET utf8 NOT NULL,
    detailed_desc VARCHAR(40) CHARACTER SET utf8 NOT NULL,
    value DOUBLE NOT NULL,
    discount_type VARCHAR (16) CHARACTER SET utf8 NOT NULL,
    redemption_location_type VARCHAR(16) CHARACTER SET utf8 NOT NULL,
    validation_type VARCHAR(16) CHARACTER SET utf8 NOT NULL,
    image_url VARCHAR(256) NOT NULL,
    default_give_image_url VARCHAR(256) NOT NULL,
    notification_text VARCHAR(200) CHARACTER SET utf8 NOT NULL,
    PRIMARY KEY (id)
)ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;


CREATE TABLE `kikbak`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    offer_id BIGINT NOT NULL,
    description VARCHAR(20) CHARACTER SET utf8 NOT NULL,
    detailed_desc VARCHAR(40) CHARACTER SET utf8 NOT NULL,
    value DOUBLE NOT NULL,
    reward_type VARCHAR (16) CHARACTER SET utf8 NOT NULL,
    validation_type VARCHAR(16) CHARACTER SET utf8 NOT NULL,
    image_url VARCHAR(256) NOT NULL,
    notification_text VARCHAR(200) CHARACTER SET utf8 NOT NULL,
    PRIMARY KEY (id)
)ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;

CREATE TABLE `offer`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(64) CHARACTER SET utf8 NOT NULL,
    image_url VARCHAR(256) NOT NULL,
    tos_url VARCHAR(2048) NOT NULL,
    redeem_limit INT DEFAULT 0,
    begin_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;

CREATE TABLE `barcode`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    offer_id BIGINT NOT NULL,
    gift_id BIGINT NOT NULL,
    user_id BIGINT,
    allocated_gift_id BIGINT,
    code VARCHAR(16) NOT NULL,
    value DOUBLE NOT NULL,
    association_date DATETIME,
    begin_date DATETIME NOT NULL,
    expiration_date DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;

create TABLE `claim`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    kikbak_id BIGINT NOT NULL,
    offer_id BIGINT NOT NULL,
    phone_number VARCHAR(24) NOT NULL,
    name VARCHAR(32) CHARACTER SET utf8 NOT NULL,
    street VARCHAR(32) CHARACTER SET utf8 NOT NULL,
    apt VARCHAR(8) CHARACTER SET utf8,
    city VARCHAR(24) CHARACTER SET utf8 NOT NULL,
    state VARCHAR(16) CHARACTER SET utf8 NOT NULL,
    zipcode VARCHAR(6) CHARACTER SET utf8 NOT NULL,
    request_date DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;

CREATE TABLE `shared`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    offer_id BIGINT NOT NULL,
    employee_id VARCHAR(32) CHARACTER SET utf8,
    type VARCHAR(16) CHARACTER SET utf8 NOT NULL,
    image_url VARCHAR(256),
    shared_date DATETIME NOT NULL,
    referral_code VARCHAR(12) NOT NULL,
    caption VARCHAR(1024) CHARACTER SET utf8,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;


CREATE TABLE `transaction`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    offer_id BIGINT NOT NULL,
    credit_id BIGINT NOT NULL,
    transaction_type SMALLINT NOT NULL,
    amount DOUBLE NOT NULL,
    date DATETIME NOT NULL,
    merchant_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    verification_code VARCHAR(8) NOT NULL,
    authorization_code VARCHAR(8) NOT NULL,
    PRIMARY KEY(id)
)ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;


CREATE TABLE `user`
(
	id BIGINT NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(64) CHARACTER SET utf8,
	last_name VARCHAR(64) CHARACTER SET utf8,
	email VARCHAR(128) NOT NULL,
	facebook_id BIGINT(0) NOT NULL UNIQUE,
	gender TINYINT NOT NULL,
	create_date DATETIME NOT NULL,
	update_date DATETIME,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;

CREATE TABLE `userToken`
(
	id BIGINT NOT NULL AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	token VARCHAR(32) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE KEY (user_id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;


CREATE TABLE `user2friend`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	facebook_friend_id BIGINT NOT NULL,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_bin;



/* Create Indexes */

CREATE INDEX email_password_key ON `account` (email ASC, password ASC);

CREATE INDEX user_id_key USING BTREE ON `devicetoken` (user_id ASC);
CREATE INDEX last_failed_delivery_key USING BTREE on `devicetoken` (last_failed_delivery ASC);


CREATE INDEX facebook_id_key USING BTREE ON `friend` (facebook_id ASC);

CREATE INDEX offer_id_key ON `allocatedgift` (offer_id ASC);
CREATE INDEX gift_id_key ON `allocatedgift` (gift_id ASC);
CREATE INDEX user_id_key ON `allocatedgift` (user_id ASC);
CREATE INDEX friend_user_id_key ON `allocatedgift` (friend_user_id ASC);
CREATE INDEX merchant_id_key ON `allocatedgift` (merchant_id ASC);
CREATE INDEX shared_id_key on `allocatedgift` (shared_id ASC);

CREATE INDEX date_range_key USING BTREE ON `credit` (begin_date ASC, end_date ASC);
CREATE INDEX merchant_id_key ON `credit` (merchant_id ASC);
CREATE INDEX location_id_key ON `credit` (location_id ASC);
CREATE INDEX offer_id_key ON `credit` (offer_id ASC);
CREATE INDEX user_id_key ON `credit` (user_id ASC);
CREATE INDEX kikbak_id_key ON `credit` (kikbak_id ASC);

CREATE INDEX offer_id_key ON `gift` (offer_id ASC);
CREATE INDEX offer_id_key ON `kikbak` (offer_id ASC);

CREATE INDEX code_key ON `barcode` (code ASC);
CREATE INDEX offer_key ON `barcode` (offer_id ASC);
CREATE INDEX user_key ON `barcode` (user_id ASC);
CREATE INDEX merchant_gift_id_Key ON `barcode` (merchant_id ASC, gift_id ASC);
CREATE INDEX gift_allocated_gift_key ON `barcode` (gift_id ASC, allocated_gift_id ASC, begin_date ASC, expiration_date ASC);
create unique index compound_index on `barcode` (merchant_id ASC, code);

CREATE INDEX user_id_key ON `claim` (user_id ASC);
CREATE INDEX offer_id_key ON `claim` (offer_id ASC);
CREATE INDEX kikbak_id_key ON `claim` (kikbak_id ASC);

CREATE INDEX merchant_id_key ON `location` (merchant_id ASC);
CREATE INDEX geo_key ON `location` (latitude ASC, longitude ASC);
CREATE INDEX zipcode_key ON `location` (zipcode ASC);

CREATE INDEX name_key USING BTREE ON `merchant` (name ASC);

CREATE INDEX valid_offer_key ON `offer` (merchant_id ASC, begin_date ASC, end_date ASC);
CREATE INDEX gift_create_key ON `offer` (begin_date ASC, end_date ASC, id ASC);

CREATE INDEX merchant_id_key USING BTREE ON `shared` (merchant_id ASC);
CREATE INDEX location_id_key USING BTREE ON `shared` (location_id ASC);
CREATE INDEX offer_id_key USING BTREE ON `shared` (offer_id ASC);
CREATE INDEX user_id_offer_id_key USING BTREE ON `shared` (user_id ASC, offer_id ASC);
CREATE UNIQUE INDEX referral_code_key USING BTREE ON `shared` (referral_code);

CREATE INDEX user_id_key ON `transaction` (user_id ASC);
CREATE INDEX offer_id_key ON `transaction` (offer_id ASC);
CREATE INDEX credit_id_key ON `transaction` (credit_id ASC);
CREATE INDEX merchant_id_key ON `transaction` (merchant_id ASC);
CREATE INDEX location_id_key ON `transaction` (location_id ASC);


CREATE INDEX facebook_id_key USING BTREE ON `user` (facebook_id ASC);
CREATE INDEX email_key USING BTREE ON `user` (email ASC);
CREATE INDEX create_date_key ON `user` (create_date ASC);
CREATE INDEX gender_key ON `user` (gender ASC);

CREATE INDEX facebook_id_key USING BTREE ON `user2friend` (facebook_friend_id ASC);
create unique index compound_index on `user2friend` (user_id ASC, facebook_friend_id);
