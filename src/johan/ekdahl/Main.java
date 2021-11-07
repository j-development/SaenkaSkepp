package johan.ekdahl;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        boolean gameOn = true;
        int matrixSize = 10;
        String fill = "-";
        String hit = "X";
        String miss = "O";
        String ship = "S";

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
        String[][] matrixA_public = fillMatrix(matrixSize,fill);
        String[][] matrixA_private = placeOnMatrix(matrixSize, ships, fill, ship, scan);

        //Init Create  Player B  Bot player
        String[][] matrixB_public = fillMatrix(matrixSize,fill);
        String[][] matrixB_private = placeOnMatrixAuto(matrixSize, ships, fill, ship, scan);


        System.out.println("Deployment Done");


        while(gameOn){
            //Player A and Player B print
            printMatrix(matrixA_private, matrixB_public, matrixSize);
            //Player A Shoots
            gameOn = playerAShoots(matrixB_private, matrixB_public, hit, miss, ship, matrixSize, scan, gameOn);
            //Player B Shoots
            gameOn = playerBShoots(matrixA_private, matrixA_public, hit, miss, ship, matrixSize, gameOn);
        }


    }

    private static boolean playerBShoots(String[][] matrixA_private, String[][] matrixA_public, String hit, String miss, String ship, int matrixSize, boolean gameOn) {
        int[] coordinates = new int[]{Integer.MIN_VALUE,Integer.MIN_VALUE};
        // Getting coordinates for our computer player(Player B)
        // Keep rolling coordinates as long as we keep getting coordinates we have already shot
        do {
            coordinates[0] = new Random().nextInt(10);
            coordinates[1] = new Random().nextInt(10);
        } while (Objects.equals(matrixA_private[coordinates[0]][coordinates[1]], miss) || Objects.equals(matrixA_private[coordinates[0]][coordinates[1]], hit));

        //Announce if coordinates hits or misses target in the Player A's Matrix
        if(Objects.equals(matrixA_private[coordinates[0]][coordinates[1]], ship)){
            System.out.println("Player B Hit!");
            matrixA_private[coordinates[0]][coordinates[1]] = hit;
            matrixA_public[coordinates[0]][coordinates[1]] = hit;
        }
        else{
            System.out.println("Player B Missed!");
            matrixA_private[coordinates[0]][coordinates[1]] = miss;
            matrixA_public[coordinates[0]][coordinates[1]] = miss;
        }
        //Always check after shots fired, if player has won
        if(checkIfAnyPlayerWon(matrixA_private, ship, matrixSize)){
            System.out.println("Player B Have Won, Congratulations!");
            gameOn = false;
        }
        return gameOn;

    }

    private static boolean playerAShoots(String[][] matrixB_private, String[][] matrixB_public, String hit, String miss, String ship, int matrixSize, Scanner scan, boolean gameOn) {
        int[] coordinates = new int[]{Integer.MIN_VALUE,Integer.MIN_VALUE};
        boolean gotCaught = true;


        System.out.println("Time To Fire, Give Me Your Coordinates Captain!");

        // Getting coordinates for Human player(Player A)
        // Keep asking for coordinates as long as user keep giving coordinates we have already shot
        // Also check for valid integer inputs
        do {
            if(coordinates[0] != Integer.MIN_VALUE){
                System.out.println("Sorry, Invalid Coordinates, Please Try Again!");
            }
            do {
                System.out.println("Choose Row: ");
                if(scan.hasNextInt()){
                    coordinates[0] = scan.nextInt();
                    gotCaught = false;
                }
                else{
                    scan.nextLine();
                    System.out.println("Enter a valid Integer value");
                }
            } while (coordinates[0] > matrixSize || coordinates[0] < 1 && gotCaught);
            gotCaught = true;
            do {
                System.out.println("Choose Col: ");
                if(scan.hasNextInt()){
                    coordinates[1] = scan.nextInt();
                    gotCaught = false;
                }
                else{
                    scan.nextLine();
                    System.out.println("Enter a valid Integer value");
                }
            } while (coordinates[1] > matrixSize || coordinates[1] < 1 && gotCaught);
            coordinates[0] -= 1;
            coordinates[1] -= 1;
        } while (Objects.equals(matrixB_public[coordinates[0]][coordinates[1]], miss) || Objects.equals(matrixB_public[coordinates[0]][coordinates[1]], hit));


        //Announce if coordinates hits or misses target in the Matrix
        if(Objects.equals(matrixB_private[coordinates[0]][coordinates[1]], ship)){
            System.out.println("Direct Hit!");
            matrixB_private[coordinates[0]][coordinates[1]] = hit;
            matrixB_public[coordinates[0]][coordinates[1]] = hit;
        }
        else{
            System.out.println("You Missed!");
            matrixB_private[coordinates[0]][coordinates[1]] = miss;
            matrixB_public[coordinates[0]][coordinates[1]] = miss;
        }
        //Always check after shots fired, if player has won
        if(checkIfAnyPlayerWon(matrixB_private, ship, matrixSize)){
            System.out.println("Player A Have Won, Congratulations!");
            gameOn = false;
        }
            return gameOn;
    }

    private static boolean checkIfAnyPlayerWon(String[][] matrix, String ship, int matrixSize) {
        int shipCounter = 0;
        for (int i = 0; i < matrixSize; i++){
            for (int j = 0; j < matrixSize; j++){
                if(Objects.equals(matrix[i][j], ship)){
                    shipCounter += 1;
                }
            }
        }
        return shipCounter == 0;

    }

    private static String[][] placeOnMatrixAuto(int matrixSize, Map<String, Integer> ships, String fill, String ship, Scanner scan) {
        //Pretty much the same as placeOnMatrix but with auto-placement for our computer player(Player B)

        //Matrix Variable filled with fill/water
        String[][] matrix = fillMatrix(matrixSize,fill);


        int numberOfShips;

        Set<Map.Entry<String, Integer>> entrySet = ships.entrySet();
        //Player B placing all his ships, announcing with a println which ship
        for (Map.Entry<String, Integer> set : entrySet){
            numberOfShips = set.getValue();
            if(numberOfShips > 0){

                for(int i = 0; i < numberOfShips; i++) {
                    System.out.println("Player B placing: " + set.getKey() + " " +  (numberOfShips-i));

                    generatePlacement(matrix, set.getKey(), new Random().nextInt(2) == 1 ? 'V' : 'H', matrixSize, ship, fill, scan, true);

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

    private static String[][] fillMatrix(int matrixSize, String fill) {
        String[][] matrix = new String[matrixSize][matrixSize];
        for(int i = 0; i < matrixSize; i++){
            for(int j = 0; j < matrixSize; j++){
                matrix[i][j] = fill;
            }
        }
        return matrix;
    }

    private static String[][] placeOnMatrix(int matrixSize, Map ships, String fill, String ship, Scanner scan) {
        // Two Matrixs filled with fill/water
        String[][] matrix = fillMatrix(matrixSize,fill);
        String[][] sampleB = fillMatrix(matrixSize,fill);

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
                    generatePlacement(matrix, set.getKey(), direction, matrixSize, ship, fill, scan, false);

                }
                }

            }

            return matrix;
        }

    private static void generatePlacement(String[][] matrix, String shipType, char direction, int matrixSize, String ship, String fill, Scanner scan, boolean auto) {

        //Variables for coordinates and placement
        int row = Integer.MIN_VALUE;
        int col = Integer.MIN_VALUE;
        int length = 0;
        int rowMax = matrixSize;
        int colMax = matrixSize;
        boolean gotCaught = true;
        Coordinates coordinates = new Coordinates();



        // Get constraints on coordinates, we don't want any array out of bounds on our Matrix
        // Also get the length of our ships and store it in length variable
        switch (shipType) {
            case "Carrier" -> {
                if (direction == 'V') {
                    rowMax -= 4;
                } else {
                    colMax -= 4;
                }
                length = 5;
            }
            case "Battleship" -> {
                if (direction == 'V') {
                    rowMax -= 3;
                } else {
                    colMax -= 3;
                }
                length = 4;
            }
            case "Cruiser", "Submarine" -> {
                if (direction == 'V') {
                    rowMax -= 2;
                } else {
                    colMax -= 2;
                }
                length = 3;
            }
            case "Destroyer" -> {
                if (direction == 'V') {
                    rowMax -= 1;
                } else {
                    colMax -= 1;
                }
                length = 2;
            }
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
        coordinates.setDirection(direction);
        coordinates.setLength(length);



        // Evaluate if placement makes the ships overlap, display message if Human Player(Player A) overlaps
        // If overlapping occur, call function generatePlacement again and get new coordinates/placement
        // If the placement is possible, update The Matrix
        if(!tryPossiblePlacement(matrix, fill, coordinates)){
            if(!auto) {
                System.out.println("Overlapping ships! Try again mister");
            }
            generatePlacement(matrix, shipType, direction, matrixSize, ship , fill, scan, auto);
        }
        else{
            for(int i = 0; i < coordinates.getLength(); i++){
                if(coordinates.getDirection() == 'V'){
                    matrix[coordinates.getRow()+i][coordinates.getCol()] = ship;
                }
                else {
                    matrix[coordinates.getRow()][coordinates.getCol()+i] = ship;
                }
            }
        }


    }

    // Function for evaluating possible placement, loops through the matrix and if any slots is occupied with something other than fill(water)
    // it will return false
    private static boolean tryPossiblePlacement(String[][] matrix, String fill, Coordinates coordinates) {
        int collisionCounter = 0;

        for(int i = 0; i < coordinates.getLength(); i++) {
            if(coordinates.getDirection() == 'V'){
                if(!Objects.equals(matrix[coordinates.getRow() + i][coordinates.getCol()], fill)){
                    collisionCounter += 1;
                }
            }
            else {
                if(!Objects.equals(matrix[coordinates.getRow()][coordinates.getCol()+i], fill)){
                    collisionCounter += 1;
                }
            }
        }

        return collisionCounter == 0;

    }

}



