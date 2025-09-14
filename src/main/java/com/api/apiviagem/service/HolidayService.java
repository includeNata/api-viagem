package com.api.apiviagem.service;


import com.api.apiviagem.DTO.request.HolidayRequestDTO;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
                            - "averageCost": Custo de viagem para viajar da {city} nesse feriado.
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
                                 "averageCost":\s
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
                                 "averageCost":\s
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
            // Monta em chunks para evitar respostas longas quebradas e valida JSON
            result = buildJsonWithChunking(getHolidays(request));
        }

        return createFile(request,result);
    }


    public String  createFile(HolidayRequestDTO request , String response){

        File file = new File(path +"\\"+ request.acronym()+request.year()+".json");
        StringBuilder json = new StringBuilder();
        try {
            if(!file.exists()){
                    if(file.createNewFile()){
                        String safe = sanitizeToJson(response);
                        String pretty = prettyPrintJson(safe);
                        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8)) {
                            writer.write(pretty);
                        }
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

    private String buildJsonWithChunking(String holidaysSource) {
        JsonArray inputArray = JsonParser.parseString(holidaysSource).getAsJsonArray();
        int chunkSize = 10;
        JsonArray merged = new JsonArray();

        for (int start = 0; start < inputArray.size(); start += chunkSize) {
            int end = Math.min(start + chunkSize, inputArray.size());
            JsonArray subArray = new JsonArray();
            for (int i = start; i < end; i++) {
                subArray.add(inputArray.get(i));
            }

            String prompt = PROMPT + "\n\n### Feriados a processar (JSON):\n" + subArray.toString();
            GenerateContentResponse response = apiService.geminiAPI(prompt);
            String text = apiService.extractResponse(response);
            String sanitized = sanitizeToJson(text);

            JsonElement element = JsonParser.parseString(sanitized);
            if (element.isJsonArray()) {
                JsonArray arr = element.getAsJsonArray();
                for (JsonElement item : arr) {
                    merged.add(item);
                }
            } else {
                merged.add(element);
            }
        }

        return prettyPrintJson(merged.toString());
    }

    private String sanitizeToJson(String raw) {
        if (raw == null) return "[]";
        String cleaned = raw.replace("```", "").replace("json", "");
        int firstBracket = cleaned.indexOf('[');
        int lastBracket = cleaned.lastIndexOf(']');
        if (firstBracket >= 0 && lastBracket >= firstBracket) {
            cleaned = cleaned.substring(firstBracket, lastBracket + 1);
        }
        try { JsonParser.parseString(cleaned); return cleaned; } catch (Exception ignored) {}
        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace >= firstBrace) {
            String objectStr = cleaned.substring(firstBrace, lastBrace + 1);
            try { JsonParser.parseString(objectStr); return objectStr; } catch (Exception ignored) {}
        }
        return cleaned;
    }

    private String prettyPrintJson(String json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement element = JsonParser.parseString(json);
            return gson.toJson(element);
        } catch (Exception e) {
            return json;
        }
    }
}
