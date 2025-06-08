package com.api.apiviagem.service;

import br.com.brasilapi.BrasilAPI;
import br.com.brasilapi.api.Feriados;
import com.api.apiviagem.DTO.HolidayRequestDTO;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


@Service
public class APIService {


    private static final Logger log = LoggerFactory.getLogger(APIService.class);
    private final Path path = Paths.get(System.getProperty("user.dir"),"src","main","resources","static");
    private static  final String PROMPT = "So gere o json, sem texto rapido.A cada feriado cite 2 lugares para viagens, a caracteristica do pais/cidade nome da moeda e pontos turisticos, longitude e latitude dos pontos turisticos me de esses dados em json , os feriados são: ";
    private String address = "https://date.nager.at/api/v3/PublicHolidays/#year/#acronym";
    private static final String JSON = """
                  {
                    "holidayName":
                    "countryCode":
                    "countryName":
                    "currency":
                    "travelDestinations": [
                      {
                        "city":
                        "state":
                        "description":
                        "touristAttractions": [
                          {
                            "name":
                            "latitude":
                            "longitude":
                          },
                          {
                            "name":
                            "latitude":
                            "longitude":
                          }
                        ],
                        "average":
                      },
                      {
                        "city":
                        "state":
                        "description":
                        "touristAttractions": [
                          {
                            "name":
                            "latitude":
                            "longitude":
                          },
                          {
                            "name":
                            "latitude":
                            "longitude":
                          }
                        ],
                        "average":
                      }
                    ]
                  },
            
            """;
    private String partPrompt = "Crie um campo no json chamado  preco de viagens , dentro dele voce coloca a media de preços de #x1 ate o ponto turisco que voce indicou";


    public String getData(HolidayRequestDTO request){
        String result = "";
        String aux = partPrompt.replace("#x1",request.origin());
        if(Files.notExists(Path.of(path + "\\f"+ request.acronym()+request.year()+".json"))){
            Client client = new Client();
            GenerateContentResponse response =
                    client.models.generateContent("gemini-2.0-flash",
                            PROMPT+getHolidays(request)+aux +" esse e o exemplo de json"+JSON,null);
           result = extractResponse(response).replace("```","").replace("json","");
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

    protected String extractResponse(GenerateContentResponse response){
        JsonObject obj = JsonParser.parseString(response.toJson()).getAsJsonObject();

        return obj.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
    }

    public String getHolidays(HolidayRequestDTO dto){
        String newAddress = address.replace("#year",dto.year()+"");
        newAddress = newAddress.replace("#acronym",dto.acronym());
        System.out.println();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request  = HttpRequest.newBuilder()
                .uri(URI.create(newAddress))
                .build();
        try {
            return  client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        } catch (IOException | InterruptedException e) {throw new RuntimeException(e);}


    }

}
