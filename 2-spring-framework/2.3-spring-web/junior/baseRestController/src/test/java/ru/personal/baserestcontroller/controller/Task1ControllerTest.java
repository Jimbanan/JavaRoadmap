package ru.personal.baserestcontroller.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Task1ControllerTest {

    @Test
    void updateTask_whenDataExists_thenUpdated200() throws IOException, InterruptedException {
        //given
        var requestBody = """
                {
                  "id": "id10",
                  "title": "title6",
                  "completed": false
                }
                """;
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/tasks"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        //when
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void updateTask_whenDataNotExists_thenCreate201() throws IOException, InterruptedException {
        //given
        var requestBody = """
                {
                  "id": "id20",
                  "title": "title6",
                  "completed": false
                }
                """;
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/tasks"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        //when
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //then
        assertThat(response.statusCode()).isEqualTo(200);
    }
}