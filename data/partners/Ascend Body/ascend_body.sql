#set @server="https://m.kikbak.me";
set @server="https://test.kikbak.me";

#Childrens Place

insert into merchant (name,                    shortname,     description, url,                             image_url) values 
                     ('Ascend Body', 'ab', 'massage',  'avaschmidt.com', concat(@server, '/data/ascend_body/logo.png'));

set @id := (select LAST_INSERT_ID());

insert into location (address_1,      city,       state, zipcode, phone_number, verification_code, merchant_id, latitude,   longitude) values
                     ('2973 16th St', 'San Francisco', 'CA',  '94103', "4157100541", 'absa21@1',           @id,         37.76547, -122.41918);

insert into offer (merchant_id, name,    offer_type, has_employee_program, image_url,                                                           tos_url,                                                           begin_date, end_date) values 
                  (@id,         'Ascend Body', 'give_only',     0,                    concat(@server, '/data/ascend_body/banner.png'), concat(@server, '/data/ascend_body/tos.html'), now(),      now() + interval 180 day);

set @offer_id := (select LAST_INSERT_ID());

insert into gift ( offer_id, description, detailed_desc,           value, discount_type, redemption_location_type, validation_type, image_url,                                                         default_give_image_url) values
                 (@offer_id, '$35 off',   'first massage with Ava Schmidt', 35,    'amount',  'store',                  'qrcode',       concat(@server, '/data/ascend_body/default_give.png'), concat(@server, '/data/ascend_body/banner.png'));
