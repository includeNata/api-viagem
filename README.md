# Destinify — API de Viagens Inteligentes

## 1. Descrição

**Destinify** é uma API REST moderna desenvolvida com **Spring Boot** e **Java 21**, projetada para oferecer sugestões de viagens inteligentes com base em feriados e informações enriquecidas sobre cidades.

A API utiliza a **IA Generativa do Google Gemini** para enriquecer conteúdo, realiza **web scraping** com **Jsoup** para extrair dados públicos, e emprega um sistema de **cache local** para otimizar o desempenho e reduzir chamadas externas.

Seu propósito é fornecer roteiros de viagem criativos e conteúdos detalhados sobre destinos, por meio de endpoints RESTful bem definidos.

---

## 2. Tecnologias Utilizadas

- **Backend:** Spring Boot 3.5.0  
- **Linguagem:** Java 21  
- **IA Generativa:** Google Gemini (google-genai:1.2.0)  
- **Web Scraping:** Jsoup 1.20.1  
- **Processamento JSON:** Gson 2.13.0  
- **Gerenciamento de Dependências:** Apache Maven  

---

## 3. Funcionalidades

A API oferece dois endpoints principais:

- **Geração de Roteiros de Feriados:** Sugere destinos com base em feriados nacionais, otimizando cada sugestão com IA.  
- **Informações de Cidades:** Extrai e resume conteúdos da Wikipedia e Wikimedia Commons, traduzindo e formatando os dados para entregar um guia turístico rico e personalizado.

---

## 4. Pré-requisitos

Antes de iniciar, certifique-se de ter:

- **JDK 21** (ou superior)  
- **Apache Maven 3.9+**  
- **Chave de API do Google Gemini** — disponível no [Google AI Studio](https://aistudio.google.com/)  

---

## 5. Endpoints

### 5.1. Gerar Roteiros de Feriados

- **Método:** `POST`  
- **URL:** `/api/v1/holiday`  
- **Descrição:** Recebe a origem, ano e sigla do país. Busca os feriados e utiliza IA para gerar sugestões de viagem. Os resultados são armazenados em cache localmente.

**Corpo da Requisição:**
```json
{
  "year": 2025,
  "origin": "São Paulo",
  "acronym": "BR"
}
```
Resposta (200 OK):
Array JSON com sugestões de destino para cada feriado.

### 5.2. Obter Informações da Cidade
- **Método:** `POST`
- - **URL:** `/api/v1/city`  
- **Descrição:** Busca informações culturais, econômicas, turísticas e de infraestrutura da cidade informada, utilizando scraping e IA para enriquecer o conteúdo.

Corpo da Requisição:
```json
{
  "city": "Paris",
  "touristSpots": ["Torre Eiffel", "Museu do Louvre"]
}
```
Resposta (200 OK):
Objeto JSON contendo descrições otimizadas e URLs de imagens dos pontos turísticos.

## 6. Tratamento de Erros
A API implementa um tratamento global de exceções com @ControllerAdvice, garantindo que todas as respostas de erro sigam um formato padronizado e seguro.

Exemplo (404 Not Found):
```json
{
  "message": "A página da Wikipedia para a cidade 'CidadeInexistente123' não foi encontrada.",
  "timestamp": "2025-06-26T20:15:45.123456Z",
  "status": 404,
  "path": "/api/v1/city"
}
```

## 7. Arquitetura e Boas Práticas
- Código Limpo e Modular: A estrutura segue princípios SOLID e Domain-Driven Design (DDD), promovendo organização, testes e manutenção eficiente.

- Centralização de Exceções: O uso de @ControllerAdvice evita repetição de lógica de erro nos controllers.

- Exceções Customizadas: Classes como ResourceNotFoundException e GeminiApiException permitem um tratamento de erros preciso e expressivo.


