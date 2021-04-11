package com.powerboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;


@Controller
@RequestMapping("/referral")
public class ReferralCodeController {

    /**
     * 推广主页带推广码
     * @param code
     * @return
     * @throws IOException
     */
    @GetMapping("/{code}")
    public ModelAndView referralCodePage(@PathVariable("code") String code) throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code",code);
        modelAndView.setViewName("index.html");
        return modelAndView;
    }

    /**
     * 推广主页
     * @return
     * @throws IOException
     */
    @GetMapping("")
    public ModelAndView referralPage() throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code","");
        modelAndView.setViewName("index.html");
        return modelAndView;
    }
}
