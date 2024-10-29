package app.entities;

public class OrderLine
{
    private final int orderId;
    private final int quantity;
    private final Cupcake cupcake;
    private final int price;


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
