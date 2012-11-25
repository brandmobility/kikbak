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
	username VARCHAR(128),
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `Kikbak`
(
	id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	merchant_id BIGINT NOT NULL,
	location_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	offer_id BIGINT NOT NULL,
	begin_date DATETIME NOT NULL,
	end_date DATETIME NOT NULL,
	redeemed_date DATETIME,
	value DOUBLE NOT NULL,
	PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE TABLE `Gift`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    offer_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    friend_user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    times_used SMALLINT NOT NULL,
    value DOUBLE NOT NULL,
    experiration_date DATETIME NOT NULL,
    PRIMARY KEY(id)
)ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE `Transaction`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    offer_id BIGINT NOT NULL,
    kikbak_id BIGINT NOT NULL,
    transaction_type SMALLINT NOT NULL,
    amount BIGINT NOT NULL,
    date DATETIME NOT NULL,
    merchant_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    verification_code VARCHAR(8) NOT NULL,
    authorization_code VARCHAR(8) NOT NULL,
    PRIMARY KEY(id)
)ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


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
	description VARCHAR(4096),
	url VARCHAR(256),
	image_url VARCHAR(512),
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
	name VARCHAR(64) NOT NULL,
	description VARCHAR(4096) NOT NULL,
	default_text VARCHAR(512) NOT NULL,
	sharer_value DOUBLE NOT NULL,
	sharee_value DOUBLE NOT NULL,
	sharee_repeat_value DOUBLE NOT NULL,
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


CREATE TABLE `DeviceToken`
(
    id BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(1024) NOT NULL,
    platform_type SMALLINT NOT NULL,
    last_update_time DATETIME NOT NULL,
    last_failed_delivery DATETIME,
    PRIMARY KEY(id)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

/* Create Indexes */

CREATE INDEX email_key ON `Account` (email ASC);
CREATE INDEX email_password_key ON `Account` (email ASC, password ASC);

CREATE INDEX facebook_id_key USING BTREE ON `Friend` (facebook_id ASC);

CREATE INDEX date_range_key USING BTREE ON `Kikbak` (begin_date ASC, end_date ASC);
CREATE INDEX redeemed_date_key ON `Kikbak` (redeemed_date ASC);
CREATE INDEX merchant_id_key ON `Kikbak` (merchant_id ASC);
CREATE INDEX location_id_key ON `Kikbak` (location_id ASC);
CREATE INDEX offer_id_key ON `Kikbak` (offer_id ASC);
CREATE INDEX user_id_key ON `Kikbak` (user_id ASC);

CREATE INDEX offer_id_key ON `Gift` (offer_id ASC);
CREATE INDEX user_id_key ON `Gift` (user_id ASC);
CREATE INDEX friend_user_id_key ON `Gift` (friend_user_id ASC);
CREATE INDEX merchant_id_key ON `Gift` (merchant_id ASC);

CREATE INDEX offer_id_key ON `Transaction` (offer_id ASC);
CREATE INDEX kikbak_id_key ON `Transaction` (kikbak_id ASC);
CREATE INDEX merchant_id_key ON `Transaction` (merchant_id ASC);
CREATE INDEX location_id_key ON `Transaction` (location_id ASC);

CREATE INDEX facebook_id_key USING BTREE ON `User` (facebook_id ASC);
CREATE INDEX email_key USING BTREE ON `User` (email ASC);
CREATE INDEX create_date_key ON `User` (create_date ASC);
CREATE INDEX gender_key ON `User` (gender ASC);

CREATE INDEX user_id_key USING BTREE ON `User2Friend` (user_id ASC, facebook_friend_id ASC);

CREATE INDEX merchant_id_key ON `Location` (merchant_id ASC);
CREATE INDEX geo_key ON `Location` (latitude ASC, longitude ASC);
CREATE INDEX zipcode_key ON `Location` (zipcode ASC);

CREATE INDEX name_key USING BTREE ON `Merchant` (name ASC);

CREATE INDEX merchant_id_key USING BTREE ON `Offer` (merchant_id ASC);
CREATE INDEX valid_offer_key ON `Offer` (merchant_id ASC, begin_date ASC, end_date ASC);

CREATE INDEX facebook_id_key USING BTREE ON `Sharing` (facebook_id ASC);
CREATE INDEX location_id_key USING BTREE ON `Sharing` (location_id ASC);

CREATE INDEX user_id_key USING BTREE ON `DeviceToken` (user_id ASC);
CREATE INDEX last_failed_delivery_key USING BTREE on `DeviceToken` (last_failed_delivery ASC);

