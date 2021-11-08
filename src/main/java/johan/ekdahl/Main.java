package johan.ekdahl;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        boolean gameOn = true;
        int matrixSize = 10;
        FieldSymbols symbols = new FieldSymbols();

        //Ship-type and number of ships store in HashMap
        Map<String, Integer> ships = new HashMap<>();


        // Carrier = 5 Slots
        ships.put("Carrier", 1);
        // Battleship = 4 Slots
        ships.put("Battleship", 1);
        // Cruiser = 3 Slots
        ships.put("Cruiser", 1);
        // Submarine = 3 Slots
        ships.put("Submarine", 1);
        // Destroyer = 2 Slots
        ships.put("Destroyer", 1);


        //Init Create  Player A  Human player
        String[][] matrixA_public = fillMatrix(matrixSize,symbols);
        String[][] matrixA_private = placeOnMatrix(matrixSize, ships, symbols, scan);

        //Init Create  Player B  Bot player
        String[][] matrixB_public = fillMatrix(matrixSize,symbols);
        String[][] matrixB_private = placeOnMatrixAuto(matrixSize, ships, symbols);


        System.out.println("Deployment Done");


        while(gameOn){
            //Player A and Player B print
            printMatrix(matrixA_private, matrixB_public, matrixSize);
            //Player A Shoots
            gameOn = playerAShoots(matrixB_private, matrixB_public, symbols, matrixSize, scan, gameOn);
            //Player B Shoots
            gameOn = playerBShoots(matrixA_private, matrixA_public, symbols, matrixSize, gameOn);
        }


    }

    private static boolean playerBShoots(String[][] matrixA_private, String[][] matrixA_public, FieldSymbols symbols, int matrixSize, boolean gameOn) {
        Coordinates coordinates = new Coordinates();
        // Getting coordinates for our computer player(Player B)
        // Keep rolling coordinates as long as we keep getting coordinates we have already shot
        do {
            coordinates.setRow(new Random().nextInt(10));
            coordinates.setCol(new Random().nextInt(10));
        } while (Objects.equals(matrixA_private[coordinates.getRow()][coordinates.getCol()], symbols.miss) || Objects.equals(matrixA_private[coordinates.getRow()][coordinates.getCol()], symbols.hit));

        //Announce if coordinates hits or misses target in the Player A's Matrix
        if(Objects.equals(matrixA_private[coordinates.getRow()][coordinates.getCol()], symbols.ship)){
            System.out.println("Player B Hit!");
            matrixA_public[coordinates.getRow()][coordinates.getCol()] = matrixA_private[coordinates.getRow()][coordinates.getCol()] = symbols.hit;
        }
        else{
            System.out.println("Player B Missed!");
            matrixA_public[coordinates.getRow()][coordinates.getCol()] = matrixA_private[coordinates.getRow()][coordinates.getCol()] = symbols.miss;
        }
        //Always check after shots fired, if player has won
        if(checkIfAnyPlayerWon(matrixA_private, symbols, matrixSize)){
            System.out.println("Player B Have Won, Congratulations!");
            gameOn = false;
        }
        return gameOn;

    }

    private static boolean playerAShoots(String[][] matrixB_private, String[][] matrixB_public, FieldSymbols symbols, int matrixSize, Scanner scan, boolean gameOn) {
        boolean gotCaught = true;
        Coordinates coordinates = new Coordinates();

        coordinates.setRow(Integer.MIN_VALUE);
        coordinates.setCol(Integer.MIN_VALUE);
        System.out.println("Time To Fire, Give Me Your Coordinates Captain!");

        // Getting coordinates for Human player(Player A)
        // Keep asking for coordinates as long as user keep giving coordinates we have already shot
        // Also check for valid integer inputs
        do {
            if(coordinates.getRow() != Integer.MIN_VALUE){
                System.out.println("Sorry, Invalid Coordinates, Please Try Again!");
            }
            do {
                System.out.println("Choose Row: ");
                if(scan.hasNextInt()){
                    coordinates.setRow(scan.nextInt()-1);
                    gotCaught = false;
                }
                else{
                    scan.nextLine();
                    System.out.println("Enter a valid Integer value");
                }
            } while (coordinates.getRow() > matrixSize || coordinates.getRow() < 1 && gotCaught);
            gotCaught = true;
            do {
                System.out.println("Choose Col: ");
                if(scan.hasNextInt()){
                    coordinates.setCol(scan.nextInt()-1);
                    gotCaught = false;
                }
                else{
                    scan.nextLine();
                    System.out.println("Enter a valid Integer value");
                }
            } while (coordinates.getCol() > matrixSize || coordinates.getCol() < 1 && gotCaught);

        } while (Objects.equals(matrixB_public[coordinates.getRow()][coordinates.getCol()], symbols.miss) || Objects.equals(matrixB_public[coordinates.getRow()][coordinates.getCol()], symbols.hit));


        //Announce if coordinates hits or misses target in the Matrix
        if(Objects.equals(matrixB_private[coordinates.getRow()][coordinates.getCol()], symbols.ship)){
            System.out.println("Direct Hit!");
            matrixB_public[coordinates.getRow()][coordinates.getCol()] = matrixB_private[coordinates.getRow()][coordinates.getCol()] = symbols.hit;
        }
        else{
            System.out.println("You Missed!");
            matrixB_public[coordinates.getRow()][coordinates.getCol()] = matrixB_private[coordinates.getRow()][coordinates.getCol()] = symbols.miss;
        }
        //Always check after shots fired, if player has won
        if(checkIfAnyPlayerWon(matrixB_private, symbols, matrixSize)){
            System.out.println("Player A Have Won, Congratulations!");
            gameOn = false;
        }
            return gameOn;
    }

    private static boolean checkIfAnyPlayerWon(String[][] matrix, FieldSymbols symbols, int matrixSize) {
        int shipCounter = 0;
        for (int i = 0; i < matrixSize; i++){
            for (int j = 0; j < matrixSize; j++){
                if(Objects.equals(matrix[i][j], symbols.ship)){
                    shipCounter += 1;
                }
            }
        }
        return shipCounter == 0;

    }

    private static String[][] placeOnMatrixAuto(int matrixSize, Map<String, Integer> ships, FieldSymbols symbols) {
        //Pretty much the same as placeOnMatrix but with auto-placement for our computer player(Player B)

        //Matrix Variable filled with fill/water
        String[][] matrix = fillMatrix(matrixSize,symbols);


        int numberOfShips;

        Set<Map.Entry<String, Integer>> entrySet = ships.entrySet();
        //Player B placing all his ships, announcing with a println which ship
        for (Map.Entry<String, Integer> set : entrySet){
            numberOfShips = set.getValue();
            if(numberOfShips > 0){

                for(int i = 0; i < numberOfShips; i++) {
                    System.out.println("Player B placing: " + set.getKey() + " " +  (numberOfShips-i));
                    Ships battleship = new Ships(set.getKey(), new Random().nextInt(2) == 1 ? 'V' : 'H');
                    generatePlacement(matrix, battleship, matrixSize, symbols, new Scanner(System.in), true);

                }
            }

        }

        return matrix;
    }

    private static void printMatrix(String[][] matrixA, String[][] matrixB, int matrixSize) {
        //Always prints two Matrixs side by side
        System.out.print(" A ");
        for (int i = 0; i < matrixSize; i++){
            System.out.print(i + 1 + " ");
        }
        System.out.print("    B ");
        for (int i = 0; i < matrixSize; i++){
            System.out.print(i + 1 + " ");
        }
        System.out.println();
        for(int row = 0; row < matrixSize; row++){
            if(row+1>9){
                System.out.print((row + 1) + " ");
            }
            else{
                System.out.print(" " + (row + 1) + " ");
            }
            for (int col = 0; col < matrixSize; col++){
                System.out.print(matrixA[row][col] + " ");
            }
            System.out.print("    ");
            if(row+1>9){
                System.out.print((row + 1) + " ");
            }
            else{
                System.out.print(" " + (row + 1) + " ");
            }
            for (int col = 0; col < matrixSize; col++){
                System.out.print(matrixB[row][col] + " ");
            }
            System.out.println();
        }
    }

    private static String[][] fillMatrix(int matrixSize, FieldSymbols symbols) {

        String[][] matrix = new String[matrixSize][matrixSize];
        for(int i = 0; i < matrixSize; i++){
            for(int j = 0; j < matrixSize; j++){
                matrix[i][j] = symbols.fill;
            }
        }
        return matrix;
    }

    private static String[][] placeOnMatrix(int matrixSize, Map ships, FieldSymbols symbols, Scanner scan) {
        // Two Matrixs filled with fill/water
        String[][] matrix = fillMatrix(matrixSize,symbols);
        String[][] sampleB = fillMatrix(matrixSize,symbols);
        char direction;

        // Variable for ships, only to reduce calls to set.getValue, maybe unnecessary
        int numberOfShips;

        // This is how I solved iterating my HashMap, gives me warning and could also probably be improved
        Set<Map.Entry<String, Integer>> entrySet = ships.entrySet();

        for (Map.Entry<String, Integer> set : entrySet){
             numberOfShips = set.getValue();
            if(numberOfShips > 0){

                for(int i = 0; i < numberOfShips; i++) {
                    printMatrix(matrix, sampleB, matrixSize);
                    System.out.println(set.getKey() + " " +  (numberOfShips-i));

                    //Getting direction and reject faulty input

                    do {
                        System.out.println("Choose Direction; Horizontal = H and Vertical = V: ");
                        direction = Character.toUpperCase(scan.next().charAt(0));
                    } while (direction != 'H' && direction != 'V');
                    Ships battleship = new Ships(set.getKey(), direction);

                    generatePlacement(matrix, battleship, matrixSize, symbols, scan, false);

                }
                }

            }

            return matrix;
        }

    private static void generatePlacement(String[][] matrix, Ships battleship, int matrixSize, FieldSymbols symbols, Scanner scan, boolean auto) {

        //Variables for coordinates and placement
        int row = Integer.MIN_VALUE;
        int col = Integer.MIN_VALUE;
        int rowMax = matrixSize;
        int colMax = matrixSize;
        boolean gotCaught = true;
        Coordinates coordinates = new Coordinates();

        if(battleship.getDirection() == 'V'){
            rowMax -= battleship.getLength()-1;
        }
        else{
            colMax -= battleship.getLength()-1;
        }



        //Generate coordinates for placement, if auto is true(for Player B), randomize coordinates
        if(auto){
            coordinates.setRow(new Random().nextInt(rowMax));
            coordinates.setCol(new Random().nextInt(colMax));
        }
        else{
            do {
                System.out.println("Choose Row: ");
                if(scan.hasNextInt()){
                    row = scan.nextInt();
                    if(row > 0 && row <= rowMax){
                        gotCaught = false;
                    }
                }
                else {
                    scan.nextLine();
                    System.out.println("Enter a valid Integer value");
                }
            } while (gotCaught);
            gotCaught = true;
            do {
                System.out.println("Choose Col: ");
                if(scan.hasNextInt()){
                    col = scan.nextInt();
                    if(col > 0 && col <= colMax){
                        gotCaught = false;
                    }
                }
                else {
                    scan.nextLine();
                    System.out.println("Enter a valid Integer value");
                }
            } while (gotCaught);

            coordinates.setRow(row-1);
            coordinates.setCol(col-1);
        }
        coordinates.setDirection(battleship.getDirection());
        coordinates.setLength(battleship.getLength());



        // Evaluate if placement makes the ships overlap, display message if Human Player(Player A) overlaps
        // If overlapping occur, call function generatePlacement again and get new coordinates/placement
        // If the placement is possible, update The Matrix
        if(!tryPossiblePlacement(matrix, symbols, coordinates)){
            if(!auto) {
                System.out.println("Overlapping ships! Try again mister");
            }
            generatePlacement(matrix, battleship, matrixSize, symbols, scan, auto);
        }
        else{
            for(int i = 0; i < coordinates.getLength(); i++){
                if(coordinates.getDirection() == 'V'){
                    matrix[coordinates.getRow()+i][coordinates.getCol()] = symbols.ship;
                }
                else {
                    matrix[coordinates.getRow()][coordinates.getCol()+i] = symbols.ship;
                }
            }
        }


    }

    // Function for evaluating possible placement, loops through the matrix and if any slots is occupied with something other than fill(water)
    // it will return false
    private static boolean tryPossiblePlacement(String[][] matrix, FieldSymbols symbols, Coordinates coordinates) {
        int collisionCounter = 0;

        for(int i = 0; i < coordinates.getLength(); i++) {
            if(coordinates.getDirection() == 'V'){
                if(!Objects.equals(matrix[coordinates.getRow() + i][coordinates.getCol()], symbols.fill)){
                    collisionCounter += 1;
                }
            }
            else {
                if(!Objects.equals(matrix[coordinates.getRow()][coordinates.getCol()+i], symbols.fill)){
                    collisionCounter += 1;
                }
            }
        }

        return collisionCounter == 0;

    }

}



