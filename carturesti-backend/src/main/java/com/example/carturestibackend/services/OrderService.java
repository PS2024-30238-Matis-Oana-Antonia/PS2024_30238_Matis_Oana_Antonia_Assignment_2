package com.example.carturestibackend.services;

import com.example.carturestibackend.config.RabbitSender;
import com.example.carturestibackend.constants.OrderLogger;
import com.example.carturestibackend.dtos.NotificationRequestDTO;
import com.example.carturestibackend.dtos.OrderDTO;
import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.dtos.mappers.OrderMapper;
import com.example.carturestibackend.entities.*;
import com.example.carturestibackend.repositories.*;
import com.example.carturestibackend.strategy.CsvFileGenerationStrategy;
import com.example.carturestibackend.strategy.FileGenerator;
import com.example.carturestibackend.strategy.PdfFileGenerationStrategy;
import com.example.carturestibackend.strategy.TxtFileGenerationStrategy;
import com.example.carturestibackend.validators.OrderValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle business logic related to orders.
 */
@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderValidator orderValidator;
    private final CartService cartService;
    private final CartRepository cartRepository;

    /**
     * Constructs a new OrderService with the specified OrderRepository.
     *
     * @param orderRepository     The OrderRepository used to interact with order data in the database.
     * @param orderItemRepository
     * @param userRepository
     * @param productRepository
     * @param orderValidator
     * @param cartService
     * @param cartRepository
     */
    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, UserRepository userRepository, ProductRepository productRepository, OrderValidator orderValidator, CartService cartService, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderValidator = orderValidator;

        this.cartService = cartService;
        this.cartRepository = cartRepository;
    }


    private OrderDTO calculateOrderTotals(Order order) {
        List<OrderItem> orderItems = order.getOrderItems(); // Obtain the list of order items

        long totalQuantity = 0;
        double totalPrice = 0;

        if (orderItems != null && !orderItems.isEmpty()) {
            for (OrderItem orderItem : orderItems) {
                Product product = orderItem.getProduct();
                totalQuantity += orderItem.getQuantity();
                totalPrice += (product.getPrice() * orderItem.getQuantity());
            }
        }

        OrderDTO orderDTO = OrderMapper.toOrderDTO(order);
        orderDTO.setTotal_quantity(totalQuantity);
        orderDTO.setTotal_price(totalPrice);

        return orderDTO;
    }


    /**
     * Retrieves all orders from the database.
     *
     * @return A list of OrderDTO objects representing the orders.
     */
    public List<OrderDTO> findOrders() {
        LOGGER.info(OrderLogger.ALL_ORDERS_RETRIEVED); // Changed from error to info
        List<Order> orderList = orderRepository.findAll();
        return orderList.stream()
                .map(OrderMapper::toOrderDTO) // Calculate totals for each order
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id The ID of the order to retrieve.
     * @return The OrderDTO object representing the retrieved order.
     * @throws ResourceNotFoundException if the order with the specified ID is not found.
     */
    public OrderDTO findOrderById(String id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (!orderOptional.isPresent()) {
            LOGGER.error(OrderLogger.ORDER_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Order.class.getSimpleName() + " with id: " + id);
        }
        return OrderMapper.toOrderDTO(orderOptional.get());
    }

    /**
     * Inserts a new order into the database.
     *
     * @param orderDTO The OrderDTO object representing the order to insert.
     * @return The ID of the newly inserted order.
     */

    @Autowired
    private RabbitSender rabbitSender;

    @Transactional
    public String insert(OrderDTO orderDTO) {
        Logger logger = LoggerFactory.getLogger(getClass());

        // Retrieve user
        User user = userRepository.findById(String.valueOf(orderDTO.getId_user()))
                .orElseThrow(() -> {
                    String message = String.format(OrderLogger.ORDER_NOT_FOUND_BY_ID, orderDTO.getId_user());
                    logger.error(message);
                    return new ResourceNotFoundException(message);
                });

        logger.info(OrderLogger.ORDER_RETRIEVED_BY_ID, user.getId_user());

        // Create a new order
        Order order = new Order();
        order.setOrder_date(orderDTO.getOrder_date());
        order.setUser(user);

        // Initialize products list if null
        if (order.getProducts() == null) {
            order.setProducts(new ArrayList<>());
        }

        double totalPrice = 0.0;

        // Iterate over products in the order
        for (String productId : orderDTO.getId_products()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        String message = String.format(OrderLogger.ORDER_NOT_FOUND_BY_ID, productId);
                        logger.error(message);
                        return new ResourceNotFoundException(message);
                    });

            if (product.getStock() <= 0) {
                String message = "Insufficient stock for product with ID: " + productId;
                logger.error(message);
                throw new RuntimeException(message);
            }

            // Determine the price to use
            double priceToUse = Math.min(product.getPrice(), product.getPrice_promotion() > 0 ? product.getPrice_promotion() : Double.MAX_VALUE);

            // Add product to order
            order.getProducts().add(product);

            // Update total price
            totalPrice += priceToUse;

            // Decrement stock
            product.setStock(product.getStock() - 1);

            // Save updated product stock
            productRepository.save(product);

            logger.info("Product with ID {} added to order. Price used: {}", product.getId_product(), priceToUse);
        }

        // Set total price of the order if there are products in the order
        if (!order.getProducts().isEmpty()) {
            order.setTotal_price(totalPrice);
            logger.info("Total price calculated for order: {}", totalPrice);
        }

        // Save the order
        try {
            order = orderRepository.save(order);
            logger.info(OrderLogger.ORDER_INSERTED, order.getId_order());

            // Send notification email
            sendNotificationEmail(user, order);

            return order.getId_order();
        } catch (Exception e) {
            logger.error("Failed to save order: {}", e.getMessage());
            throw new RuntimeException("Failed to save order: " + e.getMessage());
        }
    }


    public String findCartIdByUser(User user) {
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if (cartOptional.isPresent()) {
            return cartOptional.get().getId_cart();
        } else {
            LOGGER.error("No cart found for user with ID: {}", user.getId_user());
            throw new ResourceNotFoundException("Cart for user " + user.getId_user() + " not found.");
        }
    }
    @Transactional
    public String placeOrder(String userId, OrderDTO orderDTO) {
        Logger logger = LoggerFactory.getLogger(getClass());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    String message = String.format("User not found with ID: %s", userId);
                    logger.error(message);
                    throw new ResourceNotFoundException(message);
                });

        logger.info("User {} retrieved", userId);
        String cartId = findCartIdByUser(user);
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> {
                    String message = String.format("Cart not found with ID: %s", cartId);
                    logger.error(message);
                    throw new ResourceNotFoundException(message);
                });

        logger.info("Cart {} retrieved", cartId);
        List<ProductDTO> cartProducts = cartService.getProductsInCart(cartId);

        Order order = new Order();
        order.setUser(user);

        LocalDate orderDate = orderDTO.getOrder_date();
        if (orderDate == null) {
            throw new IllegalArgumentException("Order date is required");
        }
        order.setOrder_date(orderDate);
        order.setProducts(new ArrayList<>());
        double totalPrice = 0.0;

        long totalQuantity = 0; // Variabila pentru stocarea cantitatii totale din cos
        for (ProductDTO productDTO : cartProducts) {
            String productId = productDTO.getId_product();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        String message = String.format("Product not found with ID: %s", productId);
                        logger.error(message);
                        throw new ResourceNotFoundException(message);
                    });

            double priceToUse = Math.min(product.getPrice(), product.getPrice_promotion() > 0 ? product.getPrice_promotion() : Double.MAX_VALUE);
            long quantityInCart = productDTO.getStock();

            if (quantityInCart <= 0) {
                throw new IllegalArgumentException("Quantity in cart must be greater than 0");
            }

            if (quantityInCart > product.getStock()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + productId);
            }

            // Create OrderItem and set quantity
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantityInCart);
            orderItem.setPrice_per_unit(priceToUse);
            orderItem.setCart(cart);

            // Add OrderItem to Order
            order.getProducts().add(orderItem.getProduct());

            // Update total price
            totalPrice += priceToUse * quantityInCart;

            // Increment total quantity with quantity from the current order item
            totalQuantity += quantityInCart;

            logger.info("Product with ID {} added to order. Price used: {}", productId, priceToUse);
        }

        if (!order.getProducts().isEmpty()) {
            order.setTotal_price(totalPrice);
            order.setTotal_quantity(totalQuantity);
            order.setStatus("PENDING");
            logger.info("Total price calculated for order: {}", totalPrice);
        }

        try {
            order = orderRepository.save(order);
            logger.info(OrderLogger.ORDER_INSERTED, order.getId_order());
            sendNotificationEmail(user, order);
            return order.getId_order();
        } catch (Exception e) {
            logger.error("Failed to save order: {}", e.getMessage());
            throw new RuntimeException("Failed to save order: " + e.getMessage());
        }
    }


    private void sendNotificationEmail(User user, Order order) {
        NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
        notificationRequestDTO.setSubject("Order Confirmation");
        notificationRequestDTO.setBody(buildEmailMessage(user, order));
        notificationRequestDTO.setEmail(user.getEmail());
        rabbitSender.send(notificationRequestDTO);
    }

    private void sendNotificationEmail2(User user, String filePath) {
        NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
        notificationRequestDTO.setSubject("Order bill");
        notificationRequestDTO.setBody(buildEmailMessage2(user,filePath)); // Actualizare aici pentru a utiliza noua metodă
        notificationRequestDTO.setEmail(user.getEmail());
        notificationRequestDTO.setAttachmentPath(filePath);

        rabbitSender.send(notificationRequestDTO);
    }


    public void generateAndSendPdf(String id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            LOGGER.error(OrderLogger.ORDER_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Order.class.getSimpleName() + " with id: " + id);
        }
        Order order = orderOptional.get();
        FileGenerator fileGenerator = new FileGenerator();
        fileGenerator.setStrategy(new PdfFileGenerationStrategy());
        String filePath = fileGenerator.generateFile(order); // Generate the PDF file
        sendNotificationEmail2(order.getUser(), filePath); // Send email with attachment
    }

    public void generateAndSendTxt(String id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            LOGGER.error(OrderLogger.ORDER_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Order.class.getSimpleName() + " with id: " + id);
        }
        Order order = orderOptional.get();
        FileGenerator fileGenerator = new FileGenerator();
        fileGenerator.setStrategy(new TxtFileGenerationStrategy());
        String filePath = fileGenerator.generateFile(order); // Generate the TXT file
        sendNotificationEmail2(order.getUser(), filePath); // Send email with attachment
    }

    public void generateAndSendCsv(String id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            LOGGER.error(OrderLogger.ORDER_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Order.class.getSimpleName() + " with id: " + id);
        }
        Order order = orderOptional.get();
        FileGenerator fileGenerator = new FileGenerator();
        fileGenerator.setStrategy(new CsvFileGenerationStrategy());
        String filePath = fileGenerator.generateFile(order); // Generate the CSV file
        sendNotificationEmail2(order.getUser(), filePath); // Send email with attachment
    }

    /**
     * Deletes an order from the database by its ID.
     *
     * @param id_order The ID of the order to delete.
     * @throws ResourceNotFoundException if the order with the specified ID is not found.
     */
    @Transactional
    public void deleteOrderById(String id_order) {
        Optional<Order> orderOptional = orderRepository.findById(id_order);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setUser(null);
            for (Product product : order.getProducts()) {
                product.setStock(product.getStock() + 1);
                product.setOrders(null);
                productRepository.save(product);
            }
            orderRepository.delete(order);
            LOGGER.debug(OrderLogger.ORDER_DELETED, id_order);
        } else {
            LOGGER.error(OrderLogger.ORDER_NOT_FOUND_BY_ID, id_order);
            throw new ResourceNotFoundException(Order.class.getSimpleName() + " with id: " + id_order);
        }
    }

    /**
     * Updates an existing order in the database.
     *
     * @param id The ID of the order to update.
     * @param orderDTO The updated OrderDTO object representing the new state of the order.
     * @return The updated OrderDTO object.
     * @throws ResourceNotFoundException if the order with the specified ID is not found.
     */
    @Transactional
    public OrderDTO updateOrder(String id, OrderDTO orderDTO) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (!orderOptional.isPresent()) {
            LOGGER.error(OrderLogger.ORDER_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Order.class.getSimpleName() + " with id: " + id);
        }
        Order existingOrder = orderOptional.get();
        existingOrder.setOrder_date(orderDTO.getOrder_date());
        existingOrder.setStatus(orderDTO.getStatus());
        Order updatedOrder = orderRepository.save(existingOrder);
        LOGGER.debug(OrderLogger.ORDER_UPDATED, updatedOrder.getId_order());
        return OrderMapper.toOrderDTO(updatedOrder);
    }


    public List<OrderDTO> findOrdersByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(this::calculateOrderTotals)
                .collect(Collectors.toList());
    }

    private String buildEmailMessage(User user, Order order) {
        StringBuilder body = new StringBuilder();
        body.append("Hello, ").append(user.getName()).append("!<br><br>");
        body.append("Your order has been successfully placed with the following details:<br><br>");
        body.append("Products:<br>");
        body.append("<ul>");
        for (Product product : order.getProducts()) {
            body.append("<li>").append(product.getName()).append(": ").append(product.getAuthor()).append(", ").append(product.getPrice()).append(" lei").append("</li>");
        }
        body.append("</ul>");
        body.append("<br>Total price: ").append(order.getTotal_price()).append("<br><br>");
        body.append("Thank you for shopping with us!<br>");
        body.append("The Cărturești Team.");
        return body.toString();
    }

    private String buildEmailMessage2(User user, String attachmentPath) {
        StringBuilder body = new StringBuilder();
        body.append("Hello, ").append(user.getName()).append("!<br><br>");

        if (attachmentPath.endsWith(".txt")) {
            body.append("Please find attached the text file containing the details of your order.<br>");
        } else if (attachmentPath.endsWith(".pdf")) {
            body.append("Please find attached the PDF file containing the details of your order.<br>");
        } else if (attachmentPath.endsWith(".csv")) {
            body.append("Please find attached the CSV file containing the details of your order.<br>");
        } else {
            body.append("Please find attached the file containing the details of your order.<br>");
        }

        body.append("Thank you for shopping with us!<br>");
        body.append("The Cărturești Team.");

        return body.toString();
    }

}
