package com.api.apiviagem.service;


import com.api.apiviagem.DTO.HolidayRequestDTO;
import com.google.genai.types.GenerateContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class HolidayService {


    private static final Logger log = LoggerFactory.getLogger(HolidayService.class);
    @Autowired
    private APIService apiService;

    private final Path path = Paths.get(System.getProperty("user.dir"),"src","main","resources","static");
    private static  final String PROMPT = """
                Para cada objeto do JSON fornecido, complete os campos vazios com os seguintes dados:
            
                1. "holidayName": nome do feriado.
                2. "countryCode": código do país (ISO-3166).
                3. "countryName": nome do país sugerido para viagem no feriado.
                4. "holidayDate": data do feriado.
                5. "currency": nome da moeda oficial do país.
                6. "travelDestinations": lista de cidades recomendadas **dentro do país indicado**. Cada cidade deve conter:
                   - "city": nome de uma cidade turística famosa no país.
                   - "state": estado ou província da cidade (se aplicável).
                   - "description": descrição breve da cidade e seu apelo turístico (ex: cultural, natural, histórica, etc.).
                   - "touristAttractions": exatamente 5 pontos turísticos da cidade. Cada ponto deve conter:
                     - "name": nome do ponto turístico.
                     - "latitude": coordenada de latitude.
                     - "longitude": coordenada de longitude.
                   - "average": média de custo diário estimado para turistas em dólares (USD).
            
                Regras:
                - Todas as cidades devem pertencer ao país indicado no campo "countryName".
                - Escolha apenas cidades reconhecidas internacionalmente como destinos turísticos.
                - Todos os pontos turísticos devem ser reais, com nome e coordenadas verdadeiras.
                - Retorne exclusivamente o JSON preenchido, sem nenhum texto adicional fora da estrutura.
            
            """;
    private String address = "https://date.nager.at/api/v3/PublicHolidays/#year/#acronym";
    private static final String JSON = """
                  {
                    "holidayName": "",
                    "countryCode": "",
                    "countryName": "",
                    "holidayDate": "",
                    "currency": "",
                    "travelDestinations": [
                      {
                        "city": "",
                        "state": "",
                        "description": "",
                        "touristAttractions": [
                          {
                            "name": "",
                            "latitude": "",
                            "longitude": ""
                          },
                          {
                            "name": "",
                            "latitude": "",
                            "longitude": ""
                          }
                        ],
                        "average": ""
                      },
                      {
                        "city": "",
                        "state": "",
                        "description": "",
                        "touristAttractions": [
                          {
                            "name": "",
                            "latitude": "",
                            "longitude": ""
                          },
                          {
                            "name": "",
                            "latitude": "",
                            "longitude": ""
                          }
                        ],
                        "average": ""
                      }
                    ]
                  }
         
            """;
    private String partPrompt = "Crie um campo no json chamado  preco de viagens , dentro dele voce coloca a media de preços de #x1 ate o ponto turisco que voce indicou";


    public String getData(HolidayRequestDTO request){
        String result = "";
        String aux = partPrompt.replace("#x1",request.origin());
        if(Files.notExists(Path.of(path + "\\f"+ request.acronym()+request.year()+".json"))){
            GenerateContentResponse response = apiService.geminiAPI(PROMPT+getHolidays(request)+aux +" esse e o exemplo de json"+JSON + "não mude  os nomes dos campos. Continue até finalizar o json.");
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
