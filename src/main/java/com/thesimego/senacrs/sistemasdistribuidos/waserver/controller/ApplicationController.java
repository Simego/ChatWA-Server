package com.thesimego.senacrs.sistemasdistribuidos.waserver.controller;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.dao.AccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Simego
 */
@Controller
public class ApplicationController {
    
    @Autowired
    private AccountDAO accountDAO;
    
    /**
     * Carrega informações da tela inicial
     * @param model
     * @return 
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap model) {
        model.addAttribute("accounts",accountDAO.count());
        return "index";
    }
    
}
