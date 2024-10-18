package app.entities;

public class Cupcake {
    private CupcakeFlavor top;
    private CupcakeFlavor bottom;

    public Cupcake(CupcakeFlavor top, CupcakeFlavor bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public CupcakeFlavor getCupcakeBottom() {
        return bottom;
    }

    public CupcakeFlavor getCupcakeTop() {
        return top;
    }

    int getPrice(){
        return bottom.getPrice() + top.getPrice();
    }

    @Override
    public String toString() {
        return "Smag på øverste lag: " + top.getFlavourName() +
                "\nSmag på nederste lag: " + bottom.getFlavourName() +
                "\nTotal pris: " + getPrice();
    }
}
