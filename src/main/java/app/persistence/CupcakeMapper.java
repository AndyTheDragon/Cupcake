package app.persistence;

import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;

import java.util.ArrayList;
import java.util.List;

public class CupcakeMapper
{

    public static List<CupcakeFlavour> getFlavours(CupcakeType cupcakeType, ConnectionPool dbConnection)
    {
        List<CupcakeFlavour> flavours = new ArrayList<>();
        flavours.add(new CupcakeFlavour(1,500,"Chokolade","Chokolade",CupcakeType.TOP));
        return flavours;
    }
}
