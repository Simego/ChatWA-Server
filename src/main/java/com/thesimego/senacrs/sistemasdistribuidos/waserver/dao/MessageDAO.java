package com.thesimego.senacrs.sistemasdistribuidos.waserver.dao;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.controller.type.MessageType;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.MessageEN;
import java.io.Serializable;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author drafaelli
 */
@Repository
public class MessageDAO extends GenericDAO<MessageEN> implements Serializable {

    public void create(String message, String sender, Integer receiverAccountId, String groupName, MessageType messageType, String timestamp) {
        insert(
                new DBField("message", message),
                new DBField("sender", sender),
                new DBField("receiver", receiverAccountId),
                new DBField("groupName", groupName),
                new DBField("sentTimestamp", timestamp),
                new DBField("senderType", messageType.getValue())
        );
    }

    public void deleteById(Integer messageId) {
        delete(new DBField("id", messageId));
    }

    public List<MessageEN> listByReceiver(Integer receiverAccountId) {
        return list(new DBField("receiver", receiverAccountId));
    }

    public List<MessageEN> listBySenderAndReceiverAndTypeOrGroup(String sender, Integer receiverAccountId, String groupName, MessageType messageType) {
        if (groupName == null) {
            return list(
                    new DBField("sender", sender),
                    new DBField("receiver", receiverAccountId),
                    new DBField("senderType", messageType.getValue())
            );
        }
        return list(
                new DBField("receiver", receiverAccountId),
                new DBField("groupName", groupName),
                new DBField("senderType", messageType.getValue())
        );
    }

    public List<MessageEN> listByReceiverAndGroup(Integer receiverAccountId, String groupName) {
        return list(
                new DBField("receiver", receiverAccountId),
                new DBField("groupName", groupName)
        );
    }

}
