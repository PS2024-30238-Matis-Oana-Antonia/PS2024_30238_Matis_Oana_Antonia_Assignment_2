package com.example.carturestibackend.strategy;

import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.Product;

import java.util.Optional;

public class FileGenerator {
    private FileGenerationStrategy strategy;

    public void setStrategy(FileGenerationStrategy strategy) {
        this.strategy = strategy;
    }

    public void generateFile(Optional<Order> orderOptional) {
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            String formattedOrder;
            if (strategy instanceof PdfFileGenerationStrategy) {
                formattedOrder = formatOrderForPDF(order);
            } else if (strategy instanceof TxtFileGenerationStrategy) {
                formattedOrder = formatOrderForTXT(order);
            } else if (strategy instanceof CsvFileGenerationStrategy) {
                formattedOrder = formatOrderForCSV(order);
            } else {
                System.out.println("Unsupported file generation strategy");
                return;
            }
            strategy.generateFile(formattedOrder);
        } else {
            System.out.println("Order not found");
        }
    }

    private String formatOrderForPDF(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bill<br/>")
                .append("----------------------------------------------------------------------<br/><br/>")
                .append("Order ID: ").append(order.getId_order()).append("<br/>")
                .append("Order Date: ").append(order.getOrder_date()).append("<br/><br/>")
                .append("----------------------------------------------------------------------<br/><br/>")
                .append("Customer Details:<br/><br/>")
                .append("Name: ").append(order.getUser().getName()).append("<br/>")
                .append("Address: ").append(order.getUser().getAddress()).append("<br/>")
                .append("Email: ").append(order.getUser().getEmail()).append("<br/><br/>")
                .append("----------------------------------------------------------------------<br/><br/>")
                .append("Products: <br/><br/>");

        for (Product product : order.getProducts()) {
            sb.append(" - ").append(product.getName()).append(": ").append(product.getAuthor()).append("<br/>");
        }
        sb.append("<br/>");
        sb.append("----------------------------------------------------------------------<br/><br/>")
                .append("Total Price: ").append(order.getTotal_price()).append("<br/>");


        return sb.toString();
    }

    private String formatOrderForTXT(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bill\n")
                .append("-----------------------------------------------\n\n")
                .append("Order ID: ").append(order.getId_order()).append("\n")
                .append("Order Date").append(order.getOrder_date()).append("\n\n")
                .append("-----------------------------------------------\n\n")
                .append("Customer Details:\n")
                .append("\tName: ").append(order.getUser().getName()).append("\n")
                .append("\tAddress: ").append(order.getUser().getAddress()).append("\n")
                .append("\tEmail: ").append(order.getUser().getEmail()).append("\n\n")
                .append("-----------------------------------------------\n\n")
                .append("Products:\n\n");

        for (Product product : order.getProducts()) {
            sb.append(" - ").append(product.getName()).append(": ").append(product.getAuthor()).append("\n");
        }
        sb.append("\n");
        sb.append("-----------------------------------------------\n")
                .append("\n")
                .append("Total Price: ").append(order.getTotal_price()).append("\n");

        return sb.toString();
    }

    private String formatOrderForCSV(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bill\n")
                .append("\n")
                .append("Order ID: ").append(order.getId_order()).append("\n")
                .append("Order Date: ").append(order.getOrder_date()).append("\n")
                .append("\n")
                .append("Customer Details:\n")
                .append("\tName: ").append(order.getUser().getName()).append("\n")
                .append("\tAddress: ").append(order.getUser().getAddress()).append("\n")
                .append("\tEmail: ").append(order.getUser().getEmail()).append("\n")
                .append("\n")
                .append("Products:\n");

        for (Product product : order.getProducts()) {
            sb.append(" - ").append(product.getName()).append(": ").append(product.getAuthor()).append("\n");
        }

        sb.append("\n")
                .append("Total Price: ").append(order.getTotal_price()).append("\n");

        return sb.toString();
    }

}
