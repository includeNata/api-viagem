package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.CityResponseDTO;
import com.api.apiviagem.service.CityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/city")
public class CityController {


    @Autowired
    private CityService cityService;

    @GetMapping
    public ResponseEntity<CityResponseDTO> findCity(@RequestParam @Valid String city){
        return ResponseEntity.ok(cityService.getInformation(city));
    }
}
