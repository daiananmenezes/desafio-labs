package com.labs.desafio.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.labs.desafio.model.Order;
import com.labs.desafio.model.Product;
import com.labs.desafio.model.User;

public class OrderService {
    private Map<Integer, Set<String>> userConflictMap = new HashMap<>();

    public List<User> processFiles(File... files) throws IOException {
        Map<Integer, User> userMap = new HashMap<>();
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    parseLine(line, userMap);
                }
            }
        }

        // Relatar conflitos de IDs de usuário
        reportConflicts();

        // Ordena os usuários pelo user_id
        List<User> users = userMap.values().stream()
                .sorted(Comparator.comparingInt(User::getUserId))
                .collect(Collectors.toList());

        return users;
    }

    private void parseLine(String line, Map<Integer, User> userMap) {
        try {
            int userId = Integer.parseInt(line.substring(0, 10).trim());
            String name = line.substring(10, 55).trim();
            int orderId = Integer.parseInt(line.substring(55, 65).trim());
            int productId = Integer.parseInt(line.substring(65, 75).trim());
            BigDecimal value = new BigDecimal(line.substring(75, 87).trim().replace(',', '.'));
            String dateStr = line.substring(87, 95).trim();
            String date = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6, 8);

            if (userMap.containsKey(userId)) {
                User existingUser = userMap.get(userId);
                if (!existingUser.getName().equals(name)) {
                    // Relatar conflito
                    userConflictMap.computeIfAbsent(userId, k -> new HashSet<>()).add(existingUser.getName());
                    userConflictMap.get(userId).add(name);
                    return;
                }
            }

            User user = userMap.computeIfAbsent(userId, k -> new User(userId, name));

            Order order = user.getOrders().stream()
                    .filter(o -> o.getOrderId() == orderId)
                    .findFirst()
                    .orElseGet(() -> {
                        Order newOrder = new Order();
                        newOrder.setOrderId(orderId);
                        newOrder.setDate(date);
                        newOrder.setTotal(BigDecimal.ZERO);
                        newOrder.setProducts(new ArrayList<>());
                        user.addOrder(newOrder);
                        return newOrder;
                    });

            order.setTotal(order.getTotal().add(value));
            order.getProducts().add(new Product(productId, value));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            System.err.println("Error parsing line: " + line);
            e.printStackTrace();
        }
    }

    private void reportConflicts() {
        if (!userConflictMap.isEmpty()) {
            System.out.println("Usuarios com id duplicados:");
            for (Map.Entry<Integer, Set<String>> entry : userConflictMap.entrySet()) {
                System.out.println("ID: " + entry.getKey() + " - Nomes em Conflito: " + entry.getValue());
            }
        }
    }

    public String convertUsersToJson(List<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (User user : users) {
            sb.append("  {\n");
            sb.append("    \"user_id\": ").append(user.getUserId()).append(",\n");
            sb.append("    \"name\": \"").append(user.getName()).append("\",\n");
            sb.append("    \"orders\": [\n");

            List<Order> sortedOrders = user.getOrders().stream()
                    .sorted(Comparator.comparingInt(Order::getOrderId))
                    .collect(Collectors.toList());

            for (Order order : sortedOrders) {
                sb.append("      {\n");
                sb.append("        \"order_id\": ").append(order.getOrderId()).append(",\n");
                sb.append("        \"total\": \"").append(order.getTotal().toString()).append("\",\n");
                sb.append("        \"date\": \"").append(order.getDate()).append("\",\n");
                sb.append("        \"products\": [\n");

                List<Product> sortedProducts = order.getProducts().stream()
                        .sorted(Comparator.comparingInt(Product::getProductId))
                        .collect(Collectors.toList());

                for (Product product : sortedProducts) {
                    sb.append("          {\n");
                    sb.append("            \"product_id\": ").append(product.getProductId()).append(",\n");
                    sb.append("            \"value\": \"").append(product.getValue().toString()).append("\"\n");
                    sb.append("          },\n");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sb.append("        ]\n");
                sb.append("      },\n");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("    ]\n");
            sb.append("  },\n");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("]\n");
        return sb.toString();
    }

    public void writeJsonToFile(String json, String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs(); 
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(json);
        }
    }

    public void printSummary(List<User> users) {
        int userCount = users.size();
        int orderCount = users.stream().mapToInt(user -> user.getOrders().size()).sum();
        int productCount = users.stream()
                .flatMap(user -> user.getOrders().stream())
                .mapToInt(order -> order.getProducts().size())
                .sum();

        System.out.println("Resumo:");
        System.out.println("Total Usuarios: " + userCount);
        System.out.println("Total Orders: " + orderCount);
        System.out.println("Total Products: " + productCount);
    }
}
