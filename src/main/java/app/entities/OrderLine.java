package app.entities;

public class OrderLine
{
    private int orderId;
    private int quantity;
    private Cupcake cupcake;
    private int price;


    public OrderLine(int orderId, int quantity, Cupcake cupcake, int price)
    {
        this.orderId = orderId;
        this.quantity = quantity;
        this.cupcake = cupcake;
        this.price = price;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Cupcake getCupcake() {
        return cupcake;
    }

    public int getPrice() {
        return price;
    }


}
