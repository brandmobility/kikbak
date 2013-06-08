/* Create Tables */

CREATE TABLE `account`
(
	id BIGINT NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(64) NOT NULL,
	last_name VARCHAR(64),
	phone_number VARCHAR(24) NOT NULL,
	email VARCHAR(128) NOT NULL,
	password VARCHAR(256) NOT NULL,
	verified TINYINT,
	account_type_id SMALLINT NOT NULL,
	create_date DATETIME NOT NULL,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `accounttype`
(
	id SMALLINT NOT NULL AUTO_INCREMENT,
	type VARCHAR(64) NOT NULL UNIQUE,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE TABLE `devicetoken`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(1024) NOT NULL,
    platform_type SMALLINT NOT NULL,
    last_update_time DATETIME NOT NULL,
    last_failed_delivery DATETIME,
    PRIMARY KEY(id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `friend`
(
	id BIGINT(0) NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(64),
	last_name VARCHAR(64),
	facebook_id BIGINT(0),
	username VARCHAR(128),
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `gift`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    offer_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    friend_user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    shared_id BIGINT NOT NULL,
    value DOUBLE NOT NULL,
    redemption_date DATETIME,
    experiration_date DATETIME NOT NULL,
    PRIMARY KEY(id)
)ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE TABLE `location`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    address_1 VARCHAR(256) NOT NULL,
    address_2 VARCHAR(256),
    city VARCHAR(128) NOT NULL,
    state VARCHAR(24) NOT NULL,
    zipcode INT NOT NULL,
    zip_plus_four VARCHAR(4),
    phone_number BIGINT NOT NULL,
    verification_code VARCHAR(8) NOT NULL,
    merchant_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `kikbak`
(
	id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	merchant_id BIGINT NOT NULL,
	location_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	offer_id BIGINT NOT NULL,
	begin_date DATETIME NOT NULL,
	end_date DATETIME NOT NULL,
	value DOUBLE NOT NULL,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `merchant`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(4096),
    url VARCHAR(256),
    image_url VARCHAR(512),
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `offer`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(4096) NOT NULL,
    default_text VARCHAR(512) NOT NULL,
    kikbak_name VARCHAR(64) NOT NULL,
    kikbak_description VARCHAR(4096) NOT NULL,
    kikbak_value DOUBLE NOT NULL,
    kikbak_notification_text VARCHAR(200) NOT NULL,
    gift_name VARCHAR(64) NOT NULL,
    gift_description VARCHAR(4096) NOT NULL,    
    gift_value DOUBLE NOT NULL,
    gift_notification_text VARCHAR(200) NOT NULL,
    terms_of_service VARCHAR(4096) NOT NULL,
    begin_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `shared`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    offer_id BIGINT NOT NULL,
    fb_image_id BIGINT NOT NULL,
    shared_date DATETIME NOT NULL,
    caption VARCHAR(4096),
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `transaction`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    offer_id BIGINT NOT NULL,
    kikbak_id BIGINT NOT NULL,
    transaction_type SMALLINT NOT NULL,
    amount DOUBLE NOT NULL,
    date DATETIME NOT NULL,
    merchant_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    verification_code VARCHAR(8) NOT NULL,
    authorization_code VARCHAR(8) NOT NULL,
    PRIMARY KEY(id)
)ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `user`
(
	id BIGINT NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(64),
	last_name VARCHAR(64),
	username VARCHAR(128) NOT NULL,
	email VARCHAR(128) NOT NULL,
	facebook_id BIGINT(0) NOT NULL UNIQUE,
	gender TINYINT NOT NULL,
	create_date DATETIME NOT NULL,
	update_date DATETIME,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `user2friend`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	facebook_friend_id BIGINT NOT NULL,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;



/* Create Indexes */

CREATE INDEX email_key ON `account` (email ASC);
CREATE INDEX email_password_key ON `account` (email ASC, password ASC);

CREATE INDEX user_id_key USING BTREE ON `devicetoken` (user_id ASC);
CREATE INDEX last_failed_delivery_key USING BTREE on `devicetoken` (last_failed_delivery ASC);


CREATE INDEX facebook_id_key USING BTREE ON `friend` (facebook_id ASC);

CREATE INDEX offer_id_key ON `gift` (offer_id ASC);
CREATE INDEX user_id_key ON `gift` (user_id ASC);
CREATE INDEX friend_user_id_key ON `gift` (friend_user_id ASC);
CREATE INDEX merchant_id_key ON `gift` (merchant_id ASC);
CREATE INDEX shared_id_key on `gift` (shared_id ASC);


CREATE INDEX date_range_key USING BTREE ON `kikbak` (begin_date ASC, end_date ASC);
CREATE INDEX merchant_id_key ON `kikbak` (merchant_id ASC);
CREATE INDEX location_id_key ON `kikbak` (location_id ASC);
CREATE INDEX offer_id_key ON `kikbak` (offer_id ASC);
CREATE INDEX user_id_key ON `kikbak` (user_id ASC);


CREATE INDEX merchant_id_key ON `location` (merchant_id ASC);
CREATE INDEX geo_key ON `location` (latitude ASC, longitude ASC);
CREATE INDEX zipcode_key ON `location` (zipcode ASC);

CREATE INDEX name_key USING BTREE ON `merchant` (name ASC);

CREATE INDEX merchant_id_key USING BTREE ON `offer` (merchant_id ASC);
CREATE INDEX valid_offer_key ON `offer` (merchant_id ASC, begin_date ASC, end_date ASC);
CREATE INDEX gift_create_key ON `offer` (begin_date ASC, end_date ASC, id ASC);

CREATE INDEX user_id_key USING BTREE ON `shared` (user_id ASC);
CREATE INDEX merchant_id_key USING BTREE ON `shared` (merchant_id ASC);
CREATE INDEX location_id_key USING BTREE ON `shared` (location_id ASC);
CREATE INDEX offer_id_key USING BTREE ON `shared` (offer_id ASC);
CREATE INDEX user_id_offer_id_key USING BTREE ON `shared` (user_id ASC, offer_id ASC);


CREATE INDEX user_id_key ON `transaction` (user_id ASC);
CREATE INDEX offer_id_key ON `transaction` (offer_id ASC);
CREATE INDEX kikbak_id_key ON `transaction` (kikbak_id ASC);
CREATE INDEX merchant_id_key ON `transaction` (merchant_id ASC);
CREATE INDEX location_id_key ON `transaction` (location_id ASC);


CREATE INDEX facebook_id_key USING BTREE ON `user` (facebook_id ASC);
CREATE INDEX email_key USING BTREE ON `user` (email ASC);
CREATE INDEX create_date_key ON `user` (create_date ASC);
CREATE INDEX gender_key ON `user` (gender ASC);

CREATE INDEX user_id_key USING BTREE ON `user2friend` (user_id ASC);
CREATE INDEX facebook_id_key USING BTREE ON `user2friend` (facebook_friend_id ASC);
create unique index compound_index on `user2friend` (user_id ASC, facebook_friend_id);
