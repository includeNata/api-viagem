package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.request.LowBudgetRequestDTO;
import com.api.apiviagem.DTO.response.LowBudgetResponseDTO;
import com.api.apiviagem.service.LowBudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/low-budget")
@CrossOrigin(origins = "*")
public class LowBudgetController {

    @Autowired
    private LowBudgetService lowBudgetService;

    @PostMapping
    public ResponseEntity<LowBudgetResponseDTO> createLowBudgetEstimate(@RequestBody @Valid LowBudgetRequestDTO lowBudget) {
        return ResponseEntity.ok(lowBudgetService.createLowBudgetEstimate(lowBudget));
    }
}
