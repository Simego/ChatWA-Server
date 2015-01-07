package com.thesimego.senacrs.sistemasdistribuidos.waserver.controller;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.controller.type.MessageType;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.AccountDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.FriendDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.GroupDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.MessageDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.AccountEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.FriendEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.GroupEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.MessageEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.util.Util;
import flexjson.JSONSerializer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Simego
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private MessageDAO messagesDAO;

    @Autowired
    private FriendDAO friendsDAO;

    @Autowired
    private GroupDAO groupDAO;

    /**
     * Envia nova mensagem para usuário/grupo (/send?message=X&receiver=X&type=X&group=X)
     * @param message
     * @param receiver
     * @param type
     * @param groupName
     * @param session
     * @return 
     */
    @RequestMapping(value = "/send", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseEntity<?> send(
            @RequestParam("message") String message,
            @RequestParam("receiver") String receiver,
            @RequestParam("type") String type,
            @RequestParam(value = "group", required = false) String groupName,
            HttpSession session) {

        String sLogin = (String) session.getAttribute("login");
        String sPassword = (String) session.getAttribute("password");

        MessageType messageType = MessageType.parse(type);
        if (messageType == null) {
            return new ResponseEntity<>("Tipo de mensagem inválido.", HttpStatus.BAD_REQUEST);
        }

        if (sLogin != null && sPassword != null) {
            // Busca a conta do usuário logado
            AccountEN account = accountDAO.findByLoginAndPassword(sLogin, sPassword);

            if (messageType.equals(MessageType.USER)) {
                // Verifica se o receptor existe
                AccountEN receiverAccount = accountDAO.findByLogin(receiver);
                if (receiverAccount == null) {
                    return new ResponseEntity<>("Usuário não encontrado.", HttpStatus.BAD_REQUEST);
                }
                // Verifica se está tentando se adicionar
                if (receiverAccount.equals(account)) {
                    return new ResponseEntity<>("Você não pode mandar mensagem para você mesmo.", HttpStatus.BAD_REQUEST);
                }

                // Busca lista de amigos do usuário logado
                FriendEN friendFound = friendsDAO.findByAccountAndFriend(receiverAccount, account);
                if (friendFound == null) {
                    friendsDAO.create(receiverAccount, account.getLogin());
                }

                messagesDAO.create(message, account.getLogin(), receiverAccount.getId(), null, messageType, Util.formatDate(new Date()));
            } else {
                GroupEN group = groupDAO.findByName(groupName);
                if (group == null) {
                    return new ResponseEntity<>("Grupo não encontrado.", HttpStatus.BAD_REQUEST);
                }

                List<AccountEN> groupMembers = accountDAO.listByGroupId(group.getId());
                groupMembers.remove(account);
                for (AccountEN member : groupMembers) {
                    messagesDAO.create(message, account.getLogin(), member.getId(), group.getName(), messageType, Util.formatDate(new Date()));
                }
            }

            return new ResponseEntity<>("Mensagem enviada com sucesso.", HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Listagem de mensagens por quem enviou, sendo usuário ou grupo (/list?sender=X&type=X
     * @param sender
     * @param type
     * @param groupName
     * @param session
     * @return 
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseEntity<?> list(
            @RequestParam("sender") String sender,
            @RequestParam("type") String type,
            @RequestParam(value = "group", required = false) String groupName,
            HttpSession session) {
        String login = (String) session.getAttribute("login");
        String password = (String) session.getAttribute("password");

        MessageType messageType = MessageType.parse(type);
        if (messageType == null) {
            return new ResponseEntity<>("Tipo de mensagem inválido.", HttpStatus.BAD_REQUEST);
        }

        if (login != null && password != null) {
            AccountEN receiverAccount = accountDAO.findByLoginAndPassword(login, password);

            String resultGroupName = null;
            if (messageType.equals(MessageType.GROUP)) {
                GroupEN group = groupDAO.findByName(groupName);
                if (group == null) {
                    return new ResponseEntity<>("Grupo não encontrado.", HttpStatus.BAD_REQUEST);
                }
                resultGroupName = group.getName();
            }

            List<MessageEN> messages = messagesDAO.listBySenderAndReceiverAndTypeOrGroup(sender, receiverAccount.getId(), resultGroupName, messageType);
            for (MessageEN message : messages) {
                messagesDAO.deleteById(message.getId());
            }

            String serialize = new JSONSerializer().exclude("receiver", "*.class").serialize(messages);
            return new ResponseEntity<>(serialize, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Verifica se há novas mensagens, seja de grupo ou usuário para a conta logada na sessão
     * @param session
     * @return 
     */
    @RequestMapping(value = "/check", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseEntity<?> check(HttpSession session) {
        String login = (String) session.getAttribute("login");
        String password = (String) session.getAttribute("password");

        if (login != null && password != null) {
            AccountEN receiverAccount = accountDAO.findByLoginAndPassword(login, password);
            List<MessageEN> messages = messagesDAO.listByReceiver(receiverAccount.getId());

            Map<String, MessageEN> newMessageUsers = new HashMap<>();
            for (MessageEN message : messages) {
                newMessageUsers.put(message.getSenderType().equals(MessageType.USER.getValue()) ? message.getSender() : message.getGroupName(), message);
            }

            String serialize = new JSONSerializer().exclude("id", "receiver", "message", "sentTimestamp", "*.class").serialize(newMessageUsers.values());
            return new ResponseEntity<>(serialize, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
