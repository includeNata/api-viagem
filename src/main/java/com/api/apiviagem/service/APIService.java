package com.api.apiviagem.service;


import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;


@Service
public class APIService {




    public GenerateContentResponse geminiAPI(String prompt){
        Client client = new Client();
        GenerateContentResponse response =
                client.models.generateContent("gemini-2.0-flash",
                       prompt,null);
        return response;
    }

    public String extractResponse(GenerateContentResponse response){
        JsonObject obj = JsonParser.parseString(response.toJson()).getAsJsonObject();

        return obj.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
    }
}
