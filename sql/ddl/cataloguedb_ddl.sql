create sequence sequence_generator MINVALUE 1 MAXVALUE 999999999999999999  START WITH 1 INCREMENT BY 1  NO CYCLE;
CREATE TABLE collector
(
   id bigint PRIMARY KEY NOT NULL,
   name varchar(255),
   type varchar(255),
   datasource varchar(255),
   description varchar(5000),
   created_by varchar(255),
   created_on timestamp,
   updated_by varchar(255),
   updated_on timestamp
)
;
CREATE TABLE dashboard
(
   id bigint PRIMARY KEY NOT NULL,
   name varchar(255),
   dashboard bytea,
   dashboard_content_type varchar(255),
   description varchar(5000),
   created_by varchar(255),
   created_on timestamp,
   updated_by varchar(255),
   updated_on timestamp,
   collector_id bigint
)
;
CREATE TABLE folder
(
   id bigint PRIMARY KEY NOT NULL,
   title varchar(255),
   parent_id bigint,
   is_opened bool,
   is_checked bool,
   is_folder bool,
   created_by varchar(255),
   created_on timestamp,
   updated_by varchar(255),
   updated_on timestamp
)
;
CREATE TABLE library
(
   id bigint PRIMARY KEY NOT NULL,
   app_name varchar(255),
   virtual_path varchar(2000),
   data_source varchar(255),
   collector_id bigint,
   folder_id bigint
)
;
CREATE TABLE manage_view
(
   id bigint PRIMARY KEY NOT NULL,
   name varchar(255),
   view_data bytea,
   view_data_content_type varchar(255),
   description varchar(5000),
   type varchar(255),
   status varchar(255),
   created_by varchar(255),
   created_on timestamp,
   updated_by varchar(255),
   updated_on timestamp
)
;
