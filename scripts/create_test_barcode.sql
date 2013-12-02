drop procedure if exists create_test_barcode;

DELIMITER $$
create procedure create_test_barcode(in num INT,
                                     in merchant_id INT,
                                     in offer_id INT,
                                     in gift_id INT,
                                     in value DOUBLE,
                                     in valid_days INT)
begin
declare counter int unsigned default 0;
declare code bigint unsigned default 0;
  start transaction;
  while counter < num do
    set code=FLOOR(10000000000 + RAND() * (99999999999-10000000000));
    insert into barcode (merchant_id, offer_id,  gift_id, code, value, valid_days, begin_date, expiration_date) values
                        (merchant_id, offer_id,  gift_id, code, value, valid_days, now(),      now() + interval 90 day);
    set counter=counter+1;
  end while;
  commit;
end $$

delimiter ;


