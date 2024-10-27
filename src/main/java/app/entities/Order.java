package app.entities;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class Order
{
    private final int orderId;
    private final String name;
    private final LocalDate datePlaced;
    private LocalDate datePaid;
    private LocalDate dateCompleted;
    private String status;
    private final User user;
    private List<OrderLine> orderLines;

    public Order(int orderId, String name, String status, User user, List<OrderLine> orderLines)
    {
        this.orderId = orderId;
        this.name = name;
        this.status = status;
        this.user = user;
        this.orderLines = orderLines;

        this.datePlaced = LocalDate.now();
    }

    public  Order (int order_id, String name, Date date_placed, Date date_paid, Date date_completed, String status, User user){
        this.orderId = order_id;
        this.name = name;
        this.datePlaced = date_placed.toLocalDate();
        this.datePaid = date_paid==null?null:date_paid.toLocalDate();
        this.dateCompleted = date_completed==null?null:date_completed.toLocalDate();
        this.status = status;
        this.user = user;

    }

    public int getOrderId() {
        return orderId;
    }

    public int getOrderTotal()
    {
        int orderTotal = 0;
        for (OrderLine orderLine : orderLines)
        {
            orderTotal += orderLine.getPrice();
        }
        return orderTotal;
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

    public void updateStatus(String updatedStatus)
    {

        if(updatedStatus.equalsIgnoreCase("betalt"))
        {
            datePaid = LocalDate.now();
        }
        else if(updatedStatus.equalsIgnoreCase("leveret"))
        {
            dateCompleted = LocalDate.now();
        }

        status = updatedStatus;
    }
}
