package com.microsoft.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.ai.openai.models.ImageGenerationOptions;
import com.azure.ai.openai.models.ImageLocation;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.models.ResponseError;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebServlet("/animalgenerator")
public class AnimalGenerator extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(AnimalGenerator.class);
    private OpenAIAsyncClient client;

    @Override
    public void init() throws ServletException {
        super.init();
        this.client = initializeOpenAIClient();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");

        String animal = req.getParameter("animal");
        if (animal == null || animal.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'animal' parameter");
            return;
        }

        try (PrintWriter out = resp.getWriter()) {
            writeHtmlHeader(out);
            processRequest(animal, out);
            writeHtmlFooter(out);
        } catch (Exception e) {
            log.error("Error during OpenAI call", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing the OpenAI response");
        }
    }

    private void processRequest(String animal, PrintWriter out) {
        Mono<ChatCompletions> chatCompletionsMono = getChatCompletions(animal);

        chatCompletionsMono.flatMapMany(chatCompletions ->
        Flux.fromIterable(chatCompletions.getChoices())
                .flatMapSequential(choice -> {
                    ChatMessage message = choice.getMessage();
                    String messageAsString = message.getContent();
                    out.println("<p>Story: " + messageAsString + "</p>");

                    return getImageUrlFromDallE(messageAsString)
                            .doOnSuccess(imageUrl -> {
                                out.println("<p>Generated Image:</p>");
                                out.println("<img src=\"" + imageUrl + "\" alt=\"Generated Image of " + animal + "\">");
                            });
                })
        ).then().block();
    }

    private OpenAIAsyncClient initializeOpenAIClient() {
        String azureOpenaiKey = System.getenv("AZURE_OPENAI_KEY");
        String endpoint = System.getenv("AZURE_OPENAI_ENDPOINT");

        return new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(azureOpenaiKey))
                .buildAsyncClient();
    }

    private Mono<ChatCompletions> getChatCompletions(String animal) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage(ChatRole.SYSTEM, "You are an AI assistant that writes stories."));
        chatMessages.add(new ChatMessage(ChatRole.USER, "Create a 30 word story about a " + animal + "."));

        final ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages);
        return client.getChatCompletions("GPT-4", options);
    }

    private Mono<String> getImageUrlFromDallE(String animal) {

        ImageGenerationOptions imageGenerationOptions = new ImageGenerationOptions(
                "High definition image of " + animal);

        return client.getImages(imageGenerationOptions)
                .flatMap(images -> {
                    for (ImageLocation imageLocation : images.getData()) {
                        ResponseError error = imageLocation.getError();

                        if (error != null) {
                            log.error("Image generation operation failed. Error code: %s, error message: %s.%n",
                                    error.getCode(), error.getMessage());

                            return Mono.error(new RuntimeException("Image generation operation failed"));
                        } else {
                            return Mono.just(imageLocation.getUrl());
                        }
                    }

                    return Mono.empty();
                });
    }

    private void writeHtmlHeader(PrintWriter out) {
        out.println("<html><head><style>");
        out.println(
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; text-align: center; padding: 50px; }");
        out.println("h1 { color: #333; }");
        out.println("p { color: #555; }");
        out.println("img { max-width: 500px; height: auto; border: 1px solid #ddd; border-radius: 8px; }");
        out.println("</style></head><body>");
        out.println("<h1>Animal Story Generator</h1>");
    }

    private void writeHtmlFooter(PrintWriter out) {
        out.println("</body></html>");
    }
}
