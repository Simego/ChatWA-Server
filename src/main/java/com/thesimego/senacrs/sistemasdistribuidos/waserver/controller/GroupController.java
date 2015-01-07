package com.thesimego.senacrs.sistemasdistribuidos.waserver.controller;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.AccountDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.GroupAccountDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.GroupDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.MessageDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.AccountEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.GroupAccountEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.GroupEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.MessageEN;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private AccountDAO accountDAO;
    
    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private GroupAccountDAO groupAccountDAO;

    /**
     * Entra no grupo especificado (/join?group=X)
     * @param groupName
     * @param session
     * @return 
     */
    @RequestMapping(value = "/join", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseEntity<?> join(
            @RequestParam("group") String groupName,
            HttpSession session) {

        String sLogin = (String) session.getAttribute("login");
        String sPassword = (String) session.getAttribute("password");

        if (sLogin != null && sPassword != null) {
            // Busca a conta do usuário logado
            AccountEN account = accountDAO.findByLoginAndPassword(sLogin, sPassword);
            GroupEN group = groupDAO.findByName(groupName);

            if (group == null) {
                group = groupDAO.create(groupName);
            }
            
            List<AccountEN> groupMembers = accountDAO.listByGroupId(group.getId());
            if (groupMembers.contains(account)) {
                return new ResponseEntity<>("Você já faz parte do grupo.", HttpStatus.BAD_REQUEST);
            } else {
                groupAccountDAO.create(group.getId(), account.getId());
                return new ResponseEntity<>("Você entrou no grupo '" + group.getName() + "'.", HttpStatus.OK);
            }

        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Sai do grupo especificado (/leave?group=X)
     * @param groupName
     * @param session
     * @return 
     */
    @RequestMapping(value = "/leave", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseEntity<?> leave(
            @RequestParam("group") String groupName,
            HttpSession session) {

        String sLogin = (String) session.getAttribute("login");
        String sPassword = (String) session.getAttribute("password");

        if (sLogin != null && sPassword != null) {
            // Busca a conta do usuário logado
            AccountEN account = accountDAO.findByLoginAndPassword(sLogin, sPassword);
            GroupEN group = groupDAO.findByName(groupName);
            if (group == null) {
                return new ResponseEntity<>("O grupo não existe.", HttpStatus.BAD_REQUEST);
            }

            List<AccountEN> groupMembers = accountDAO.listByGroupId(group.getId());
            if (!groupMembers.contains(account)) {
                return new ResponseEntity<>("Você não está no grupo.", HttpStatus.BAD_REQUEST);
            }
            
            List<MessageEN> messages = messageDAO.listByReceiverAndGroup(account.getId(), group.getName());
            messageDAO.deleteAll(messages);

            groupAccountDAO.delete(group.getId(), account.getId());
            return new ResponseEntity<>("Você saiu do grupo '" + group.getName() + "'.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Lista os grupos para a conta na sessão
     * @param session
     * @return 
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseEntity<?> list(
            HttpSession session) {

        String login = (String) session.getAttribute("login");
        String password = (String) session.getAttribute("password");

        if (login != null && password != null) {
            AccountEN account = accountDAO.findByLoginAndPassword(login, password);

            List<GroupAccountEN> groups = groupAccountDAO.listByAccountId(account.getId());
            Set<String> grps = new LinkedHashSet<>();
            for (GroupAccountEN grp : groups) {
                grps.add(grp.getGroup().getName());
            }

            return new ResponseEntity<>(grps, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
