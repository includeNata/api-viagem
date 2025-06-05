package com.api.apiviagem.service;

import br.com.brasilapi.BrasilAPI;
import br.com.brasilapi.api.Feriados;
import com.api.apiviagem.DTO.HolidayRequestDTO;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Arrays;


@Service
public class APIService {


    private static final Logger log = LoggerFactory.getLogger(APIService.class);

    public String getData(HolidayRequestDTO request){

        log.info("Iniciando o service getDat");
        Client client = new Client();
        GenerateContentResponse response =
                client.models.generateContent("gemini-2.0-flash",
                        "So gere o json, sem texto rapido.A cada feriado cite 2 lugares para viagens, a caracteristica do pais/cidade nome da moeda e pontos turisticos, longitude e latitude dos pontos turisticos me de esses dados em json , os feriados são: "+
                                Arrays.toString(BrasilAPI.feriados(request.year().toString())),null);
        log.info("Terminou O processo");
        return extractResponse(response).replace("```","").replace("json","");


    }


    protected String extractResponse(GenerateContentResponse response){
        JsonObject obj = JsonParser.parseString(response.toJson()).getAsJsonObject();

        return obj.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

    }

}
