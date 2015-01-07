package com.thesimego.senacrs.sistemasdistribuidos.waserver.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 *
 * @author drafaelli
 */
@Table(name = "message")
public class MessageEN extends GenericEN implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Column(name = "id")
    private Integer id;

    @Column(name = "message")
    private String message;

    @Column(name = "sender")
    private String sender;
    
    @JoinColumn(name = "receiver")
    private AccountEN receiver;
    
    @Column(name = "sent_timestamp")
    private String sentTimestamp;
    
    @Column(name = "sender_type")
    private String senderType;
    
    @Column(name = "group_name")
    private String groupName;
    
    public MessageEN() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public AccountEN getReceiver() {
        return receiver;
    }

    public void setReceiver(AccountEN receiver) {
        this.receiver = receiver;
    }

    public String getSentTimestamp() {
        return sentTimestamp;
    }

    public void setSentTimestamp(String sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "MessageEN{" + "id=" + id + ", message=" + message + ", sender=" + sender + ", receiver=" + receiver + ", sentTimestamp=" + sentTimestamp + ", senderType=" + senderType + ", groupName=" + groupName + '}';
    }

}
