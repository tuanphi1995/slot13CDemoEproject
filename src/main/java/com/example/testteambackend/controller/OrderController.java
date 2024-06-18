package com.example.testteambackend.controller;

import com.example.testteambackend.entity.Customer;
import com.example.testteambackend.entity.Order;
import com.example.testteambackend.entity.OrderDTO;
import com.example.testteambackend.entity.Product;
import com.example.testteambackend.repository.CustomerRepository;
import com.example.testteambackend.repository.OrderRepository;
import com.example.testteambackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order orderDetails) {
        try {
            Optional<Customer> customer = customerRepository.findById(orderDetails.getCustomer().getCustomer_id());
            Optional<Product> product = productRepository.findById(orderDetails.getProduct().getProduct_id());

            if (customer.isPresent() && product.isPresent()) {
                orderDetails.setCustomer(customer.get());
                orderDetails.setProduct(product.get());
                orderDetails.setDate(new Date());  // Set current date if needed
                Order savedOrder = orderRepository.save(orderDetails);
                return ResponseEntity.ok(savedOrder);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Integer id, @RequestBody Order orderDetails) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        Optional<Customer> customer = customerRepository.findById(orderDetails.getCustomer().getCustomer_id());
        Optional<Product> product = productRepository.findById(orderDetails.getProduct().getProduct_id());

        if (optionalOrder.isPresent() && customer.isPresent() && product.isPresent()) {
            Order existingOrder = optionalOrder.get();
            existingOrder.setCustomer(customer.get());
            existingOrder.setProduct(product.get());
            existingOrder.setDate(orderDetails.getDate());
            Order updatedOrder = orderRepository.save(existingOrder);
            return ResponseEntity.ok(updatedOrder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> showAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            List<OrderDTO> orderDTOs = orders.stream().map(order -> new OrderDTO(
                    order.getOrderId(),
                    order.getDate(),
                    order.getCustomer().getCustomer_name(),
                    order.getProduct().getProduct_name()
            )).collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}