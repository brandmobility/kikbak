#set @server="https://m.kikbak.me";
#set @server="https://test.kikbak.me";

set @offer_begin_date=CONVERT_TZ('2014-01-30 00:00','-08:00','+00:00');
set @offer_end_date=CONVERT_TZ('2014-04-30 00:00','-08:00','+00:00');
#Verizon Wireless

insert into merchant (name,               shortname, description, url,                              image_url) values 
                     ('Verizon Wireless', 'Verizon', 'telecom',   'http://www.verizonwireless.com', concat(@server, '/data/verizon/verizon_logo.png'));

set @merchant_id := (select LAST_INSERT_ID());

insert into offer (merchant_id,  name,            offer_type, has_employee_program, redeem_limit, auth,    protection, map_uri, image_url,                                           tos_url,                                           begin_date,        end_date) values 
                  (@merchant_id, 'subscription',  'both',     1,                    10,           'phone', null,       null,    concat(@server, '/data/verizon/verizon_banner.png'), concat(@server, '/data/verizon/verizon_tos.html'), @offer_begin_date, @offer_end_date);

set @offer_id := (select LAST_INSERT_ID());

insert into gift ( offer_id,  description, detailed_desc,              value, discount_type, redemption_location_type, validation_type, image_url,                                         default_give_image_url) values
                 (@offer_id, '$50 off',    ' when switching with new 2-year service', 50,    'amount',      'all',                  'barcode',       concat(@server, '/data/verizon/verizon_give.png'), concat(@server, '/data/verizon/verizon_banner.png'));

set @gift_id := (select LAST_INSERT_ID());

insert into kikbak (offer_id,  description, detailed_desc,                              value, reward_type, validation_type, image_url) values
                   (@offer_id, '$50',       'for every friend that redeems your gift' , 50,    'gift_card', 'barcode',       concat(@server, '/data/verizon/verizon_banner.png'));

#
#insert into location (address_1,               city,            state, zipcode, phone_number, verification_code, merchant_id, geofence, latitude, longitude) values
#                     ('219 University Avenue', 'Palo Alto',     'CA',  '94301', 6503236127,   'verizon',         @id,         25,       37.4455,  -122.1620),
#                     ('1020 N Rengstorff Ave', 'Mountain View', 'CA',  '94043', 6509661441,   'verizon',         @id,         25,       37.4214,  -122.0944);
