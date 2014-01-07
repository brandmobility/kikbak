alter table barcode add column status varchar(16) not null default 'open';
alter table barcode add column redeem_date datetime;
alter table barcode drop column redeemed;
