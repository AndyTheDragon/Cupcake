package app.entities;

public class CupcakeFlavour
{
    private final int cupcakeFlavourId;
    private final int price;
    private final String flavourName;
    private final String flavourDesc;
    private final CupcakeType type;

    public CupcakeFlavour(int cupcakeFlavourId, int price, String flavourName, String flavourDesc, CupcakeType type)
    {
        this.cupcakeFlavourId = cupcakeFlavourId;
        this.price = price;
        this.flavourName = flavourName;
        this.flavourDesc = flavourDesc;
        this.type = type;
    }

    public int getCupcakeFlavourId() {
        return cupcakeFlavourId;
    }

    public int getPrice() {
        return price;
    }

    public String getFlavour()
    {
    public String getName()
    {
        return flavourName;
    }

    public String getFlavour(){
        return "Smag navn: " + flavourName +
                "Beskrivelse: " + flavourDesc;
    }

    public CupcakeType getType() {
        return type;
    }

    public String getCupcakeType(){
        return type.toString();
    }

    @Override
    public String toString()
    {
        return cupcakeFlavourId + ", " +
                type + ", " +
                flavourName + ", " +
                flavourDesc + ", " +
                price + " dkk, ";
    }
}
