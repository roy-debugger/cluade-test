-- Active: 1772523339972@@127.0.0.1@3306@eventuate
-- USE eventuate;

-- DROP Table IF Exists message;
-- DROP Table IF Exists received_messages;
-- DROP Table IF Exists offset_store;

-- CREATE TABLE message (
--   id VARCHAR(255) PRIMARY KEY,
--   destination VARCHAR(1000) NOT NULL,
--   headers VARCHAR(1000) NOT NULL,
--   payload VARCHAR(1000) NOT NULL,
--   published SMALLINT DEFAULT 0,
--   message_partition SMALLINT,
--   creation_time BIGINT
-- );

-- CREATE INDEX message_published_idx ON message(published, id);

-- CREATE TABLE received_messages (
--   consumer_id VARCHAR(255),
--   message_id VARCHAR(255),
--   PRIMARY KEY(consumer_id, message_id),
--   creation_time BIGINT
-- );

-- CREATE TABLE offset_store(
--   client_name VARCHAR(255) NOT NULL PRIMARY KEY,
--   serialized_offset VARCHAR(255)
-- );



-- CREATE TABLE cdc_monitoring (reader_id VARCHAR(1000) PRIMARY KEY, last_time BIGINT);


use order_db;


DROP Table IF Exists message;
DROP Table IF Exists received_messages;
DROP Table IF Exists offset_store;

CREATE TABLE message (
  id VARCHAR(255) PRIMARY KEY,
  destination VARCHAR(1000) NOT NULL,
  headers VARCHAR(1000) NOT NULL,
  payload VARCHAR(1000) NOT NULL,
  published SMALLINT DEFAULT 0,
  message_partition SMALLINT,
  creation_time BIGINT
);

CREATE INDEX message_published_idx ON message(published, id);

CREATE TABLE received_messages (
  consumer_id VARCHAR(255),
  message_id VARCHAR(255),
  PRIMARY KEY(consumer_id, message_id),
  creation_time BIGINT
);

CREATE TABLE offset_store(
  client_name VARCHAR(255) NOT NULL PRIMARY KEY,
  serialized_offset VARCHAR(255)
);



-- CREATE TABLE cdc_monitoring (reader_id VARCHAR(768) PRIMARY KEY, last_time BIGINT);

use inventory_db;


DROP Table IF Exists message;
DROP Table IF Exists received_messages;
DROP Table IF Exists offset_store;

CREATE TABLE message (
  id VARCHAR(255) PRIMARY KEY,
  destination VARCHAR(1000) NOT NULL,
  headers VARCHAR(1000) NOT NULL,
  payload VARCHAR(1000) NOT NULL,
  published SMALLINT DEFAULT 0,
  message_partition SMALLINT,
  creation_time BIGINT
);

CREATE INDEX message_published_idx ON message(published, id);

CREATE TABLE received_messages (
  consumer_id VARCHAR(255),
  message_id VARCHAR(255),
  PRIMARY KEY(consumer_id, message_id),
  creation_time BIGINT
);

CREATE TABLE offset_store(
  client_name VARCHAR(255) NOT NULL PRIMARY KEY,
  serialized_offset VARCHAR(255)
);



-- CREATE TABLE cdc_monitoring (reader_id VARCHAR(768) PRIMARY KEY, last_time BIGINT);

use payment_db;


DROP Table IF Exists message;
DROP Table IF Exists received_messages;
DROP Table IF Exists offset_store;

CREATE TABLE message (
  id VARCHAR(255) PRIMARY KEY,
  destination VARCHAR(1000) NOT NULL,
  headers VARCHAR(1000) NOT NULL,
  payload VARCHAR(1000) NOT NULL,
  published SMALLINT DEFAULT 0,
  message_partition SMALLINT,
  creation_time BIGINT
);

CREATE INDEX message_published_idx ON message(published, id);

CREATE TABLE received_messages (
  consumer_id VARCHAR(255),
  message_id VARCHAR(255),
  PRIMARY KEY(consumer_id, message_id),
  creation_time BIGINT
);

CREATE TABLE offset_store(
  client_name VARCHAR(255) NOT NULL PRIMARY KEY,
  serialized_offset VARCHAR(255)
);



-- CREATE TABLE cdc_monitoring (reader_id VARCHAR(768) PRIMARY KEY, last_time BIGINT);
