#set @server="https://m.kikbak.me";
set @server="https://test.kikbak.me";

#NancyBee

insert into merchant (name, shortname, description, url, image_url) values 
                     ('Nancy Bee Salon', 'nancybee', 'salon','http://nancybeesalonandspa.com/', '');

set @id := (select LAST_INSERT_ID());

insert into location (address_1, city, state, zipcode, phone_number, verification_code, merchant_id, latitude, longitude) values
                     ('3666 El Camino Real', 'Palo Alto', 'CA', '94306', 6504248490, '24rl(bee', @id, 37.418033, -122.132859);

insert into offer (merchant_id, name, offer_type, has_employee_program, image_url, tos_url, begin_date, end_date) values
                  (@id, 'nancybee', 'give_only', 0, concat(@server, '/data/nancy_bee/banner.png'), concat(@server, '/data/nancy_bee/tos.html'), now(), now() + interval 60 day);

set @offer_id := (select LAST_INSERT_ID());

insert into gift ( offer_id, description, detailed_desc, value, discount_type, redemption_location_type, validation_type, image_url, default_give_image_url) values
                 (@offer_id, '20% off', 'first massage or haircut', 20, 'percentage', 'store', 'qrcode', concat(@server, '/data/nancy_bee/default_give.png'), concat(@server, '/data/nancy_bee/banner.png'));

set @gift_id := (select LAST_INSERT_ID());
