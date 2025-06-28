package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.request.CityRequestDTO;
import com.api.apiviagem.DTO.response.CityResponseDTO;
import com.api.apiviagem.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
