package app.entities;

public class OrderLine
{
    private int orderId;
    private int quantity;
    private Cupcake cupcake;
    private String topFlavour;
    private String bottomFlavour;
    private int price;
    private int bottomFlavourId;
    private int topFlavorId;

    public OrderLine(int orderId, int quantity, Cupcake cupcake, int price, String topFlavour, String bottomFlavour, int bottomFlavorId, int topFlavourId)
    {
        this.orderId = orderId;
        this.quantity = quantity;
        this.cupcake = cupcake;
        this.price = price;
        this.topFlavour = topFlavour;
        this.bottomFlavour = bottomFlavour;
        this.bottomFlavourId = bottomFlavorId;
        this.topFlavorId = topFlavourId;
    }

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

    public String getTopFlavour() { return topFlavour; }

    public String getBottomFlavour() { return bottomFlavour; }

    public int getTopFlavorId() { return topFlavorId;}
    public int getBottomFlavourId() { return bottomFlavourId;}
}
