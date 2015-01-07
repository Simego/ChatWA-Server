package com.thesimego.senacrs.sistemasdistribuidos.waserver.dao;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.AccountEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.GroupAccountEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.util.Util;
import java.io.Serializable;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author drafaelli
 */
@Repository
public class AccountDAO extends GenericDAO<AccountEN> implements Serializable {

    public AccountEN findByLogin(String login) {
        return find(new DBField("login", login));
    }

    public AccountEN findByLoginAndPassword(String login, String password) {
        return find(
                new DBField("login", login),
                new DBField("password", password)
        );
    }

    public void create(String login, String password) {
        insert(
                new DBField("login", login),
                new DBField("password", password)
        );
    }
    
    public List<AccountEN> listByGroupId(Integer groupId) {
        return list(
                new DBJoin("account", "id", GroupAccountEN.class),
                new DBField(Util.getTableNameByAnnotation(GroupAccountEN.class)+"."+"groups", groupId)
        );
    }

}
