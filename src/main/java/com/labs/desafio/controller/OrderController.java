package com.labs.desafio.controller;

import com.labs.desafio.model.User;
import com.labs.desafio.service.OrderService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class OrderController {

    public void processOrders(String inputFilePath1, String inputFilePath2, String outputFilePath) {
        OrderService orderService = new OrderService();
        File file1 = new File(inputFilePath1);
        File file2 = new File(inputFilePath2);

        try {
            List<User> listUser = orderService.processFiles(file1, file2);
            String jsonOutput = orderService.convertUsersToJson(listUser);
            orderService.writeJsonToFile(jsonOutput, outputFilePath);
            orderService.printSummary(listUser);
            System.out.println("Saída JSON gravada no arquivo: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
