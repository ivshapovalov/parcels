create table parcels
(
    acceptance_date timestamp(6) with time zone not null,
    id bigint not null,
    receive_date timestamp(6) with time zone,
    acceptance_index varchar(255) not null,
    current_index varchar(255),
    parcel_type varchar(255) not null check (parcel_type in ('LETTER','PACKAGE','BANDEROLE','POSTCARD')),
    receiver_address varchar(255) not null,
    receiver_index varchar(255) not null,
    receiver_name varchar(255) not null,
    status varchar(255) not null check (status in ('ACCEPTED','IN_TRANSIT','AT_POSTAL_OFFICE','WAITING_FOR_RECEIVING','RECEIVED')),
    primary key (id)
);

create table postal_offices
(
    address varchar(255) not null,
    index varchar(255) not null,
    name varchar(255) not null,
    primary key (index)
);

create table transfers
(
    arrival_date timestamp(6) with time zone,
    departure_date timestamp(6) with time zone not null,
    id bigint not null,
    parcel_id bigint not null,
    arrival_index varchar(255) not null,
    departure_index varchar(255) not null,
    primary key (id)
);

GO
