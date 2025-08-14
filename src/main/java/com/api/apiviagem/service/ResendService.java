package com.api.apiviagem.service;

import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendService {

    @Value("${RESEND_KEY}")
    private  String RESEND_API_KEY;
    @Value("${EMAIL}")
    private String email;

    private static final String MESSAGE = """
       
            Olá!
            
            Muito obrigado por se inscrever para ter acesso antecipado ao Destinify. Estamos incrivelmente felizes por ter você a bordo!
            
            Você agora faz parte de um grupo exclusivo de viajantes que serão os primeiros a experimentar uma forma totalmente nova e inteligente de planejar viagens de feriado. Chega de planilhas e estresse, a sua única preocupação será escolher o próximo carimbo no passaporte.
            
            Estamos nos ajustes finais para garantir que a plataforma seja perfeita. Em breve, você receberá um e-mail com seu convite de acesso exclusivo.
            
            Um abraço,
            Equipe Destinify
           
            """;

    public String sendEmail(String emailReceiver){
        Resend resend = new Resend(RESEND_API_KEY);
        CreateEmailOptions emailParams = CreateEmailOptions.builder()
                .from(email)
                .to(emailReceiver)
                .subject("Destinify")
                .text(MESSAGE)
                .build();

        try {
            CreateEmailResponse resposta = resend.emails().send(emailParams);
            System.out.println("E-mail enviado com sucesso! ID: " + resposta.getId());
            return  "sucesso";
        } catch (ResendException e) {
            throw new RuntimeException("Falha ao enviar o e-mail: " + e.getMessage(), e);
        }
    }
}
