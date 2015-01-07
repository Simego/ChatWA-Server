package com.thesimego.senacrs.sistemasdistribuidos.waserver.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 *
 * @author drafaelli
 */
@Table(name = "groups_account")
public class GroupAccountEN extends GenericEN implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(name = "id")
    private Integer id;
    
    @JoinColumn(name = "groups")
    private GroupEN group;
    
    @JoinColumn(name = "account")
    private AccountEN account;

    public GroupAccountEN() {
    }

    public GroupAccountEN(Integer id, GroupEN group, AccountEN account) {
        this.id = id;
        this.group = group;
        this.account = account;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GroupEN getGroup() {
        return group;
    }

    public void setGroup(GroupEN group) {
        this.group = group;
    }

    public AccountEN getAccount() {
        return account;
    }

    public void setAccount(AccountEN account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "GroupAccountEN{" + "id=" + id + ", group=" + group + ", account=" + account + '}';
    }
    
}
