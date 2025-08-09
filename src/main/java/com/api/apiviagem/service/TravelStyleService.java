package com.api.apiviagem.service;


import com.api.apiviagem.DTO.response.TravelStyleResponseDTO;
import com.api.apiviagem.model.TravelStyle;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelStyleService {


    @Autowired
    private APIService apiService;
    @Autowired
    private Gson gson;

    public TravelStyleResponseDTO createTravelStyles(String slug){

        String prompt = """
                Quero que você atue como um gerador de JSON para estilos de viagem. \s
                Você receberá no final desta mensagem um estilo de viagem (por exemplo: "Aventura & Ecoturismo"). \s
                Com base nesse estilo, crie e retorne apenas um JSON no seguinte formato exato:
                
                {
                  "slug": "slug-do-estilo-em-kebab-case",
                  "styleName": "Nome do estilo exatamente como passado",
                  "titlePage": "Título da página no formato: [Destinos de {styleName} no Brasil | Destinify]",
                  "descriptionPage": "Descrição breve e atraente sobre o estilo de viagem, com foco no Brasil.",
                  "destinations": [
                    {
                      "name": "Nome do destino",
                      "location": "Cidade/Estado, País",
                      "slug": "slug-do-destino-em-kebab-case",
                      "image": null,
                      "description": "Descrição envolvente do destino, com 1 a 2 frases."
                    }
                  ]
                }
                
                Regras:
                - Retorne apenas JSON válido, sem comentários ou texto fora do JSON.
                - A lista "destiny" deve ter pelo menos 3 destinos relevantes ao estilo.
                - Use descrições criativas e atrativas.
                - Não invente locais inexistentes.
                
                Estilo de viagem:
                
                """ +slug;

        GenerateContentResponse response = apiService.geminiAPI(prompt);
        String result = apiService.extractResponse(response).replace("```","").replace("json","");
        return gson.fromJson(result, TravelStyleResponseDTO.class);
    }
}
