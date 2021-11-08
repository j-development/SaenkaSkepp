package johan.ekdahl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipsTest {

    char[] directions = {'V', 'H'};
    String[] Shiptypes = {"Carrier",
            "Battleship",
            "Cruiser",
            "Submarine",
            "Destroyer" };
    int[] lengths = {5,4,3,3,2};



    @Test
    void getLength() {
        for (int i = 0; i < directions.length; i++){
            for (int j = 0; j < Shiptypes.length; j++){
                Ships ship = new Ships(Shiptypes[j], directions[i]);
                assertEquals(lengths[j],ship.getLength());
            }
        }
    }

    @Test
    void getDirection() {
    }
}