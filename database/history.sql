DROP TABLE History CASCADE CONSTRAINTS PURGE;

CREATE TABLE History
  (
    TARGETPID   INTEGER NOT NULL,
    PID         INTEGER NOT NULL,
    PLACEID     VARCHAR2 (120 CHAR) NOT NULL,
    LDATE       DATE NOT NULL
  ) ;
ALTER TABLE History ADD CONSTRAINT History_PK PRIMARY KEY ( TARGETPID, PID, LDATE ) ;
ALTER TABLE History ADD CONSTRAINT History_Person_FK FOREIGN KEY ( PID ) REFERENCES Person ( PID ) ;
ALTER TABLE History ADD CONSTRAINT History_Target_Person_FK FOREIGN KEY ( TARGETPID ) REFERENCES Person ( PID ) ;

INSERT INTO History (TARGETPID, PID, PLACEID, LDATE)
VALUES (4, 5, 'ChIJteZiRTqVC0cRw0VL4NFmd-k', sysdate-3-(1/24)-37/(24*60));
INSERT INTO History (TARGETPID, PID, PLACEID, LDATE)
VALUES (3, 1, 'ChIJteZiRTqVC0cRw0VL4NFmd-k', sysdate-4-(5/24)-2/(24*60));

commit;

--delete from History;

SELECT * FROM History;
