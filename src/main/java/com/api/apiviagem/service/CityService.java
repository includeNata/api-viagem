package com.api.apiviagem.service;

import com.api.apiviagem.DTO.response.CityResponseDTO;
import com.api.apiviagem.model.City;
import com.api.apiviagem.model.Image;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class CityService {


    @Autowired
    private Gson gson;

    @Autowired
    private APIService apiService;

    private Map<String, String> map = Map.ofEntries(
            Map.entry("Culture", "Culture"),
            Map.entry("Economy", "Economy"),
            Map.entry("Transportation", "Transportation"),
            Map.entry("Parks and recreation", "Parks_and_recreation"),
            Map.entry("Sports", "Sports"),
            Map.entry("Urban rail", "Urban_rail"),
            Map.entry("Theaters", "Theaters"),
            Map.entry("Museums", "Museums"),
            Map.entry("Festivals", "Festivals"),
            Map.entry("Tourism and recreation", "Tourism_and_recreation"),
            Map.entry("Arts and theater", "Arts_and_theater"),
            Map.entry("Theater","Theater"),
            Map.entry("Events","Events"),
            Map.entry("Transport","Transport"),
            Map.entry("Sport","Sport"),
            Map.entry("Feasts and festivals","Feasts_and_festivals")
    );



    public CityResponseDTO getInformation(String city){

        try {
            String address = "https://en.wikipedia.org/wiki/#1";
            address = address.replace("#1",city);

            Document document = Jsoup.connect(address).get();
            int countP = 0;

            boolean flag = true;
            List<Element> elements = document.getAllElements();
            StringBuilder p = new StringBuilder();
            Element []e = document.getAllElements().toArray(new Element[0]);
            City newCity = new City();

            for(int i=0; i<elements.size(); i++){
                if(map.containsKey(elements.get(i).text()) && elements.get(i).id().equals(map.get(elements.get(i).text())) ){
                    for(int j=i+1;flag && j< elements.size(); j++){

                        if(e[j].tagName().equals("p")){
                            p.append(e[j].text());
                            countP++;
                        }

                        if(e[j].hasClass("mw-heading mw-heading3") && countP > 1){
                            flag = false;
                        }
                    }
                    updateObject(newCity,p.toString(),elements.get(i).text());
                    p.delete(0,p.length());
                    flag = true;
                    countP = 0;
                }
            }

           return  translateText(gson.toJson(newCity),city);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public  CityResponseDTO  translateText(String json,String city){
         String format = """
                 {
                  "culture": "o texto aqui",
                  "economy": "o texto aqui",
                  "transportation":"o texto aqui",
                  "sports": "o texto aqui",
                  "parksAndRecreation": "o texto aqui",
                  "state:":"coloque o nome do estado da cidade aqui",
                  "population": "coloque o valor da população da cidade aqui, apenas a quantidade ",
                  "year_foundation":
                  "latitude":
                  "longitude":
                 }
                 """;
         String propmt = "Traduza o texto para português é otimize, deixe o texto bem curto mas com todas onformações, sua redação (melhore clareza, fluidez e concisão). Em seguida, retorne o resultado no formato JSON, seguindo esta estrutura:"+format+" texto = "+json +" cheque se o json criado e valido. Não meche no Images."+city;
         GenerateContentResponse result = apiService.geminiAPI(propmt);
         String resultS = apiService.extractResponse(result).replace("```","").replace("json","");

         return  gson.fromJson(resultS, CityResponseDTO.class);

    }

    public void updateObject(City city, String text,String element){
        try {
            Class<?> cityClass  = Class.forName("com.api.apiviagem.model.City");
            Map<String, String> htmlIds = Map.ofEntries(
                    Map.entry("Air", "Transportation"),
                    Map.entry("Urban rail", "Transportation"),
                    Map.entry("Theaters", "Parks and recreation"),
                    Map.entry("Museums", "Parks and recreation"),
                    Map.entry("Festivals", "Parks and recreation"),
                    Map.entry("Arts and theater", "Parks and recreation"),
                    Map.entry("Tourism and recreation", "Parks and recreation"),
                    Map.entry("Theater","Parks and recreation"),
                    Map.entry("Events","Parks and recreation"),
                    Map.entry("Transport","Transportation"),
                    Map.entry("Sport","Sports"),
                    Map.entry("Feasts and festivals","Parks and recreation")
            );

            String id;
            Method method;
            if(htmlIds.containsKey(element)){
                id = htmlIds.get(element);

                if(id.equals("Parks and recreation")){

                    method = cityClass.getMethod("setParksAndRecreation", String.class);
                }
                else{
                    method = cityClass.getMethod("set" + map.get(htmlIds.get(element)), String.class);
                }
                method.invoke(city,text);
            }


            if(!htmlIds.containsKey(element) && map.containsKey(element)){

                if(element.equals("Parks and recreation")){
                    method = cityClass.getMethod("setParksAndRecreation", String.class);
                }
                else
                    method = cityClass.getMethod("set" + map.get(element), String.class);

                method.invoke(city,text);
            }


        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {throw new RuntimeException(e);}

    }



}
