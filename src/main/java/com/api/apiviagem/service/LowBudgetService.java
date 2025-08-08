package com.api.apiviagem.service;

import com.api.apiviagem.DTO.request.LowBudgetRequestDTO;
import com.api.apiviagem.DTO.response.LowBudgetResponseDTO;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LowBudgetService {


    @Autowired
    private APIService apiService;

    @Autowired
    private Gson gson;

    private String prompt = "Com base nos dados que vou te passar , faça uma estimativa de orçamento  de quanto vai ficar essa viagem com a data de hoje" + LocalDateTime.now();


    public LowBudgetResponseDTO createLowBudgetEstimate(LowBudgetRequestDTO lowBudget) {
        prompt += "" + lowBudget;

        prompt +="A reposta tem que vir nesse formato {\n" +
                "  \"hosting\": \"valor com o preço da moeda local,sem a sigla\",\n" +
                "  \"meals\": \"valor com o preço da moeda local,sem a sigla\",\n" +
                "  \"localTransport\": \"valor com o preço da moeda local,sem a sigla\",\n" +
                "  \"attractions\": \"valor com o preço da moeda local,sem a sigla\",\n" +
                "  \"totalValue\": \"valor com o preço da moeda local , sem a sigla\"\n" +
                "  \"coinLocal\": \"sigla da moeda local\"\n" +
                "}";

        prompt +=" quero apenas o json de reposta , nao volte nenhum texto. Retorne os valores como double dos. Sem aspas nos valores doubles";
        GenerateContentResponse response = apiService.geminiAPI(prompt);
        String result  = apiService.extractResponse(response).replace("```","").replace("json","");
        return  gson.fromJson(result, LowBudgetResponseDTO.class);
    }

}
