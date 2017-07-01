/**
* SQL for Fims postgresql tables
*/

CREATE OR REPLACE FUNCTION update_modified_column()
  RETURNS TRIGGER AS $$
BEGIN
  IF TG_OP = 'INSERT' OR row(NEW.*) IS DISTINCT FROM row(OLD.*) THEN
    NEW.modified = now();
    RETURN NEW;
  ELSE
    RETURN OLD;
  END IF;
END;
$$ language 'plpgsql';

CREATE OR REPLACE FUNCTION set_created_column()
  RETURNS TRIGGER AS $$
BEGIN
  NEW.created = now();
  RETURN NEW;
END;
$$ language 'plpgsql';

DROP TABLE IF EXISTS bcids;

CREATE TABLE bcids (
  id SERIAL PRIMARY KEY NOT NULL,
  ezid_made BOOLEAN NOT NULL DEFAULT '0',
  ezid_request BOOLEAN NOT NULL DEFAULT '1',
  identifier TEXT,
  doi TEXT,
  title TEXT,
  web_address TEXT,
  resource_type TEXT NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_id UUID,
  creator TEXT NOT NULL
);

CREATE INDEX bcids_identifier_idx on bcids (identifier);
CREATE INDEX bcids_resourceType_idx on bcids (resource_type);

CREATE TRIGGER update_bcids_modtime BEFORE INSERT OR UPDATE ON bcids FOR EACH ROW EXECUTE PROCEDURE update_modified_column();
CREATE TRIGGER set_bcids_createdtime BEFORE INSERT ON bcids FOR EACH ROW EXECUTE PROCEDURE set_created_column();
