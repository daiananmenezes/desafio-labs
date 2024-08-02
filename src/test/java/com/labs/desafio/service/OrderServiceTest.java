package com.labs.desafio.service;

import com.labs.desafio.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        orderService = new OrderService();
    }

    @Test
    public void testConvertUsersToJson() throws IOException {
        File file1 = new File("src/test/resources/data/data_1.txt");
        File file2 = new File("src/test/resources/data/data_2.txt");

        List<User> users = orderService.processFiles(file1, file2);
        String jsonOutput = orderService.convertUsersToJson(users);

        assertThat(users)
        .filteredOn(user -> user.getUserId() == 2)
        .hasSize(1)
        .extracting(User::getName)
        .containsExactly("Tarsha Powlowski");
        
        assertThat(jsonOutput).isNotEmpty();
        assertThat(users).hasSize(10);
        assertThat(jsonOutput).contains("\"user_id\":");
        assertThat(jsonOutput).contains("\"orders\":");
        assertThat(jsonOutput).contains("\"products\":");
    }

    @Test
    public void testWriteJsonToFile() throws IOException {
        File file1 = new File("src/test/resources/data/data_1.txt");
        File file2 = new File("src/test/resources/data/data_2.txt");
        String outputFilePath = "src/test/resources/output.json";

        List<User> users = orderService.processFiles(file1, file2);
        String jsonOutput = orderService.convertUsersToJson(users);
        orderService.writeJsonToFile(jsonOutput, outputFilePath);

        File outputFile = new File(outputFilePath);
        assertThat(outputFile).exists();
        assertThat(Files.readString(Paths.get(outputFilePath))).isEqualTo(jsonOutput);

        Files.delete(Paths.get(outputFilePath));
    }

    @Test
    public void testUserConflictDetection() throws IOException {
        File file1 = new File("src/test/resources/data/data_1.txt");
        File file2 = new File("src/test/resources/data/data_2.txt");

        List<User> users = orderService.processFiles(file1, file2);

        assertThat(users).filteredOn(user -> user.getUserId() == 1).hasSize(1);
        assertThat(users).hasSize(10); 
    }
}
