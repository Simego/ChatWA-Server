package com.thesimego.senacrs.sistemasdistribuidos.waserver.controller;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.AccountDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.FriendDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.AccountEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.FriendEN;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author drafaelli
 */
@Controller
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private AccountDAO accountDAO;
    
    @Autowired
    private FriendDAO friendsDAO;

    /**
     * Adiciona amigo à conta da sessão (/add?friend=X)
     * @param friend
     * @param session
     * @return 
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseEntity<?> add(
            @RequestParam("friend") String friend,
            HttpSession session) {

        String sLogin = (String) session.getAttribute("login");
        String sPassword = (String) session.getAttribute("password");

        if (sLogin != null && sPassword != null) {
            // Verifica se o usuário a ser adicionado existe
            if(accountDAO.findByLogin(friend) == null) {
                return new ResponseEntity<>("Usuário não encontrado.", HttpStatus.BAD_REQUEST);
            }
            
            // Busca a conta do usuário logado
            AccountEN account = accountDAO.findByLoginAndPassword(sLogin, sPassword);
            // Verifica se está tentando se adicionar
            if(friend.equals(account.getLogin())) {
                return new ResponseEntity<>("Você não pode se adicionar como amigo.", HttpStatus.BAD_REQUEST);
            }
            
            // Busca lista de amigos do usuário logado
            List<FriendEN> friends = friendsDAO.findByAccount(account);
            // Cria a lista de String com os usuários
            for(FriendEN f : friends) {
                if(f.getFriend().equals(friend)) {
                    return new ResponseEntity<>("O usuário já está em sua lista.", HttpStatus.BAD_REQUEST);
                }
            }
            
            friendsDAO.create(account, friend);
            return new ResponseEntity<>("Amigo adicionado com sucesso.", HttpStatus.OK);
            
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }

    /**
     * Lista amigos da conta logada
     * @param session
     * @return 
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseEntity<?> list(HttpSession session) {
        String login = (String) session.getAttribute("login");
        String password = (String) session.getAttribute("password");

        if (login != null && password != null) {
            AccountEN account = accountDAO.findByLoginAndPassword(login, password);
            List<FriendEN> friends = friendsDAO.findByAccount(account);
            List<String> friendsAsString = new ArrayList<>();
            
            for(FriendEN friend : friends) {
                friendsAsString.add(friend.getFriend());
            }
            
            return new ResponseEntity<>(friendsAsString, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
