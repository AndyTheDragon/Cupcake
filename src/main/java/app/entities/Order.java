package app.entities;

import java.time.LocalDate;

public class Order {
    private int orderId;
    private String name;
    private LocalDate datePlaced;
    private LocalDate datePaid;
    private LocalDate dateCompleted;
    private String status;
    private User user;
    private List<OrderLine> orderLines;

    public Order(int orderId, String name, String status, User user, List<OrderLine> orderLines) {
        this.orderId = orderId;
        this.name = name;
        this.status = status;
        this.user = user;
        this.orderLines = orderLines;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDatePlaced() {
        return datePlaced;
    }

    public LocalDate getDatePaid() {
        return datePaid;
    }

    public LocalDate getDateCompleted() {
        return dateCompleted;
    }

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void updateStatus(String updatedStatus){
        status = updatedStatus;
    }
}
