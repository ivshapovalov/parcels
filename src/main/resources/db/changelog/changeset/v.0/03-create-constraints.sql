--liquibase formatted sql

--changeset Ivan:3
alter table if exists parcels add constraint parcel_postal_office_acceptance_index_constraint foreign key (acceptance_index) references postal_offices;
alter table if exists parcels add constraint parcel_postal_office_current_index_constraint foreign key (current_index) references postal_offices;
alter table if exists parcels add constraint parcel_postal_office_receiver_index_constraint foreign key (receiver_index) references postal_offices;
alter table if exists transfers add constraint transfers_parcel_id_constraint foreign key (parcel_id) references parcels;
alter table if exists transfers add constraint transfers_departure_index_constraint foreign key (departure_index) references postal_offices;
alter table if exists transfers add constraint transfers_arrival_index_constraint foreign key (arrival_index) references postal_offices;

--rollback
alter table if exists parcels drop constraint if exists parcel_postal_office_acceptance_index_constraint;
alter table if exists parcels drop constraint if exists parcel_postal_office_current_index_constraint;
alter table if exists parcels drop constraint if exists parcel_postal_office_receiver_index_constraint;
alter table if exists transfers drop constraint if exists transfers_parcel_id_constraint;
alter table if exists transfers drop constraint if exists transfers_departure_index_constraint;
alter table if exists transfers drop constraint if exists transfers_arrival_index_constraint;