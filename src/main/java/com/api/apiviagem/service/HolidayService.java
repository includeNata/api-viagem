package com.api.apiviagem.service;


import com.api.apiviagem.DTO.request.HolidayRequestDTO;
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
                       \s
                         1. "holidayName": nome do feriado.
                         2. "country": nome do país sugerido para viagem no feriado.
                         3. "holidayDate": data do feriado.
                         4. "currency": moeda oficial do país.
                         5. "travelDestinations": uma lista de exatamente 4 cidades turísticas desse país com os seguintes campos:
                            - "city": nome da cidade.
                            - "state": estado ou província (se aplicável).
                            - "description": breve descrição turística da cidade.
                            - "latitude": latitude geográfica da cidade.
                            - "longitude": longitude geográfica da cidade.
                            - "timeTravel": tempo de viagem. Ex: 3h30 (voo + traslados).
                            - "targetAudience": público alvo da cidade.
                            - "highlights": Lista de array dos destaques da cidade;
                            - "timeTemperature": Temperatura padrão da cidade. Exemplos: Moderado - Quente - Frio
                       \s
                       \s
                         ### Regras obrigatórias:
                       \s
                         - Todas as cidades devem pertencer ao país informado no campo `"country"`.
                         - As cidades devem ser reconhecidas internacionalmente como **destinos turísticos** populares.
                         - Cada país deve conter **exatamente 4** cidades no campo `"travelDestinations"`.
                         - A resposta deve ser **exclusivamente o JSON preenchido**, sem nenhuma explicação ou texto adicional fora do JSON.
                         - O JSON de saída deve ser **sempre bem formatado e válido**, com:
                           - Todos os campos preenchidos corretamente,
                           - Sem vírgulas extras no final de objetos ou arrays,
                           - Nenhum caractere fora do JSON (sem comentários, sem anotações).
                         - Processar **todos os feriados** fornecidos no JSON de entrada.
                         - Se o conteúdo da resposta for muito grande, **quebrar a resposta em partes**, garantindo que cada parte seja um **JSON válido**, bem formatado e completo por si só.
                         - Em nenhuma hipótese retornar um JSON mal formatado ou com campos incompletos.
                         - As descrições deve sempre ser em portugues
                       \s
                         ### Exemplo de estrutura correta:
                       \s
                         [
                           {
                             "holidayName":\s
                             "country":\s
                             "holidayDate":\s
                             "currency":\s
                             "travelDestinations": [
                               {
                                 "city":\s
                                 "state":\s
                                 "description":\s
                                 "latitude":\s
                                 "longitude":\s
                                 "timeTravel":\s
                                 "targetAudience":
                                 "highlights":\s
                                 "timeTemperature":\s
                       \s
                               },
                               {
                                 "city":\s
                                 "state":\s
                                 "description":\s
                                 "latitude":\s
                                 "longitude":\s
                                 "timeTravel":\s
                                 "targetAudience":
                                 "highlights":\s
                                 "timeTemperature":\s
                               }
                             ]
                           }
                         ]
                       \s
                        ## Aqui e o são os feriados fornecido.
            """;
    private String address = "https://date.nager.at/api/v3/PublicHolidays/#year/#acronym";



    public String getData(HolidayRequestDTO request){
        String result = "";


        if(Files.notExists(Path.of(path +"\\"+ request.acronym()+request.year()+".json"))){
            GenerateContentResponse response = apiService.geminiAPI(PROMPT+getHolidays(request));
            result = apiService.extractResponse(response).replace("```","").replace("json","");
        }

        return createFile(request,result);
    }


    public String  createFile(HolidayRequestDTO request , String response){

        File file = new File(path +"\\"+ request.acronym()+request.year()+".json");
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
        try {return client.send(request, HttpResponse.BodyHandlers.ofString()).body();}
        catch (IOException | InterruptedException e) {throw new RuntimeException(e);}
    }

}
