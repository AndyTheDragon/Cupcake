package app.entities;

public class Cupcake
{
    private CupcakeFlavour top;
    private CupcakeFlavour bottom;

    public Cupcake(CupcakeFlavour top, CupcakeFlavour bottom)
    {
        this.top = top;
        this.bottom = bottom;
    }

    public CupcakeFlavour getCupcakeBottom() {
        return bottom;
    }

    public CupcakeFlavour getCupcakeTop() {
        return top;
    }

    public int getPrice(){
        return bottom.getPrice() + top.getPrice();
    }

    @Override
    public String toString()
    {
        return "Smag på øverste lag: " + top.getFlavour() +
                "\nSmag på nederste lag: " + bottom.getFlavour() +
                "\nTotal pris: " + getPrice();
    }
}
