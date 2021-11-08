package johan.ekdahl;

class Ships {
    private int length;
    private final char direction;

    public int getLength() {
        return length;
    }



    public char getDirection() {
        return direction;
    }


    public Ships(String shipType, char direction) {
        this.direction = direction;
        switch (shipType){
            case "Carrier" -> this.length = 5;
            case "Battleship" -> this.length = 4;
            case "Cruiser", "Submarine" -> this.length = 3;
            case "Destroyer" -> this.length = 2;
        }
    }
}
