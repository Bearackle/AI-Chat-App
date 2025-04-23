package com.workspace.llmsystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class LlamaService {

    @Value("${llama.path}")
    private String llamaCppPath;

    @Value("${llama.model}")
    private String modelPath;
    private String fullPrompt;

    public Flux<String> streamResponse(String prompt) {
        return Flux.create(emitter -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        llamaCppPath,
                        "--model", modelPath,
                        "--prompt", "\""+prompt+"\"",
                        "--stream"
                );
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        emitter.next(line);
                    }
                    emitter.complete();
                }
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                emitter.error(e);
            }
        });
    }

    public Mono<String> nonStreamResponse(String prompt) {
        return Mono.fromCallable(() -> {
            ProcessBuilder pb = new ProcessBuilder(
                    llamaCppPath,
                    "-m", modelPath,
                    "--prompt", "\"" +prompt + "\"",
                    "-t", "4",
                    "-n","100"
            );
            Process process = pb.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            process.waitFor();
            return output.toString();
        });
    }
    public String getValue(){
        String prompt = "Please enter the following value:";
            ProcessBuilder pb = new ProcessBuilder(
                    llamaCppPath,
                    "--model", modelPath,
                    "--prompt","\""+prompt+"\"",
                    "--stream"
            );
            fullPrompt = String.join(" ", pb.command());
            return fullPrompt;
    }
}