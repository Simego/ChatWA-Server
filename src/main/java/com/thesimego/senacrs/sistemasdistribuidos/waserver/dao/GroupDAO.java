package com.thesimego.senacrs.sistemasdistribuidos.waserver.dao;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.GroupEN;
import java.io.Serializable;
import org.springframework.stereotype.Repository;

/**
 *
 * @author drafaelli
 */
@Repository
public class GroupDAO extends GenericDAO<GroupEN> implements Serializable {

    public GroupEN findByName(String name) {
        return find(
                new DBField("name", name)
        );
    }
    
    public GroupEN create(String name) {
        insert(new DBField("name", name));
        return findByName(name);
    }

}
