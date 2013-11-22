alter table shared  modify `type` varchar(64);
alter table shared  add `email` varchar(64);
alter table shared  add `phonenumber` varchar(24);

alter table `user` modify `facebook_id` bigint;
alter table `user` modify `email` varchar(64);
alter table `user` modify `create_date` timestamp;
alter table `user` add `manual_name` varchar(64);
alter table `user` add `manual_email` varchar(64);
alter table `user` add `manual_number` varchar(64);

