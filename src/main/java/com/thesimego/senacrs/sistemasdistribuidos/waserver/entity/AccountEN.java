package com.thesimego.senacrs.sistemasdistribuidos.waserver.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Table;

/**
 *
 * @author drafaelli
 */
@Table(name = "account")
public class AccountEN extends GenericEN implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Column(name = "id")
    private Integer id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    public AccountEN() {
    }

    public AccountEN(Integer id) {
        this.id = id;
    }

    public AccountEN(Integer id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AccountEN{" + "id=" + id + ", login=" + login + ", password=" + password + '}';
    }

}
