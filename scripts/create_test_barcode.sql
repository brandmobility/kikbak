# 12-digit UPC-A with correct checksum

drop function if exists random_barcode;
drop procedure if exists create_test_barcode;

DELIMITER $$

create function random_barcode() returns text
begin
	declare code bigint unsigned default 0;
	declare a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11 int unsigned default 0;
	declare control int unsigned default 0;
	set code=FLOOR(10000000000 + RAND() * (99999999999-10000000000));
	set a1=substr(code,1,1);
	set a2=substr(code,2,1);
	set a3=substr(code,3,1);
	set a4=substr(code,4,1);
	set a5=substr(code,5,1);
	set a6=substr(code,6,1);
	set a7=substr(code,7,1);
	set a8=substr(code,8,1);
	set a9=substr(code,9,1);
	set a10=substr(code,10,1);
	set a11=substr(code,11,1);
	set control=3*(a1+a3+a5+a7+a9+a11)+(a2+a4+a6+a8+a10);
	set control=(10-(control%10))%10;
	return concat(code,control);
end $$

create procedure create_test_barcode(in num INT,
                                     in merchant_id INT,
                                     in offer_id INT,
                                     in gift_id INT,
                                     in value DOUBLE,
                                     in valid_days INT)
begin
declare counter int unsigned default 0;
  start transaction;
  while counter < num do
    insert into barcode (merchant_id, offer_id,  gift_id, code,             value, valid_days, begin_date, expiration_date) values
                        (merchant_id, offer_id,  gift_id, random_barcode(), value, valid_days, now(),      now() + interval 90 day);
    set counter=counter+1;
  end while;
  commit;
end $$

delimiter ;
