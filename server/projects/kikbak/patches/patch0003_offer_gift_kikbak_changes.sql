alter table offer add column `offer_type` varchar(12);
update offer set `offer_type`='both';
alter table offer modify column `offer_type` varchar(12) not null;

alter table `allocatedgift` add column `created_date` TIMESTAMP default CURRENT_TIMESTAMP;
alter table `credit` add column `created_date` TIMESTAMP default CURRENT_TIMESTAMP;

update `credit` set created_date=now();
update `allocatedgift` set created_date=now();