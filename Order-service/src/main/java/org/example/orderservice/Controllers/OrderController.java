package org.example.orderservice.Controllers;

import org.example.orderservice.entities.Order;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.services.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController//Retourne JSON automatiquement
@RequestMapping("/orders")
//@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // CREATE
    @PostMapping
    public Order createOrder(@RequestBody Order order) {//JSON → Objet Java cote serveur
        return orderService.createOrder(order);
    }

    //  all
    @GetMapping
    public List<Order> getAll() {
        return orderService.getAll();
    }

    // by id
    @GetMapping("/{id}")
    public Optional<Order> getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    // by user
    @GetMapping("/user/{userId}")
    public List<Order> getByUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    // UPDATE - status
    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return orderService.updateStatus(id, status);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}