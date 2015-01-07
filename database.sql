-- Table: account
CREATE TABLE account ( 
    id       INTEGER        PRIMARY KEY ON CONFLICT ROLLBACK AUTOINCREMENT,
    login    VARCHAR( 32 )  NOT NULL ON CONFLICT ROLLBACK
                            UNIQUE ON CONFLICT ROLLBACK,
    password VARCHAR( 64 )  NOT NULL ON CONFLICT ROLLBACK 
);




-- Table: friend
CREATE TABLE friend ( 
    id      INTEGER        PRIMARY KEY ON CONFLICT ROLLBACK AUTOINCREMENT,
    account INTEGER        NOT NULL ON CONFLICT ROLLBACK
                           REFERENCES account ( id ),
    friend  VARCHAR( 32 )  NOT NULL ON CONFLICT ROLLBACK 
);




-- Table: groups
CREATE TABLE groups ( 
    id   INTEGER        PRIMARY KEY ON CONFLICT ROLLBACK AUTOINCREMENT,
    name VARCHAR( 32 )  NOT NULL ON CONFLICT ROLLBACK
                        UNIQUE ON CONFLICT ROLLBACK 
);




-- Table: groups_account
CREATE TABLE groups_account ( 
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    groups  INTEGER NOT NULL ON CONFLICT ROLLBACK
                    REFERENCES groups ( id ),
    account INTEGER NOT NULL ON CONFLICT ROLLBACK
                    REFERENCES account ( id ) 
);




-- Table: message
CREATE TABLE message ( 
    id             INTEGER         PRIMARY KEY ON CONFLICT ROLLBACK AUTOINCREMENT
                                   NOT NULL,
    message        VARCHAR( 200 )  NOT NULL ON CONFLICT ROLLBACK,
    sender         VARCHAR( 32 )   NOT NULL ON CONFLICT ROLLBACK,
    receiver       INTEGER         NOT NULL ON CONFLICT ROLLBACK
                                   CONSTRAINT 'fk_receiver_account_id' REFERENCES account ( id ),
    sent_timestamp VARCHAR( 64 )   NOT NULL ON CONFLICT ROLLBACK,
    sender_type    VARCHAR         NOT NULL ON CONFLICT ROLLBACK,
    group_name     VARCHAR( 32 ) 
);





