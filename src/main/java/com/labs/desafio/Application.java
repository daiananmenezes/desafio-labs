package com.labs.desafio;

import com.labs.desafio.controller.OrderController;

public class Application {

    public static void main(String[] args) {
        OrderController controller = new OrderController();
        String inputFilePath1 = "src/main/resources/data/data_1.txt";
        String inputFilePath2 = "src/main/resources/data/data_2.txt";
        String outputFilePath = "src/main/resources/output/output.json";

        controller.processOrders(inputFilePath1, inputFilePath2, outputFilePath);
    }
}
