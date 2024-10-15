package app.entities;

public class Cupcake {

    private int id;
    private String orderName;
    private int quantity;

    public Cupcake(int id, String orderName, int quantity) {
        this.id = id;
        this.orderName = orderName;
        this.quantity = quantity;
    }

    public  int getId() { return id; }

    public  String getOrderName() { return orderName; }

    public int getQuantity() { return quantity; }

    @Override
    public String toString() {
        return "Cupcake [" +
                ", order id: " + id + '\'' +
                ", order name: " + orderName + '\'' +
                ", quantity: " + quantity + '\'' +
                "]";
    }
}

