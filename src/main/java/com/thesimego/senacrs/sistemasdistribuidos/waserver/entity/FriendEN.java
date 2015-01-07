package com.thesimego.senacrs.sistemasdistribuidos.waserver.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 *
 * @author drafaelli
 */
@Table(name = "friend")
public class FriendEN extends GenericEN implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "friend")
    private String friend;
    
    @JoinColumn(name = "account", referencedColumnName = "id")
    private AccountEN account;

    public FriendEN() {
    }

    public FriendEN(Integer id) {
        this.id = id;
    }

    public FriendEN(Integer id, String friend) {
        this.id = id;
        this.friend = friend;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public AccountEN getAccount() {
        return account;
    }

    public void setAccount(AccountEN account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "FriendsEN{" + "id=" + id + ", friend=" + friend + ", account=" + account + '}';
    }
    
}
