#set @server="https://m.kikbak.me";
set @server="https://test.kikbak.me";

#Trio Spa

insert into merchant (name,                 shortname,     description,    url,                      image_url) values 
                     ('TrioSpa',            'trio',        'spa',          'http://www.TrioSpa.net', concat(@server, '/data/triospa/logo.png'));

set @id := (select LAST_INSERT_ID());

insert into location (address_1,          city,       state, zipcode, phone_number, verification_code, merchant_id, geofence, latitude,  longitude) values
                     ('2160 The Alameda', 'San Jose', 'CA',  '95126', 4089851544,   '35v8trio',         @id,        25,       37.344439, -121.930489);

insert into offer (merchant_id, name,   offer_type, has_employee_program, redeem_limit, auth, protection, map_uri, image_url,                                   tos_url,                                   begin_date, end_date) values 
                  (@id,         'trio', 'both',     0,                    10,           null, null,       null,    concat(@server, '/data/triospa/banner.png'), concat(@server, '/data/triospa/tos.html'), now(),      now() + interval 90 day);

set @offer_id := (select LAST_INSERT_ID());

insert into gift ( offer_id,  description, detailed_desc,                 value, discount_type, redemption_location_type, validation_type, image_url,                                         default_give_image_url) values
                 (@offer_id, '$20 off',    'Massage or Signature Facial', 20,    'amount',      'store',                  'qrcode',        concat(@server, '/data/triospa/default_give.png'), concat(@server, '/data/triospa/banner.png'));

set @gift_id := (select LAST_INSERT_ID());

insert into kikbak (offer_id,  description, detailed_desc,                           value, reward_type, validation_type, image_url) values
                   (@offer_id, '$10 credit',   'towards massage, facial or waxing', 10.00, 'purchase',  'qrcode',        concat(@server, '/data/triospa/banner.png'));
