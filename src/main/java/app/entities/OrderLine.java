package app.entities;

public class OrderLine
{
    private int orderId;
    private int quantity;
    private Cupcake cupcake;
    private String topFlavour;
    private String bottomFlavour;
    private int price;

    public OrderLine(int orderId, int quantity, Cupcake cupcake, int price, String topFlavour, String bottomFlavour)
    {
        this.orderId = orderId;
        this.quantity = quantity;
        this.cupcake = cupcake;
        this.price = price;
        this.topFlavour = topFlavour;
        this.bottomFlavour = bottomFlavour;
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

    public String getTopFlavour() { return topFlavour; }

    public String getBottomFlavour() { return bottomFlavour; }

}
