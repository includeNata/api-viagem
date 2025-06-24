package com.api.apiviagem.service;


import com.api.apiviagem.DTO.HolidayRequestDTO;
import com.api.apiviagem.model.Holiday;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class HolidayService {


    private static final Logger log = LoggerFactory.getLogger(HolidayService.class);
    @Autowired
    private APIService apiService;

    private final Path path = Paths.get(System.getProperty("user.dir"),"src","main","resources","static");
    private static  final String PROMPT = """
                Para cada objeto do JSON fornecido, complete os campos vazios com os seguintes dados:
            
                1. "holidayName": nome do feriado.
                2. "countryName": nome do país sugerido para viagem no feriado.
                3. "holidayDate": data do feriado.
                4. "currency": nome da moeda oficial do país.
                5. city
                6. "latitude": "",
                    "longitude": "",
                7. "description": descrição da cidade e porque ela é boa no feriado indicado    
            
                Regras:
                - Todas as cidades devem pertencer ao país indicado no campo "countryName".
                - Escolha apenas cidades reconhecidas internacionalmente como destinos turísticos.
                - Retorne exclusivamente o JSON preenchido, sem nenhum texto adicional fora da estrutura.
                - Cidades boas para turismo.
            """;
    private String address = "https://date.nager.at/api/v3/PublicHolidays/#year/#acronym";
    private static final String JSON = """
            {
              "holidayName": "",
              "countryName": "",
              "holidayDate": "",
              "description:"",
              "city":,
              "currency": "",
               "latitude": "",
              "longitude": ""
            }
         
            """;



    public String getData(HolidayRequestDTO request){
        String result = "";


        if(Files.notExists(Path.of(path + "\\f"+ request.acronym()+request.year()+".json"))){
            GenerateContentResponse response = apiService.geminiAPI(PROMPT+getHolidays(request)+" esse e o exemplo de json"+JSON + "não mude  os nomes dos campos. Continue até finalizar o json.");
           result = apiService.extractResponse(response).replace("```","").replace("json","");
        }

        return createFile(request,result);
    }


    public String  createFile(HolidayRequestDTO request , String response){

        File file = new File(path+"\\f"+ request.acronym()+request.year()+".json");
        StringBuilder json = new StringBuilder();
        try {
            if(!file.exists()){
                    if(file.createNewFile()){
                        FileWriter writer = new FileWriter(file);
                        writer.write(response);
                        writer.close();
                        log.info("Json criado com sucesso!");
                    }
                return response;
            }
            else{
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                while(bufferedReader.ready()){
                    json.append(bufferedReader.readLine()).append("\n");
                }
                bufferedReader.close();
                log.info("Json carregado com sucesso!");
                return json.toString();
            }
        } catch (IOException e) { throw new RuntimeException(e);}


    }



    public String getHolidays(HolidayRequestDTO dto){
        String newAddress = address.replace("#year",dto.year()+"");
        newAddress = newAddress.replace("#acronym",dto.acronym());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request  = HttpRequest.newBuilder()
                .uri(URI.create(newAddress))
                .build();
        try {
            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return  response;

        } catch (IOException | InterruptedException e) {throw new RuntimeException(e);}


    }

}
