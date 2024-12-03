--liquibase formatted sql

--changeset bruce.jeaung:init
create table if not exists container (
    id bigint primary key,
    name text
);

create table if not exists cluster (
    id bigint primary key,
    name text,
    container_id bigint
);

create table if not exists pack (
    id bigint primary key,
    cluster_id bigint,
    name text
);

create table if not exists eis_measurement (
    id bigserial,
    container_id bigint,
    cluster_id bigint,
    pack_id bigint,
    cell_id bigint,
    real_impedance double precision,
    imaginary_impedance double precision,
    temperature double precision,
    frequency double precision,
    voltage double precision,
    creation_time timestamp without time zone,
    primary key (id, creation_time)
) PARTITION BY RANGE (creation_time);

create table if not exists generated_record (
    id bigserial,
    container_id bigint,
    cluster_id bigint,
    pack_id bigint,
    cell_id text,
    creation_time timestamp without time zone,
    dispersion_coefficient double precision,
    temperature double precision,
    sei_parameter integer,
    dendrites_parameter integer,
    electrolyte_parameter integer,
    polarization_potential double precision,
    conductivity double precision,
    primary key (id, creation_time)
) PARTITION BY RANGE (creation_time);

create table if not exists pack_metrics_record (
    id bigserial,
    container_id bigint,
    cluster_id bigint,
    pack_id bigint,
    dispersion_coefficient double precision,
    safety_rate double precision,
    creation_time timestamp without time zone,
    primary key (id, creation_time)
) PARTITION BY RANGE (creation_time);

-- populate the configuration
insert into container (id) values (1), (2);
insert into cluster (id, container_id) values (1, 1), (2, 2);
insert into pack (id, cluster_id) values (1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1);
insert into pack (id, cluster_id) values (9, 2), (10, 2), (11, 2), (12, 2), (13, 2), (14, 2), (15, 2), (16, 2);

--changeset bruce.jeaung:db-scheduler
create table scheduled_tasks (
    task_name text not null,
    task_instance text not null,
    task_data bytea,
    execution_time timestamp with time zone not null,
    picked BOOLEAN not null,
    picked_by text,
    last_success timestamp with time zone,
    last_failure timestamp with time zone,
    consecutive_failures INT,
    last_heartbeat timestamp with time zone,
    version BIGINT not null,
    priority SMALLINT,
    PRIMARY KEY (task_name, task_instance)
);

CREATE INDEX execution_time_idx ON scheduled_tasks (execution_time);
CREATE INDEX last_heartbeat_idx ON scheduled_tasks (last_heartbeat);
CREATE INDEX priority_execution_time_idx on scheduled_tasks (priority desc, execution_time asc);
