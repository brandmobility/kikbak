#set @server="https://m.kikbak.me";
set @server="https://test.kikbak.me";

#Childrens Place

insert into merchant (name,                    shortname,     description, url,                             image_url) values 
                     ('Crepe Cone', 'crepecone', 'food',  'http://crepe-cone.com/', concat(@server, '/data/crepe_cone/logo.png'));

set @id := (select LAST_INSERT_ID());

insert into location (address_1,      city,       state, zipcode, phone_number, verification_code, merchant_id, latitude,   longitude) values 
                     ('684 King St', 'San Francisco', 'CA',  '94107', "4154318899", 'dI232sf(81sk',           @id,         37.77016, -122.40220);
                   
insert into offer (merchant_id, name,    image_url,                                                           tos_url,                                                           begin_date, end_date, offer_type) values 
                  (@id,         'crepecone', concat(@server, '/data/crepe_cone/banner.png'), concat(@server, '/data/crepe_cone/tos.html'), now(),      now() + interval 180 day, "give_only");

set @offer_id := (select LAST_INSERT_ID());

insert into gift ( offer_id, description, detailed_desc,           value, discount_type, redemption_location_type, validation_type, image_url,                                                         default_give_image_url) values
                 (@offer_id, '15% off',   'for first-time customers', 15,    'percentage',  'store',                  'qrcode',       concat(@server, '/data/crepe_cone/default_give.png'), concat(@server, '/data/crepe_cone/banner.png'));

set @gift_id := (select LAST_INSERT_ID());                 



