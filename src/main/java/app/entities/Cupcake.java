package app.entities;

public class Cupcake
{
    private final CupcakeFlavour top;
    private final CupcakeFlavour bottom;

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
        return top.getName() + "-" + bottom.getName() + " Cupcake";
    }
}
