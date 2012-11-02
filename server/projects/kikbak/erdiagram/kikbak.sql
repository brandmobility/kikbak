




/* Create Tables */

CREATE TABLE BRANCH
(
	ID BIGINT NOT NULL AUTO_INCREMENT,
	ADDRESS_1 VARCHAR(256) NOT NULL,
	ADDRESS_2 VARCHAR(256),
	CITY VARCHAR(128) NOT NULL,
	STATE VARCHAR(24) NOT NULL,
	ZIPCODE VARCHAR(6) NOT NULL,
	ZIP_PLUS_FOUR VARCHAR(4),
	MERCHANT_ID BIGINT NOT NULL,
	LATITUDE DOUBLE NOT NULL,
	LONGITUDE DOUBLE NOT NULL,
	PRIMARY KEY (ID, STATE)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE FRIEND
(
	ID BIGINT(0) NOT NULL AUTO_INCREMENT,
	FIRSTNAME VARCHAR(64),
	LASTNAME VARCHAR(64),
	FACEBOOK_ID BIGINT(0),
	USERNAME VARCHAR(64),
	PRIMARY KEY (ID)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE KIKBAK
(
	ID BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	MERCHANT_ID BIGINT NOT NULL,
	LOCATION_ID BIGINT NOT NULL,
	USER_ID BIGINT NOT NULL,
	REWARD_ID BIGINT NOT NULL,
	MULTIPLIER_ID BIGINT NOT NULL,
	START_DATE DATETIME NOT NULL,
	EXPIRATION_DATE DATETIME NOT NULL,
	REDEEMED_DATE DATETIME,
	PRIMARY KEY (ID)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE MERCHANT
(
	ID BIGINT NOT NULL AUTO_INCREMENT,
	NAME VARCHAR(64) NOT NULL,
	MERCHANT_TYPE BIGINT(0) NOT NULL,
	DESCRIPTION VARCHAR(1024),
	GRAPH_PATH VARCHAR(512) NOT NULL,
	PRIMARY KEY (ID)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE REWARD
(
	ID BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	MERCHANT_ID BIGINT NOT NULL,
	LOCATION_ID BIGINT NOT NULL,
	NAME VARCHAR(64) NOT NULL,
	DESCRIPTION VARCHAR(4096) NOT NULL,
	VALUE DOUBLE NOT NULL,
	REWARD_TYPE INT NOT NULL,
	PRIMARY KEY (ID)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE REWARDMULTIPLIER
(
	ID BIGINT NOT NULL AUTO_INCREMENT,
	MULTIPLIER DOUBLE,
	PRIMARY KEY (ID)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE SHARING
(
	ID BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	FACEBOOK_ID BIGINT NOT NULL UNIQUE,
	LOCATION_ID BIGINT NOT NULL,
	GRAPH_PATH VARCHAR(512) NOT NULL,
	PRIMARY KEY (ID)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE USER2FRIEND
(
	USER_ID BIGINT NOT NULL,
	FRIEND_ID BIGINT(0) NOT NULL,
	FACEBOOK_FRIEND_ID BIGINT NOT NULL
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE USERS
(
	ID BIGINT NOT NULL AUTO_INCREMENT,
	FIRSTNAME VARCHAR(64),
	LASTNAME VARCHAR(64),
	EMAIL VARCHAR(128),
	FACEBOOK_ID BIGINT(0),
	GENDER TINYINT,
	CREATE_DATE DATETIME NOT NULL,
	UPDATE_DATE DATETIME,
	PRIMARY KEY (ID)
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;



/* Create Indexes */

CREATE INDEX MERCHANT_ID_KEY ON BRANCH (MERCHANT_ID ASC);
CREATE INDEX GEO_KEY ON BRANCH (LATITUDE ASC, LONGITUDE ASC);
CREATE INDEX ZIPCODE_KEY ON BRANCH (ZIPCODE ASC);
CREATE INDEX FACEBOOK_ID_KEY USING BTREE ON FRIEND (FACEBOOK_ID ASC);
CREATE INDEX DATE_RANGE_KEY USING BTREE ON KIKBAK (START_DATE ASC, EXPIRATION_DATE ASC);
CREATE INDEX REDEEMED_DATE_KEY ON KIKBAK (REDEEMED_DATE ASC);
CREATE INDEX MERCHANT_ID_LEY ON KIKBAK (MERCHANT_ID ASC);
CREATE INDEX LOCATION_ID_KEY ON KIKBAK (LOCATION_ID ASC);
CREATE INDEX USER_ID_KEY ON KIKBAK (USER_ID ASC);
CREATE INDEX NAME_KEY USING BTREE ON MERCHANT (NAME ASC);
CREATE INDEX MERCHANT_TYPE_KEY USING BTREE ON MERCHANT (MERCHANT_TYPE ASC);
CREATE INDEX MERCHANT_ID_KEY USING BTREE ON REWARD (MERCHANT_ID ASC);
CREATE INDEX LOCATION_ID_KEY ON REWARD (LOCATION_ID ASC);
CREATE INDEX REWARD_TYPE_KEY ON REWARD (REWARD_TYPE ASC);
CREATE INDEX FACEBOOK_ID_KEY USING BTREE ON SHARING (FACEBOOK_ID ASC);
CREATE INDEX LOCATION_ID_KEY USING BTREE ON SHARING (LOCATION_ID ASC);
CREATE INDEX USER_ID_KEY USING BTREE ON USER2FRIEND (USER_ID ASC, FACEBOOK_FRIEND_ID ASC);
CREATE INDEX FACEBOOK_ID_KE USING BTREE ON USERS (FACEBOOK_ID ASC);
CREATE INDEX EMAIL_KEY USING BTREE ON USERS (EMAIL ASC);
CREATE INDEX CREATE_DATE_KEY ON USERS (CREATE_DATE ASC);
CREATE INDEX GENDER_KEY ON USERS (GENDER ASC);



