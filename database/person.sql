DROP TABLE Person CASCADE CONSTRAINTS PURGE;

CREATE TABLE Person
  (
    PID         INTEGER NOT NULL,
    REALNAME    VARCHAR2 (30 CHAR) NOT NULL,
    GENDER      VARCHAR2 (8 CHAR) NOT NULL,
    AGE         INTEGER NOT NULL,
    USERID      VARCHAR2 (30 CHAR) NOT NULL UNIQUE,
    PASSWD      VARCHAR2 (64 CHAR) NOT NULL,
    IMAGEURI    VARCHAR2 (300 CHAR) NOT NULL,
    EMAIL       VARCHAR2 (50 CHAR) NOT NULL,
    PHONE       VARCHAR2 (20 CHAR) NOT NULL,
    HOBBIES     VARCHAR2 (1400 CHAR)
  ) ;
ALTER TABLE Person ADD CONSTRAINT Person_PK PRIMARY KEY ( PID ) ;

DROP SEQUENCE Person_sequence;

CREATE SEQUENCE Person_sequence;

CREATE OR REPLACE TRIGGER Person_on_insert
  BEFORE INSERT ON Person
  FOR EACH ROW
BEGIN
  SELECT Person_sequence.nextval
  INTO :new.PID
  FROM dual;
END;
/

INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES) 
VALUES ('Michal Hruska', 'Male', 21, 'Michal9', '34c425a9e1c8d8e725899a5b34d3c603c7a91ff39da92ef5f67310a643283815',
        'https://i.imgur.com/V1ppn0f.jpg', 'michal@gmail.com', '653284312', ' 1 5 15 32 128 ');

INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES) 
VALUES ('Ivana Dobrovska', 'Female', 34, 'Ivana1', '087232c617b75b0745421beb6bf2d1c00a86eb744e065ac24972f33ea3ea8580',
        'https://i.imgur.com/4GZrEJZ.png', 'ivana@seznam.cz', '734289532', ' 3 12 22 33 34 ');

INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES) 
VALUES ('Adam Holecek', 'Male', 24, 'Adam5', '0c35f0196937f61775791dae18546e0c32adee77af82770c3e6794c5253326a5',
        'https://i.imgur.com/H1fkXvK.jpg', 'adam@gmail.com', '473852342', ' 7 54 82 215 ');

INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES) 
VALUES ('Petr Novacek', 'Male', 22, 'Petr41', '19f3e9b3bdc816201e236d411449a79de11e0b044b604de9b2c78a7bf19f4f8a',
        'https://i.imgur.com/ghMkr7u.jpg', 'petr@seznam.cz', '875623234', ' 64 208 216 235 260 ');

INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES) 
VALUES ('Alex Borovsky', 'Male', 48, 'Alex4', 'f3cce19dbb6fd2f97e1863a5fc65225e35e274ca3069a40d21542d405641a984',
        'https://i.imgur.com/YyPG2VY.jpg', 'alex@email.cz', '875454652', ' 12 14 125 182 ');

INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES) 
VALUES ('Pavla Dvorakova', 'Female', 15, 'Pavla8', 'cbc7591526239e3d00ace606d961cd3d83df038a80aebded556e090c95e8ebb8',
        'https://i.imgur.com/4Bw8VOs.jpg', 'pavla@gmail.com', '363253458', ' 38 84 161 173 276 ');

INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES) 
VALUES ('Kristyna Borska', 'Female', 21, 'Kristyna5', '12e5fb7d7e4bd584b0735b07f6f7c4c3e65085cba9231b6f06523c39299f8f84',
        'https://i.imgur.com/3cMjMIG.jpg', 'kristyna@seznam.cz', '766432524', ' 1 79 221 227 234 287 ');

INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES) 
VALUES ('Ivan Dvorak', 'Male', 79, 'Ivan5', '691872b9e172528a2e53713bf9802fc4ac33d5ecffb711d759c609d47c2f293f',
        'https://i.imgur.com/u8UGn6x.gif', 'ivan@email.cz', '765124534', ' 29 31 80 149 155 244 ');

INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES) 
VALUES ('Adela Oblakova', 'Female', 32, 'Adela2', '989977bf1d32deff7d6d77e72950854a386888c4c8811746ebce15d37dc7c43a',
        'https://i.imgur.com/6vSlWIG.jpg', 'adela@seznam.cz', '623456546', ' 52 155 235 248 ');

INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES) 
VALUES ('Ondrej Valenta', 'Male', 18, 'Ondrej6', '23b7fd17c17e976e946ae9c728c22d958c12dad00d67e2a4413cbbb79d39b4a7',
        'https://i.imgur.com/OAxkznm.jpg', 'ondra@gmail.com', '363253458', ' 40 59 102 105 130 270 ');

commit;

--delete from person;
--delete from person where pid > 10;

SELECT * FROM PERSON;
