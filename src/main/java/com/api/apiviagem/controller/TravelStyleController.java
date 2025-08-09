package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.response.TravelStyleResponseDTO;
import com.api.apiviagem.service.TravelStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/travel-style")
@CrossOrigin(origins = "*")
public class TravelStyleController {

    @Autowired
    private TravelStyleService travelStyleService;

    @GetMapping
    public TravelStyleResponseDTO createTravelStyles(@RequestParam String slug) {
        return travelStyleService.createTravelStyles(slug) ;
    }
}
