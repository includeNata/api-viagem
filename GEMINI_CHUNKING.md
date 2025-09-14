# Funcionalidade de Quebra de Prompts do Gemini

## Visão Geral

Esta funcionalidade permite que prompts grandes sejam automaticamente quebrados em partes menores e enviados sequencialmente para a API do Gemini, com as respostas sendo combinadas em uma resposta final consolidada.

## Como Funciona

### 1. Detecção de Tamanho
- O sistema verifica se o prompt excede o tamanho máximo configurado (padrão: 30.000 caracteres)
- Se for menor, é enviado diretamente
- Se for maior, é processado em partes

### 2. Divisão Inteligente
- O prompt é dividido em partes menores respeitando:
  - Tamanho máximo configurável por parte
  - Pontos naturais de quebra (pontos finais, quebras de linha, etc.)
  - Sobreposição entre partes para manter contexto

### 3. Processamento Sequencial
- Cada parte é enviada individualmente para o Gemini
- Contexto é adicionado a cada parte para manter coerência
- Pausa configurável entre chamadas para evitar rate limiting

### 4. Consolidação
- Todas as respostas são combinadas
- Uma chamada final é feita para consolidar e organizar o resultado
- Fallback em caso de erro na consolidação

## Configuração

### application.yml
```yaml
gemini:
  max-prompt-size: 30000      # Tamanho máximo por parte
  overlap-size: 1000          # Sobreposição entre partes
  delay-between-chunks: 200   # Delay entre chamadas (ms)
  model: gemini-2.0-flash     # Modelo do Gemini
```

### Parâmetros

| Parâmetro | Descrição | Padrão |
|-----------|-----------|--------|
| `max-prompt-size` | Tamanho máximo de cada parte do prompt | 30000 |
| `overlap-size` | Tamanho da sobreposição entre partes | 1000 |
| `delay-between-chunks` | Delay entre chamadas (milissegundos) | 200 |
| `model` | Modelo do Gemini a ser usado | gemini-2.0-flash |

## Benefícios

### 1. Tratamento de Prompts Grandes
- Permite processar prompts que excedem os limites da API
- Mantém a qualidade da resposta mesmo com conteúdo extenso

### 2. Robustez
- Tratamento de erros em cada parte
- Fallback em caso de falha na consolidação
- Logging detalhado para debugging

### 3. Performance
- Pausas configuráveis para evitar rate limiting
- Processamento paralelo possível (futuro)
- Cache de respostas intermediárias

### 4. Flexibilidade
- Configuração via arquivo de propriedades
- Fácil ajuste de parâmetros
- Suporte a diferentes modelos

## Uso

A funcionalidade é transparente para os serviços que usam o `APIService`. Não há mudanças necessárias no código existente:

```java
// Funciona normalmente para prompts pequenos
GenerateContentResponse response = apiService.geminiAPI("Prompt pequeno");

// Automaticamente quebra prompts grandes
GenerateContentResponse response = apiService.geminiAPI("Prompt muito grande...");
```

## Logging

O sistema fornece logs detalhados:

```
INFO  - Processando prompt com 50000 caracteres
INFO  - Prompt grande detectado, quebrando em partes
INFO  - Processando 3 partes do prompt
DEBUG - Processando parte 1/3 com 30000 caracteres
DEBUG - Parte 1/3 processada com sucesso
...
INFO  - Respostas combinadas com sucesso, total de 15000 caracteres
```

## Tratamento de Erros

### Exceções Customizadas
- `GeminiApiException`: Para erros específicos da API
- Tratamento global via `GlobalExceptionHandler`
- Status HTTP apropriado (503 Service Unavailable)

### Estratégias de Fallback
1. Se uma parte falhar, continua com as outras
2. Se a consolidação falhar, tenta resumo simplificado
3. Se tudo falhar, lança exceção com detalhes

## Testes

Testes unitários cobrem:
- Divisão de prompts pequenos e grandes
- Encontros de pontos naturais de quebra
- Combinação de respostas
- Configuração de parâmetros

## Considerações Futuras

### Melhorias Possíveis
1. **Processamento Paralelo**: Enviar partes em paralelo
2. **Cache Inteligente**: Cache de partes processadas
3. **Retry Automático**: Tentativas automáticas em caso de falha
4. **Métricas**: Monitoramento de performance
5. **Streaming**: Respostas em tempo real

### Limitações Atuais
1. Processamento sequencial (não paralelo)
2. Sem cache de partes
3. Sem retry automático
4. Sem métricas detalhadas

## Exemplo de Uso

```java
@Service
public class MeuService {
    
    @Autowired
    private APIService apiService;
    
    public String processarConteudoGrande(String conteudo) {
        // O prompt será automaticamente quebrado se for muito grande
        GenerateContentResponse response = apiService.geminiAPI(conteudo);
        return apiService.extractResponse(response);
    }
}
```

## Monitoramento

Para monitorar o uso da funcionalidade, observe os logs:
- Prompts que são quebrados
- Número de partes criadas
- Tempo de processamento
- Erros em partes individuais

## Troubleshooting

### Problemas Comuns

1. **Rate Limiting**: Aumente `delay-between-chunks`
2. **Timeout**: Reduza `max-prompt-size`
3. **Qualidade da Resposta**: Ajuste `overlap-size`
4. **Performance**: Otimize configurações baseado no uso

### Debug

Ative logs DEBUG para ver detalhes:
```yaml
logging:
  level:
    com.api.apiviagem.service.APIService: DEBUG
```
