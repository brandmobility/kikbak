#set @server="https://m.kikbak.me";
#set @server="https://test.kikbak.me";

#Verizon Wireless

insert into merchant (name,               shortname, description, url,                              image_url) values 
                     ('Verizon Wireless', 'Verizon', 'telecom',   'http://www.verizonwireless.com', concat(@server, '/data/verizon/verizon_logo.png'));

set @id := (select LAST_INSERT_ID());

insert into location (address_1,               city,            state, zipcode, phone_number, verification_code, merchant_id, geofence, latitude, longitude) values
                     ('219 University Avenue', 'Palo Alto',     'CA',  '94301', 6503236127,   'verizon',         @id,         25,       37.4455,  -122.1620),
                     ('1020 N Rengstorff Ave', 'Mountain View', 'CA',  '94043', 6509661441,   'verizon',         @id,         25,       37.4214,  -122.0944);

insert into offer (merchant_id, name,            offer_type, has_employee_program, redeem_limit, auth,    protection, map_uri, image_url,                                           tos_url,                                           begin_date, end_date) values 
                  (@id,         'subscription',  'both',     1,                    10,           'phone', null,       null,    concat(@server, '/data/verizon/verizon_banner.png'), concat(@server, '/data/verizon/verizon_tos.html'), now(),      now() + interval 180 day);

set @offer_id := (select LAST_INSERT_ID());

insert into gift ( offer_id,  description, detailed_desc,              value, discount_type, redemption_location_type, validation_type, image_url,                                         default_give_image_url) values
                 (@offer_id, '$50 off',    ' when switching with new 2-year service', 50,    'amount',      'all',                  'barcode',       concat(@server, '/data/verizon/verizon_give.png'), concat(@server, '/data/verizon/verizon_banner.png'));

set @gift_id := (select LAST_INSERT_ID());

insert into kikbak (offer_id,  description, detailed_desc,                              value, reward_type, validation_type, image_url) values
                   (@offer_id, '$50',       'for every friend that redeems your gift' , 50,    'gift_card', 'barcode',       concat(@server, '/data/verizon/verizon_banner.png'));

# generate test barcodes
#call create_test_barcode(100, @id, @offer_id, @gift_id, 50, 10);

# manual test barcodes
#insert into barcode (merchant_id, offer_id,  gift_id,  code,          value, valid_days, begin_date, expiration_date) values
#                    (@id,         @offer_id, @gift_id, "09546999653", 50,    10,         now(),      now() + interval 90 day),
#                    (@id,         @offer_id, @gift_id, "59445115420", 50,    10,         now(),      now() + interval 90 day),
#                    (@id,         @offer_id, @gift_id, "22945801296", 50,    10,         now(),      now() + interval 90 day),
#                    (@id,         @offer_id, @gift_id, "11987354821", 50,    10,         now(),      now() + interval 90 day),
#                    (@id,         @offer_id, @gift_id, "50473707719", 50,    10,         now(),      now() + interval 90 day),
#                    (@id,         @offer_id, @gift_id, "41935246720", 50,    10,         now(),      now() + interval 90 day),
#                    (@id,         @offer_id, @gift_id, "59407004509", 50,    10,         now(),      now() + interval 90 day),
#                    (@id,         @offer_id, @gift_id, "64684068371", 50,    10,         now(),      now() + interval 90 day),
#                    (@id,         @offer_id, @gift_id, "57595412781", 50,    10,         now(),      now() + interval 90 day),
#                    (@id,         @offer_id, @gift_id, "99113099441", 50,    10,         now(),      now() + interval 90 day);


