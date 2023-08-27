alter table if exists parcels drop constraint if exists parcel_postal_office_index_constraint;
alter table if exists transfers drop constraint if exists postal_office_parcel_id_constraint;
alter table if exists transfers drop constraint if exists postal_office_index_receiver_constraint;
alter table if exists transfers drop constraint if exists postal_office_index_sender_constraint;

drop table if exists databasechangelog cascade;
drop table if exists databasechangeloglock cascade;
drop table if exists parcels cascade;
drop table if exists postal_offices cascade;
drop table if exists transfers cascade;

drop sequence if exists parcels_seq;
drop sequence if exists transfers_seq;
