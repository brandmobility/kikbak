alter table `barcode` add column `valid_days` int unsigned;
alter table `barcode` add column `redeemed` boolean default 0;
alter table `barcode` add column `expired` boolean default 0;