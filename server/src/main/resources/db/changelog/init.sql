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

--changeset bruce.jeaung:analytics
create table if not exists monthly_statistics (
    id bigserial primary key,
    year_month char(7) unique,
    total_inspections integer,
    total_measurements bigint
);

--changeset bruce.jeaung:v2
drop table if exists pack_metrics_record;
drop table if exists generated_record;

-- pack_statistics
CREATE TABLE IF NOT EXISTS pack_statistics (
    id SERIAL PRIMARY KEY,
    container_id INTEGER,
    cluster_id INTEGER,
    pack_id INTEGER,
    real_time_id TEXT,
    abs_impedance_mean REAL,
    impedance_std_dev REAL,
    coefficient_variation REAL,
    dispersion REAL,
    characteristic_frequencies jsonb,
    max_abs_impedance REAL,
    min_abs_impedance REAL,
    max_coefficient_variation REAL,
    min_coefficient_variation REAL,
    max_impedance_std_dev REAL,
    min_impedance_std_dev REAL,
    temperature REAL,
    cell_colors jsonb,
    degradation_level TEXT,
    suggestion TEXT,
    alert_text TEXT,
    creation_time TIMESTAMP without time zone
);
create index if not exists idx_pack_statistics_container_creation_time on pack_statistics (container_id, creation_time);
create index if not exists idx_pack_statistics_pack_creation_time on pack_statistics (pack_id, creation_time);


-- cell_statistics
CREATE TABLE IF NOT EXISTS cell_statistics (
    id SERIAL PRIMARY KEY,
    container_id INTEGER,
    cluster_id INTEGER,
    pack_id INTEGER,
    group_id INTEGER,
    cell_id INTEGER,
    abs_impedance_mean REAL,
    impedance_std_dev REAL,
    coefficient_variation REAL,
    phase REAL,
    imp_mean_to_max_ratio REAL,
    imp_mean_to_min_ratio REAL,
    imp_mean_to_avg_ratio REAL,
    imp_std_dev_to_max_ratio REAL,
    imp_std_dev_to_min_ratio REAL,
    imp_std_dev_to_avg_ratio REAL,
    max_impedance_std_dev REAL,
    min_impedance_std_dev REAL,
    max_abs_impedance REAL,
    min_abs_impedance REAL,
    max_coefficient_variation REAL,
    min_coefficient_variation REAL,
    real_part_correlation jsonb not null,
    imag_part_correlation jsonb not null,
    nyquist_plot jsonb,
    bode_plot jsonb,
    drt_plot jsonb,
    real_part_10Hz real,
    imaginary_part_10Hz real,
    equivalent_circuit_diagram jsonb,
    creation_time TIMESTAMP without time zone
);
create index if not exists idx_cell_statistics_cell_creation_time on cell_statistics (cell_id, creation_time);

--changeset bruce.jeaung:delete-config
delete from cluster;
delete from container;
delete from pack;