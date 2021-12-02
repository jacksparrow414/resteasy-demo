CREATE TABLE "USER"( user_id   varchar NOT NULL PRIMARY KEY,roles varchar(45) NOT NULL,PASS_WORD varchar(45) NOT NULL,scopes varchar(45) NOT NULL);
CREATE TABLE "CLIENT"(client_id varchar(45) NOT NULL PRIMARY KEY,client_secret varchar(45) NOT NULL,redirect_url varchar(45) NOT NULL,scopes varchar(45) NOT NULL,authorized_grant_types varchar(100) NOT NULL);
CREATE TABLE "AUTHORIZATION_CODE"(code varchar(45) NOT NULL PRIMARY KEY,client_id varchar(45) NOT NULL,redirect_url varchar(45) NOT NULL,approved_scopes varchar(45) NOT NULL,user_id varchar(100) NOT NULL,EXPIRATION_DATE timestamp NOT NULL);