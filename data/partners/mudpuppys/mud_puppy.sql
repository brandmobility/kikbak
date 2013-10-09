set @server="https://m.kikbak.me";
#set @server="https://test.kikbak.me";

#Mud Puppys

insert into merchant (name,                 shortname,     description,    url,                       image_url) values 
                     ('Mudpuppy\'s Castro', 'Mudpuppy\'s', 'pet groomer', 'http://www.mudpuppys.com', concat(@server, '/data/mud_puppy/mud_puppy_logo.png'));

set @id := (select LAST_INSERT_ID());

insert into location (address_1,            city,           state, zipcode, phone_number, verification_code, merchant_id, latitude, longitude) values 
                     ('536 Castro Street', 'San Francisco', 'CA',  '94114', 4154318899,   'mudpuppy',        @id,         37.7602,  -122.4351);
#                     ('Chestnut Street',   'San Francisco', 'CA',  '94114', 4154318899,   'mudpuppy',        @id,         37.800250, -122.440728),
#                     ('#1 Isabel Street',  'Richmond',      'CA',  '94804', 5105598899,   'mudpuppy',        @id,         37.899053, -122.323972);

insert into offer (merchant_id, name,        image_url,                                               tos_url,                                               begin_date, end_date) values 
                  (@id,         'wash',      concat(@server, '/data/mud_puppy/mud_puppy_banner.png'), concat(@server, '/data/mud_puppy/mud_puppy_tos.html'), now(),      now() + interval 180 day);

set @offer_id := (select LAST_INSERT_ID());

insert into gift ( offer_id,  description, detailed_desc,                    value, discount_type, redemption_location_type, validation_type, image_url,                                             default_give_image_url) values
                 (@offer_id, '$10 off',    'any first-time dog or cat wash', 10,   'amount',      'store',                  'qrcode',         concat(@server, '/data/mud_puppy/mud_puppy_give.png'), concat(@server, '/data/mud_puppy/mud_puppy_banner.png'));

set @gift_id := (select LAST_INSERT_ID());                 

insert into kikbak (offer_id,  description,        detailed_desc,                             value, reward_type, validation_type, image_url) values
                   (@offer_id, '$5 store credit', 'for every friend that redeems your gift' , 5.00, 'purchase',  'qrcode',         concat(@server, '/data/mud_puppy/mud_puppy_banner.png'));




	
