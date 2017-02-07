package com.coursework.controller;

import com.coursework.model.Order;
import com.coursework.model.Ticket;
import com.coursework.model.User;
import com.coursework.services.RowService;
import com.coursework.services.TicketService;
import com.coursework.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @Autowired
    private RowService rowService;

    @RequestMapping(value = "/admin/edit/ticket", method = RequestMethod.GET, params = {"ticketId"})
    public String editTicket(@RequestParam int ticketId, Model model) {
        model.addAttribute("ticket", ticketService.getTicketByID(ticketId));
        return "/admin/edit/ticket";
    }

    @RequestMapping(value = "/admin/edit/ticket", method = RequestMethod.POST)
    public String editTicket(@Valid Ticket ticket, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/ticket";
        }
        ticketService.updateTicket(ticket);
        return "redirect:/admin/ticket";
    }

    @RequestMapping(value = "/admin/ticket", method = RequestMethod.GET)
    public String allTicket(@RequestParam(required = false, defaultValue = "1") Integer page, Model model) {
        Page<Ticket> pages = ticketService.getAllTicketsPage(page);
        model.addAttribute("allTickets", pages);
        return "/admin/ticket";
    }

    @RequestMapping(value = "/tickets", method = RequestMethod.GET)
    public String allTicketBySession(@RequestParam int filmSessionId, Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("tickets", new ArrayList<Integer>());
        model.addAttribute("allRow", rowService.getRowBySession(filmSessionId));
        return "/tickets";
    }

    @RequestMapping(value = "/tickets", method = RequestMethod.POST)
    public String addTicketToUser(@Valid Order order, Model model, BindingResult bindingResult) {
        User user = userService.findByUsername(getPrincipal());
        userService.updateUser(user, order);
        return "redirect:/";
    }

    private String getPrincipal() {
        String userName;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }
}

