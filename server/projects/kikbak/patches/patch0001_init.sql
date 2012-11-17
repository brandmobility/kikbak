/* Create Tables */

CREATE TABLE `Account`
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


CREATE TABLE `AccountType`
(
	id SMALLINT NOT NULL AUTO_INCREMENT,
	type VARCHAR(64) NOT NULL UNIQUE,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `Friend`
(
	id BIGINT(0) NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(64),
	last_name VARCHAR(64),
	facebook_id BIGINT(0),
	username VARCHAR(64),
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `Kikbak`
(
	id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	merchant_id BIGINT NOT NULL,
	location_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	reward_id BIGINT NOT NULL,
	begin_date DATETIME NOT NULL,
	end_date DATETIME NOT NULL,
	redeemed_date DATETIME,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `User`
(
	id BIGINT NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(64),
	last_name VARCHAR(64),
	username VARCHAR(128) NOT NULL,
	email VARCHAR(128) NOT NULL,
	facebook_id BIGINT(0) NOT NULL,
	gender TINYINT NOT NULL,
	create_date DATETIME NOT NULL,
	update_date DATETIME,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `User2Friend`
(
	user_id BIGINT NOT NULL,
	friend_id BIGINT(0) NOT NULL,
	facebook_friend_id BIGINT NOT NULL
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `Location`
(
	id BIGINT NOT NULL AUTO_INCREMENT,
	address_1 VARCHAR(256) NOT NULL,
	address_2 VARCHAR(256),
	city VARCHAR(128) NOT NULL,
	state VARCHAR(24) NOT NULL,
	zipcode INT NOT NULL,
	zip_plus_four VARCHAR(4),
	verification_code VARCHAR(8) NOT NULL,
	merchant_id BIGINT NOT NULL,
	latitude DOUBLE NOT NULL,
	longitude DOUBLE NOT NULL,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `Merchant`
(
	id BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(64) NOT NULL,
	description VARCHAR(1024),
	merchant_type BIGINT(0) NOT NULL,
	graph_path VARCHAR(512) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE TABLE `MerchantType`
(
    id SMALLINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(64) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE TABLE `Offer`
(
	id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	merchant_id BIGINT NOT NULL,
	location_id BIGINT,
	name VARCHAR(64) NOT NULL,
	description VARCHAR(4096) NOT NULL,
	sharer_value DOUBLE NOT NULL,
	sharee_value DOUBLE NOT NULL,
	begin_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `Sharing`
(
	id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	facebook_id BIGINT NOT NULL UNIQUE,
	location_id BIGINT NOT NULL,
	graph_path VARCHAR(512) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;



/* Create Indexes */

CREATE INDEX email_key ON `Account` (email ASC);
CREATE INDEX email_password_key ON `Account` (email ASC, password ASC);
CREATE INDEX facebook_id_key USING BTREE ON `Friend` (facebook_id ASC);
CREATE INDEX date_range_key USING BTREE ON `Kikbak` (begin_date ASC, end_date ASC);
CREATE INDEX redeemed_date_key ON `Kikbak` (redeemed_date ASC);
CREATE INDEX merchant_id_key ON `Kikbak` (merchant_id ASC);
CREATE INDEX location_id_key ON `Kikbak` (location_id ASC);
CREATE INDEX user_id_key ON `Kikbak` (user_id ASC);
CREATE INDEX facebook_id_key USING BTREE ON `User` (facebook_id ASC);
CREATE INDEX email_key USING BTREE ON `User` (email ASC);
CREATE INDEX create_date_key ON `User` (create_date ASC);
CREATE INDEX gender_key ON `User` (gender ASC);
CREATE INDEX user_id_key USING BTREE ON `User2Friend` (user_id ASC, facebook_friend_id ASC);
CREATE INDEX merchant_id_key ON `Location` (merchant_id ASC);
CREATE INDEX geo_key ON `Location` (latitude ASC, longitude ASC);
CREATE INDEX zipcode_key ON `Location` (zipcode ASC);
CREATE INDEX name_key USING BTREE ON `Merchant` (name ASC);
CREATE INDEX merchant_type_key USING BTREE ON `Merchant` (merchant_type ASC);
CREATE INDEX merchant_id_key USING BTREE ON `Offer` (merchant_id ASC);
CREATE INDEX location_id_key ON `Offer` (location_id ASC);
CREATE INDEX valid_reward_key ON `Offer` (merchant_id ASC, begin_date ASC, end_date ASC);
CREATE INDEX facebook_id_key USING BTREE ON `Sharing` (facebook_id ASC);
CREATE INDEX location_id_key USING BTREE ON `Sharing` (location_id ASC);



