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
        ships.put("Carrier", 0);
        // Battleship = 4 Slots
        ships.put("Battleship", 1);
        // Cruiser = 3 Slots
        ships.put("Cruiser", 1);
        // Destroyer = 2 Slots
        ships.put("Destroyer", 1);

        //Init Create  Player A  Human player
        String[][] matrixA_public = fillMatrix(matrixSize,fill);
        String[][] matrixA_private = placeOnMatrix(matrixSize, ships, fill, ship, scan);

        //Init Create  Player B  Bot player
        String[][] matrixB_public = fillMatrix(matrixSize,fill);
        String[][] matrixB_private = placeOnMatrixAuto(matrixSize, ships, fill, ship, scan);


        System.out.println("Done");


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
                    generatePlacement(matrix, set.getKey(), new Random().nextInt(2), matrixSize, ship, fill, scan, true);

                }
            }

        }

        return matrix;
    }

    private static void printMatrix(String[][] matrixA, String[][] matrixB, int matrixSize) {
        //Print Matrix, boring, 10 looks a bit off because of two-digit-width
        //Always prints two Matrixs side by side
        System.out.print("A ");
        for (int i = 0; i < matrixSize; i++){
            System.out.print(i + 1 + " ");
        }
        System.out.print("   B ");
        for (int i = 0; i < matrixSize; i++){
            System.out.print(i + 1 + " ");
        }
        System.out.println();
        for(int row = 0; row < matrixSize; row++){
            System.out.print(row + 1 + " ");
            for (int col = 0; col < matrixSize; col++){
                System.out.print(matrixA[row][col] + " ");
            }
            System.out.print("    ");
            System.out.print(row + 1 + " ");
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

        //Probably don't need two variables for direction, but for now I have two
        String dir;
        int direction;

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
                    //Vertical Direction = V = 1
                    //Horizontal Direction = H = 0
                    while(true) {
                        System.out.println("Choose Direction; Horizontal = H and Vertical = V: ");
                        dir = scan.nextLine().toUpperCase();
                        if(dir.equals("H") || dir.equals("V")) {
                            if(dir.equals("V")){
                                direction = 1;
                            }
                            else {
                                direction = 0;
                            }
                            break;
                        }
                    }
                    generatePlacement(matrix, set.getKey(), direction, matrixSize, ship, fill, scan, false);

                }
                }

            }

            return matrix;
        }

    private static void generatePlacement(String[][] matrix, String shipType, int direction, int matrixSize, String ship, String fill, Scanner scan, boolean auto) {

        //Variables for coordinates and placement
        int[] possiblePlacement = new int[4];
        int row = Integer.MIN_VALUE;
        int col = Integer.MIN_VALUE;
        int length = 0;
        int rowMax = matrixSize;
        int colMax = matrixSize;
        boolean gotCaught = true;

        // Get constraints on coordinates, we don't want any array out of bounds on our Matrix
        // Also get the length of our ships and store it in length variable
        switch (shipType) {
            case "Carrier" -> {
                if (direction == 1) {
                    rowMax -= 4;
                } else {
                    colMax -= 4;
                }
                length = 5;
            }
            case "Battleship" -> {
                if (direction == 1) {
                    rowMax -= 3;
                } else {
                    colMax -= 3;
                }
                length = 4;
            }
            case "Cruiser" -> {
                if (direction == 1) {
                    rowMax -= 2;
                } else {
                    colMax -= 2;
                }
                length = 3;
            }
            case "Destroyer" -> {
                if (direction == 1) {
                    rowMax -= 1;
                } else {
                    colMax -= 1;
                }
                length = 2;
            }
        }
        //Generate coordinates for placement, if auto is true(for Player B), randomize coordinates
        if(auto){
            possiblePlacement[0] = new Random().nextInt(rowMax);
            possiblePlacement[1] = new Random().nextInt(colMax);
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

            possiblePlacement[0] = row-1;
            possiblePlacement[1] = col-1;
        }
        possiblePlacement[2] = direction;
        possiblePlacement[3] = length;


        // Evaluate if placement makes the ships overlap, display message if Human Player(Player A) overlaps
        // If overlapping occur, call function generatePlacement again and get new coordinates/placement
        // If the placement is possible, update The Matrix
        if(!tryPossiblePlacement(matrix,possiblePlacement,fill)){
            if(!auto) {
                System.out.println("Overlapping ships! Try again mister");
            }
            generatePlacement(matrix, shipType, direction, matrixSize, ship , fill, scan, auto);
        }
        else{
            for(int i = 0; i < possiblePlacement[3]; i++){
                if(possiblePlacement[2] == 1){
                    matrix[possiblePlacement[0]+i][possiblePlacement[1]] = ship;
                }
                else {
                    matrix[possiblePlacement[0]][possiblePlacement[1]+i] = ship;
                }
            }
        }


    }

    // Function for evaluating possible placement, loops through the matrix and if any slots is occupied with something other than fill(water)
    // it will return false
    private static boolean tryPossiblePlacement(String[][] matrix, int[] possiblePlacement, String fill) {
        int collisionCounter = 0;

        for(int i = 0; i < possiblePlacement[3]; i++) {
            if(possiblePlacement[2] == 1){
                if(!Objects.equals(matrix[possiblePlacement[0] + i][possiblePlacement[1]], fill)){
                    collisionCounter += 1;
                }
            }
            else {
                if(!Objects.equals(matrix[possiblePlacement[0]][possiblePlacement[1]+i], fill)){
                    collisionCounter += 1;
                }
            }
        }

        return collisionCounter == 0;

    }

}



