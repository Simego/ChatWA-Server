package com.thesimego.senacrs.sistemasdistribuidos.waserver.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Table;

/**
 *
 * @author drafaelli
 */
@Table(name = "groups")
public class GroupEN extends GenericEN implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name")
    private String name;
    
    public GroupEN() {
    }

    public GroupEN(Integer id) {
        this.id = id;
    }

    public GroupEN(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GroupEN{" + "id=" + id + ", name=" + name + '}';
    }

}
