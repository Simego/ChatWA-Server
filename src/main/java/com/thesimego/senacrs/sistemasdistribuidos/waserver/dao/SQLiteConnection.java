package com.thesimego.senacrs.sistemasdistribuidos.waserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Simego
 */
@Component
@Scope(value = "singleton")
public class SQLiteConnection {

    private Connection connection = null;

    public Connection getConnection() {
        return connection;
    }

    /**
     * Inicializa a conexão com o SQLite, criando as tabelas caso necessário
     */
    @PostConstruct
    public void openConnection() {
        try {
            System.setProperty("com.thesimego.debug.query", "true");
            
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:whatsapop.db");
            
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS account ( \n" +
                                "    id       INTEGER        PRIMARY KEY ON CONFLICT ROLLBACK AUTOINCREMENT,\n" +
                                "    login    VARCHAR( 32 )  NOT NULL ON CONFLICT ROLLBACK\n" +
                                "                            UNIQUE ON CONFLICT ROLLBACK,\n" +
                                "    password VARCHAR( 64 )  NOT NULL ON CONFLICT ROLLBACK \n" +
                                ");"
            );
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS friend ( \n" +
                                "    id      INTEGER        PRIMARY KEY ON CONFLICT ROLLBACK AUTOINCREMENT,\n" +
                                "    account INTEGER        NOT NULL ON CONFLICT ROLLBACK\n" +
                                "                           REFERENCES account ( id ),\n" +
                                "    friend  VARCHAR( 32 )  NOT NULL ON CONFLICT ROLLBACK \n" +
                                ");"
            );
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS groups ( \n" +
                                "    id   INTEGER        PRIMARY KEY ON CONFLICT ROLLBACK AUTOINCREMENT,\n" +
                                "    name VARCHAR( 32 )  NOT NULL ON CONFLICT ROLLBACK\n" +
                                "                        UNIQUE ON CONFLICT ROLLBACK \n" +
                                ");"
            );
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS groups_account ( \n" +
                                "    id      INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "    groups  INTEGER NOT NULL ON CONFLICT ROLLBACK\n" +
                                "                    REFERENCES groups ( id ),\n" +
                                "    account INTEGER NOT NULL ON CONFLICT ROLLBACK\n" +
                                "                    REFERENCES account ( id ) \n" +
                                ");"
            );
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS message ( \n" +
                                "    id             INTEGER         PRIMARY KEY ON CONFLICT ROLLBACK AUTOINCREMENT\n" +
                                "                                   NOT NULL,\n" +
                                "    message        VARCHAR( 200 )  NOT NULL ON CONFLICT ROLLBACK,\n" +
                                "    sender         VARCHAR( 32 )   NOT NULL ON CONFLICT ROLLBACK,\n" +
                                "    receiver       INTEGER         NOT NULL ON CONFLICT ROLLBACK\n" +
                                "                                   CONSTRAINT 'fk_receiver_account_id' REFERENCES account ( id ),\n" +
                                "    sent_timestamp VARCHAR( 64 )   NOT NULL ON CONFLICT ROLLBACK,\n" +
                                "    sender_type    VARCHAR         NOT NULL ON CONFLICT ROLLBACK, \n" +
                                "    group_name     VARCHAR( 32 )  \n" +
                                ");"
            );
            
            System.out.println("###### SQLITE CONNECTION INITIALIZED ######");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Finaliza a conexão com o SQLite
     */
    @PreDestroy
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("###### SQLITE CONNECTION CLOSED ######");
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

}
