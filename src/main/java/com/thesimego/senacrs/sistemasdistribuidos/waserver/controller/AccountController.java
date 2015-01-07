package com.thesimego.senacrs.sistemasdistribuidos.waserver.controller;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.AccountDAO;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.AccountEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.util.Util;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author drafaelli
 */
@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountDAO accountDAO;

    /**
     * Cria uma conta com login e senha (/create?login=X&password=X)
     * @param login
     * @param password
     * @param session
     * @return 
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseEntity<?> create(
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            HttpSession session) {

        AccountEN foundAccount = accountDAO.findByLogin(login);

        if (foundAccount != null) {
            return new ResponseEntity<>("Usuário já existente.", HttpStatus.BAD_REQUEST);
        }

        accountDAO.create(login, Util.getSha256(password));
        AccountEN newAccount = accountDAO.findByLogin(login);
        
        session.setAttribute("login", newAccount.getLogin());
        session.setAttribute("password", newAccount.getPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Faz login com login e senha (/login/{login}/{password})
     * @param login
     * @param password
     * @param session
     * @return 
     */
    @RequestMapping(value = "/login/{login}/{password}", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseEntity<?> login(
            @PathVariable("login") String login,
            @PathVariable("password") String password,
            HttpSession session) {

        AccountEN account = accountDAO.findByLoginAndPassword(login, Util.getSha256(password));
        if (account != null) {
            session.setAttribute("login", account.getLogin());
            session.setAttribute("password", account.getPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("Login ou senha inválido(s).", HttpStatus.BAD_REQUEST);
    }

    /**
     * Faz logout da sessão
     * @param session
     * @return 
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> logout(HttpSession session) {
        if (Util.isUserLogged(session)) {
            session.setAttribute("login", null);
            session.setAttribute("password", null);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verifica se usuário está logado
     * @param session
     * @return 
     */
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> check(HttpSession session) {
        if (Util.isUserLogged(session)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
