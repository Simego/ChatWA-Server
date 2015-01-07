package com.thesimego.senacrs.sistemasdistribuidos.waserver.dao;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.AccountEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.FriendEN;
import java.io.Serializable;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author drafaelli
 */
@Repository
public class FriendDAO extends GenericDAO<FriendEN> implements Serializable {

    public List<FriendEN> findByAccount(AccountEN account) {
        return findByAccountId(account.getId());
    }

    public List<FriendEN> findByAccountId(Integer id) {
        return list(new DBField("account", id));
    }

    public void create(AccountEN account, String friend) {
        insert(
                new DBField("friend", friend),
                new DBField("account", account.getId())
        );
    }

    public FriendEN findByAccountAndFriend(AccountEN account, AccountEN friend) {
        return find(
                new DBField("account", account.getId()),
                new DBField("friend", friend.getLogin())
        );
    }

}
