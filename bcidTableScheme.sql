/**
* SQL for Bcid postgresql tables
*/

CREATE OR REPLACE FUNCTION set_created_column()
  RETURNS TRIGGER AS $$
BEGIN
  NEW.created = now();
  RETURN NEW;
END;
$$ language 'plpgsql';

CREATE SEQUENCE bcid_id;

DROP TABLE IF EXISTS ezid_queue;

CREATE TABLE ezid_queue (
  identifier TEXT PRIMARY KEY,
  doi TEXT,
  title TEXT,
  web_address TEXT,
  resource_type TEXT NOT NULL,
  created TIMESTAMP,
  creator TEXT NOT NULL,
  publisher TEXT NOT NULL,
  request_type TEXT NOT NULL
);

CREATE TRIGGER set_ezid_queue_createdtime BEFORE INSERT ON ezid_queue FOR EACH ROW EXECUTE PROCEDURE set_created_column();

DROP TABLE IF EXISTS clients;

CREATE TABLE clients (
  id CHAR(20) PRIMARY KEY NOT NULL,
  secret TEXT NOT NULL,
  access_token CHAR(20),
  token_expiration TIMESTAMP
);

DROP TABLE IF EXISTS client_identifiers;

CREATE TABLE client_identifiers (
  client_id CHAR(20) NOT NULL REFERENCES clients(id),
  identifier TEXT UNIQUE NOT NULL
);

CREATE INDEX client_identifiers_idx ON client_identifiers(client_id, identifier);

