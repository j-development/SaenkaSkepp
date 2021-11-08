package johan.ekdahl;

 class Coordinates {
    private int row;
    private int col;
    private char direction;
    private int length;


     public char getDirection() {
         return direction;
     }

     public void setDirection(char direction) {
         this.direction = direction;
     }

     public int getLength() {
         return length;
     }

     public void setLength(int length) {
         this.length = length;
     }

     public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
