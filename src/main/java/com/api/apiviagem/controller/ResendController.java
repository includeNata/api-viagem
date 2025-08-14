package com.api.apiviagem.controller;

import com.api.apiviagem.service.ResendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send-email")
public class ResendController {


    @Autowired
    private ResendService resendService;

    @GetMapping("/{email}")
    public ResponseEntity<String> sendEmail(@PathVariable String email) {

       return  ResponseEntity.ok(resendService.sendEmail(email));
    }
}
