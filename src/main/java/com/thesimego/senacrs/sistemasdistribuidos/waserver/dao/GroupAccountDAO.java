package com.thesimego.senacrs.sistemasdistribuidos.waserver.dao;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.GroupAccountEN;
import java.io.Serializable;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author drafaelli
 */
@Repository
public class GroupAccountDAO extends GenericDAO<GroupAccountEN> implements Serializable {

    public void create(Integer groupId, Integer accountId) {
        insert(
                new DBField("group", groupId),
                new DBField("account", accountId)
        );
    }
    
    public void delete(Integer groupId, Integer accountId) {
        delete(
                new DBField("group", groupId),
                new DBField("account", accountId)
        );
    }
    
    public List<GroupAccountEN> listByGroupIdAndAccountId(Integer groupId, Integer accountId) {
        return list(
                new DBField("group", groupId),
                new DBField("account", accountId)
        );
    }

    public List<GroupAccountEN> listByAccountId(Integer accountId) {
        return list(
                new DBField("account", accountId)
        );
    }

}
