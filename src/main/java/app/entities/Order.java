package app.entities;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class Order
{
    private int orderId;
    private String name;
    private LocalDate datePlaced;
    private LocalDate datePaid;
    private LocalDate dateCompleted;
    private String status;
    private User user;
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

    public  Order (int order_id, String name, Date date_placed, Date date_paid, Date date_completed, String status, int user_id){
        this.orderId = order_id;
        this.name = name;
        this.datePlaced = LocalDate.now();
        this.datePaid = LocalDate.now();
        this.dateCompleted = LocalDate.now();
        this.status = status;


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
