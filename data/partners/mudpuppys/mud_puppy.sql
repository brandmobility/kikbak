#set @server="https://m.kikbak.me";
#set @server="https://test.kikbak.me";

#Mud Puppys

insert into merchant (name,                 shortname,     description,    url,                       image_url) values 
                     ('Mudpuppy\'s Castro', 'Mudpuppys', 'pet groomer', 'http://www.mudpuppys.com', concat(@server, '/data/mud_puppy/mud_puppy_logo.png'));

set @id := (select LAST_INSERT_ID());

insert into location (address_1,            city,           state, zipcode, phone_number, verification_code, merchant_id, geofence, latitude, longitude) values
                     ('536 Castro Street', 'San Francisco', 'CA',  '94114', 4154318899,   'mudpuppy',        @id,         25,       37.7602,  -122.4351);

insert into offer (merchant_id, name,   offer_type, has_employee_program, image_url,                                               redeem_limit, tos_url,                                               begin_date, end_date) values 
                  (@id,         'wash', 'both',     0,                    concat(@server, '/data/mud_puppy/mud_puppy_banner.png'), 10,           concat(@server, '/data/mud_puppy/mud_puppy_tos.html'), now(),      now() + interval 180 day);

set @offer_id := (select LAST_INSERT_ID());

insert into gift ( offer_id,  description, detailed_desc,                          value, discount_type, redemption_location_type, validation_type, image_url,                                               default_give_image_url) values
                 (@offer_id, '$10 off',    'any service for first-time customers', 10,    'amount',      'store',                  'qrcode',         concat(@server, '/data/mud_puppy/mud_puppy_give.png'), concat(@server, '/data/mud_puppy/mud_puppy_banner.png'));

set @gift_id := (select LAST_INSERT_ID());

insert into kikbak (offer_id,  description,        detailed_desc,                             value, reward_type, validation_type, image_url) values
                   (@offer_id, '$5 service credit', 'for every friend that redeems your gift' , 5.00, 'purchase',  'qrcode',         concat(@server, '/data/mud_puppy/mud_puppy_banner.png'));
