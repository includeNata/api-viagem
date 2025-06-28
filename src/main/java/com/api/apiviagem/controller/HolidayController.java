package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.request.HolidayRequestDTO;
import com.api.apiviagem.service.HolidayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/holiday")
@CrossOrigin(origins = "*")
public class HolidayController {


    private static final Logger log = LoggerFactory.getLogger(HolidayController.class);

    @Autowired
    private HolidayService apiService;


    @PostMapping
    public ResponseEntity<String> findAll(@RequestBody HolidayRequestDTO request) {
        return ResponseEntity.ok(apiService.getData(request));
    }
}
