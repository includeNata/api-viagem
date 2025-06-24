package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.CityRequestDTO;
import com.api.apiviagem.DTO.CityResponseDTO;
import com.api.apiviagem.service.CityService;
import jakarta.validation.Valid;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController
@RequestMapping("/api/v1/city")
public class CityController {


    @Autowired
    private CityService cityService;


    @PostMapping
    public ResponseEntity<CityResponseDTO> findCity(@RequestBody CityRequestDTO cityRequestDTO){
        return ResponseEntity.ok(cityService.getInformation(cityRequestDTO));
    }
}
