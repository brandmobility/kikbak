#set @server="https://m.kikbak.me";
#set @server="https://test.kikbak.me";

#Senerity Spa

insert into merchant (name,           shortname, description, url) values 
                     ('Serenity Spa', 'Spa',     'spa',       'http://www.serenityspa.com');

set @id := (select LAST_INSERT_ID());

insert into location (address_1,        city,        state, zipcode, phone_number, verification_code, merchant_id, latitude,  longitude) values
                     ('225 Wilton Ave', 'Palo Alto', 'CA',  '94306', 3107094681,   'spa',             @id,         37.42080, -122.13034);

insert into offer (merchant_id, name,         offer_type, has_employee_program, image_url,                                                     tos_url,                                                     begin_date, end_date) values 
                  (@id,         'first_time', 'both',     0,                    concat(@server, '/data/serenity_spa/serenity_spa_banner.png'), concat(@server, '/data/serenity_spa/serenity_spa_tos.html'), now(),      now() + interval 180 day);

set @offer_id := (select LAST_INSERT_ID());

insert into gift ( offer_id,  description,    detailed_desc,                          value, lottery,                 discount_type, redemption_location_type, validation_type, image_url,                                                   default_give_image_url) values
                 (@offer_id, 'up to $10 off', 'any service for first-time customers', 10,    '0.05 10 0.25 7 0.7 5',  'amount',      'store',                  'qrcode',        concat(@server, '/data/serenity_spa/serenity_spa_give.png'), concat(@server, '/data/serenity_spa/serenity_spa_banner.png'));

set @gift_id := (select LAST_INSERT_ID());

insert into kikbak (offer_id,  description,  detailed_desc,                             value, reward_type, validation_type, image_url) values
                   (@offer_id, '$15 credit', 'for every friend that redeems your gift', 15.00, 'purchase',  'qrcode',        concat(@server, '/data/serenity_spa/serenity_spa_banner.png'));
