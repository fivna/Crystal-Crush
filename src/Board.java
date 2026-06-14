import java.util.*;
/**
 * Board class represents the game board, which holds the crystals and their states. 
 * Includes methods for initializing board, checking for valid moves, marking matched 
 * crystals, crushing marked crystals, dropping new crystals, and activating special crystals.
 */

public class Board {
    private Game game; // reference to game object
    private Crystal[][] crystals; // holds the crystals on the board
    private int[][] states; // 0 for unmarked, 1 for marked
    private final int numCols;
    private final int numRows;
    private final int cellSize = 72; // size of each cell in pixels
    private int targetsAcquired; // number of target crystals acquired in current level
    private int comboMultiplier; // multiplier for scoring combos
    private final String[] types = {"r", "o", "g", "p", "pi"}; // possible crystal types: red, orange, green, purple, pink
    

    /**
     * Constructor for Board class.
     * 
     * @param numCols number of columns for the board
     * @param numRows number of rows for the board
     * @param game reference to Game object
     */
    public Board(int numCols, int numRows, Game game){ 
        this.crystals = new Crystal[numCols][numRows];
        this.numCols = numCols;
        this.numRows = numRows;
        this.states = new int[numCols][numRows];
        this.game = game;
        comboMultiplier = 1;
    }


    /**
     * Initializes board by filling it with random crystals.
     */
    public void initialize(){
        
        // reset states; all crystals are unmarked
        for (int x = 0; x < numCols; x++){
            for (int y = 0; y < numRows; y++){
                states[x][y] = 0;
            }
        }
        targetsAcquired = 0; // reset number of acquired crystals
        

        // fill baord with random crystals
        for (int y = 0; y < numCols; y++){
            for (int x = 0; x < numRows; x++){
                boolean valid = false;

                while (!valid){
                    int index = (int)(Math.random() * types.length);
                    crystals[x][y] = new Crystal(types[index], false, x, y); // create new crystal with random type
                    valid = isValidPlacement(x, y, types[index]); // ensure that no initial matches exist on the board
                }
            }
        }
    }


    /**
     * Checks if placing a crystal of a certain type at a specific position would create an initial match.
     * 
     * @param x the crystal's x position on board
     * @param y the crystal's y position on board
     * @param type the type of the crystal being placed
     * @return true if placement is valid, false otherwise
     */
    private boolean isValidPlacement(int x, int y, String type){
        if (x >= 2 && crystals[x-1][y].getType().equals(type) && crystals[x-2][y].getType().equals(type)) return false; // checks the 2 on left
        if (y >= 2 && crystals[x][y-1].getType().equals(type) && crystals[x][y-2].getType().equals(type)) return false; // checks the 2 on top
        
        return true;
    }


    /**
     * Updates the states array to mark all crystals that are part of a match.
     */
    public void updateStates(){
        for (int y = 0; y < numRows; y++){
            for (int x = 0; x < numCols; x++){

                // check for matches, marking all if any are found
                if (states[x][y] == 0 && (checkHorizontal(x, y) || checkVertical(x, y))) {
                    markAll(x, y);
                }
            }
        }

        int pointsEarned = 0;
        for (int y = 0; y < numRows; y++){
            for (int x = 0; x < numCols; x++){
                String t = game.getCurrentLevel().getTargetType();
                if (states[x][y] == 1){
                    pointsEarned += 10; // 50 points for every normal crystal
                    if (crystals[x][y].getType().equals(t)){
                        targetsAcquired++;
                        pointsEarned += 50; // 50 points for every target crystal
                    }
                }
            }
        }
        game.getCurrentLevel().increaseScore(pointsEarned * comboMultiplier); // multiply by combo multiplier
    }


    /**
     * Marks all crystals part of match from specified position.
     * 
     * @param x x position to check from
     * @param y y position to check from
     */
    private void markAll(int x, int y){
        String type = crystals[x][y].getType();

        int hLength;
        if (checkHorizontal(x, y)) hLength = horizontalMatchLength(x, y); // store length of horizontal match
        else hLength = 0;

        int vLength;
        if (checkVertical(x, y)) vLength = verticalMatchLength(x, y); // store length of vertical match
        else vLength = 0;


        if (hLength >= 1){
            if (x >= 1 && crystals[x-1][y].getType().equals(type)) markLeft(x, y); // mark all to left
            if (x <= numCols - 2 && crystals[x+1][y].getType().equals(type)) markRight(x, y); // mark all to right
        }
        if (vLength >= 1){
            if (y >= 1 && crystals[x][y-1].getType().equals(type)) markUp(x, y); // mark all to top
            if (y <= numRows - 2 && crystals[x][y+1].getType().equals(type)) markDown(x, y); // mark all to bottom
        }

        int maxLength = Math.max(hLength, vLength); 


        // if a match of 5 is made, dynamite is created
        if (maxLength >= 5){
            crystals[x][y].setType("d"); // set type to dynamite
            crystals[x][y].setSpecial(false);
            states[x][y] = 0; // unmark so it's not crushed immediately
            game.getCurrentLevel().increaseScore(500); // extra points for 5 in a row
        }

        // if a match of 4 is made, a special crystal is made
        else if (maxLength == 4){ 
            crystals[x][y].setSpecial(true);
            states[x][y] = 0;
            game.getCurrentLevel().increaseScore(200); // extra points for 4 in a row
        }
    }

    
    /**
     * Finds the length of the horizontal match.
     * 
     * @param x the x position to check
     * @param y the y position to check
     * @return the total length of the horizontal match
     */
    private int horizontalMatchLength(int x, int y){
        String type = crystals[x][y].getType();
        int length = 1; // default length set to 1


        int i = x-1; // check previous positions
        while (i >= 0 && crystals[i][y] != null && crystals[i][y].getType().equals(type)){ 
            // check how many crystals to the left are the same type
            length++;
            i--; // check previouus position
        }

        i = x+1; // check following positions
        while (i < numCols && crystals[i][y] != null && crystals[i][y].getType().equals(type)){
            // check how many crystals to the right are the same type
            length++;
            i++; // check next position
        }

        return length;
    }


     /**
     * Finds the length of the vertical match.
     * 
     * @param x the x position to check
     * @param y the y position to check
     * @return the total length of the vertical match
     */
    public int verticalMatchLength(int x, int y){
        String type = crystals[x][y].getType();
        int length = 1;// default length set to 1


        int i = y-1; // check positions above
        while (i >= 0 && crystals[x][i] != null && crystals[x][i].getType().equals(type)){
            // check how many crystals to the top are the same type
            length++;
            i--;
        }

        i = y+1; // check positions below
        while (i < numRows && crystals[x][i] != null && crystals[x][i].getType().equals(type)){
            // check how many crystals to the bottom are the same type
            length++;
            i++;
        }

        return length;
    }

    
    /**
     * Checks if there is a vertical match of 3 or more crystals of the same type at the specified position.
     * 
     * @param x the crystal's x position on board
     * @param y the crystal's y position on board
     * @return true if there is a vertical match, false otherwise
     */
    private boolean checkVertical(int x, int y){
        String type = crystals[x][y].getType();

        if (y >= 2 && crystals[x][y-1].getType().equals(type) && crystals[x][y-2].getType().equals(type)) return true; // check 2 on bottom
        if (y <= numRows - 3 && crystals[x][y+1].getType().equals(type) && crystals[x][y+2].getType().equals(type)) return true; // check 2 on top
        if (y >= 1 && y <= numRows - 2 && crystals[x][y-1].getType().equals(type) && crystals[x][y+1].getType().equals(type)) return true; // check 1 on top and 1 on bottom
        
        return false;
    }
    

    /**
     * Checks if there is a horizontal match of 3 or more crystals of the same type at the specified position.
     * 
     * @param x the crystal's x position on board
     * @param y the crystal's y position on board
     * @return true if there is a horizontal match, false otherwise
     */
    private boolean checkHorizontal(int x, int y){
        String type = crystals[x][y].getType();
        if (x >= 2 && crystals[x-1][y].getType().equals(type) && crystals[x-2][y].getType().equals(type)) return true; // check 2 on left
        if (x <= numCols - 3 && crystals[x+1][y].getType().equals(type) && crystals[x+2][y].getType().equals(type)) return true; // check 2 on right
        if (x >= 1 && x <= numCols - 2 && crystals[x-1][y].getType().equals(type) && crystals[x+1][y].getType().equals(type)) return true; // check 1 on left and 1 on right
       
        return false;
    }


    /**
     * Marks all crystals part of match from specified position to the right. Activates special crystals accordingly if part of match.
     * 
     * @param x x position to check
     * @param y y position to check
     */
    private void markRight(int x, int y){
        String type = crystals[x][y].getType();
        if (crystals[x][y].isSpecial()) activateSpecial(crystals[x][y]); // activate special crystal
        if (x == numCols-1 || !crystals[x+1][y].getType().equals(type)) { // if at right edge of board or next crystal is different type, mark and return
            states[x][y] = 1;
            return;
        }

        states[x][y] = 1;
        markRight(x+1, y); // continue marking to the right
    }


    /**
     * Marks all crystals part of match from specified position to the left. Activates special crystals accordingly if part of match.
     * 
     * @param x x position to check
     * @param y y position to check
     */
    private void markLeft(int x, int y){
        String type = crystals[x][y].getType();
        if (crystals[x][y].isSpecial()) activateSpecial(crystals[x][y]); // activate special crystal
        if (x == 0 || !crystals[x-1][y].getType().equals(type)) { // if at left edge of board or next crystal is different type, mark and return
            states[x][y] = 1;
            return;
        }

        states[x][y] = 1;
        markLeft(x-1, y); // continue marking to the left
    }


    /**
     * Marks all crystals part of match from specified position upwards. Activates special crystals accordingly if part of match.
     * 
     * @param x x position to check
     * @param y y position to check
     */
    private void markUp(int x, int y){
        String type = crystals[x][y].getType();
        if (crystals[x][y].isSpecial()) activateSpecial(crystals[x][y]); // activate special crystal
        if (y == 0 || !crystals[x][y-1].getType().equals(type)) { // if at top edge of board or next crystal is different type, mark and return
            states[x][y] = 1;
            return;
        }

        states[x][y] = 1;
        markUp(x, y-1); // continue marking upwards
    }


     /**
     * Marks all crystals part of match from specified position downwards. Activates special crystals accordingly if part of match.
     * 
     * @param x x position to check
     * @param y y position to check
     */
    private void markDown(int x, int y){
        String type = crystals[x][y].getType();
        if (crystals[x][y].isSpecial()) activateSpecial(crystals[x][y]);
        if (y == numRows - 1 || !crystals[x][y+1].getType().equals(type)) {// if at bottom edge of board or next crystal is different type, mark and return
            states[x][y] = 1;
            return;
        }

        states[x][y] = 1;
        markDown(x, y+1); // continue marking downwards
    }

     
    /**
     * Ensures that at least a match of 3 is made by swapping two crystals.
     * 
     * @param c1 first crystal
     * @param c2 second crystal
     * @return true if valid move, false otherwise
     */
    public boolean isValidMove(Crystal c1, Crystal c2){
        boolean isValid = false;
        swapElements(c1, c2); // swaps crystals to check for matches

        // store positions
        int x1 = c1.getPosition()[0]; 
        int y1 = c1.getPosition()[1];

        // swap positions
        int x2 = c2.getPosition()[0];
        int y2 = c2.getPosition()[1];

        if (checkVertical(x1, y1) || checkHorizontal(x1, y1)) isValid = true; // checks if horizontal or vertical match is made for first crystal
        if (checkVertical(x2, y2) || checkHorizontal(x2, y2)) isValid = true; // checks if horizontal or vertical match is made for second crystal

        swapElements(c1, c2); // swap back to original positions

        return isValid;
    }

    
     /**
     * Swaps the positions of the two crystals
     * 
     * @param c1 first crystal
     * @param c2 second crystal
     */
    public void swapElements(Crystal c1, Crystal c2){
        int x1 = c1.getPosition()[0];
        int y1 = c1.getPosition()[1];

        int x2 = c2.getPosition()[0];
        int y2 = c2.getPosition()[1];

        crystals[x1][y1] = c2;
        crystals[x2][y2] = c1;

        c1.setPosition(x2, y2);
        c2.setPosition(x1, y1);
    }


    /**
     * Crushes all marked crystals by setting them to null and unmarking them in states array.
     */
    public void crush(){
        for (int y = 0; y < numRows; y++){
            for (int x = 0; x < numCols; x++){
                if (states[x][y] == 1){ // if crystal is marked, set to null and unmark
                    crystals[x][y] = null;
                    states[x][y] = 0;
                }
            }
        }
    }


    /**
     * Drops crystals down to fill empty spaces and generate new ones.
     */
    public void drop(){
        for (int x = 0; x < numCols; x++){
            ArrayList<Crystal> col = new ArrayList<>(); // stores all uncrushed crystals in the column
            ArrayList<Integer> oldRows = new ArrayList<>(); // stores the uncrushed crystals' old rows 

            for (int y = 0; y < numRows; y++){
                if (crystals[x][y] != null){ 
                    col.add(crystals[x][y]); // add to list of uncrushed crystals
                    oldRows.add(y); // add the crystal's old row position
                    crystals[x][y] = null; // set to null to prepare for dropping
                }
            }

            int numEmpty = numRows - col.size(); // number of empty spaces that need to be filled

            for (int i = 0; i < col.size(); i++){
                int newY = numEmpty + i; // new row position after dropping, accounting for empty spaces
                Crystal c = col.get(i); 
                int oldY = oldRows.get(i); // get the crystal's old position
                int shift = newY - oldY; // number of spaces for the crystal to drop

                crystals[x][newY] = c; // drop crystal, setting its new positio
                c.setPosition(x, newY);


                // crystal needs to be shifted downward
                if (shift > 0){
                    c.setPy(-(shift * cellSize)); // set pixel offset to create falling animation
                    c.resetFall(); // reset falling speed and acceleration
                    c.setFalling(true);
                }
            }

            for (int y = 0; y < numEmpty; y++){
                int index = (int)(Math.random() * types.length); 
                Crystal c = new Crystal(types[index], false, x, y); // generate random crystal
                c.setPy(-(numEmpty * cellSize)); // set pixel offset for falling animation
                c.resetFall();  // reset falling speed and acceleration
                c.setFalling(true);
                crystals[x][y] = c;
            }
        }
    }

   
    /**
     * Checks if all matches have been crushed.
     * 
     * @return true if there are no more matches, false otherwise
     */
    public boolean isFullyCrushed(){
        updateStates(); // scans map to update all states
        
        for (int y = 0; y < numCols; y++){
            for (int x = 0; x < numRows; x++){
                if (states[x][y] == 1) return false; // if a crystal is marked, then the board is not fully crushed
            }
        }

        return true;
    }


    /**
     * Activates special crystals and dynamite, marking all the crystals affected by it.
     * 
     * @param c the item being activated
     */
    public void activateSpecial(Crystal c){
        int x = c.getPosition()[0]; // get x position
        int y = c.getPosition()[1]; // get y position


        // if item is dynamite
        if (c.getType().equals("d")) { 
            c.setActivated(true);
            activateDynamite(x, y); // activate and mark crystals accordingly
        }

        // if item is special crystal
        else if (c.isSpecial()){ 

            for (int i = 0; i < numCols; i++){ // iterate through all columns
                if (crystals[i][y].getType().equals("d")) { // activate dynamite if in that row
                    c.setActivated(true);
                    activateDynamite(i, y);
                }
                if (crystals[i][y] != null){
                    states[i][y] = 1; // mark all crystals in the horizontal row
                }
            }
        }
    }


    /**
     * Marks 8 boxes around it, plus an extra four on the middle of each edge (star shape around center)
     * 
     * @param centerX x coordinate of the dynamite
     * @param centerY y coordinate of the dynamite
     */
    private void activateDynamite(int centerX, int centerY){
        for (int dx = -1; dx <= 1; dx++){
            for (int dy = -1; dy <= 1; dy++){
                mark(centerX + dx, centerY + dy); // marks 8 boxes around the dynamite
            }
        }

        // mark the 4 boxes in the middle of each outer edge
        mark(centerX + 2, centerY);
        mark(centerX - 2, centerY);
        mark(centerX, centerY + 2);
        mark(centerX, centerY - 2);
    }
    

    /**
     * Helper method to mark all crystals blown up by dynamite, activating special crystals if necessary.
     * 
     * @param x the x coordinate to mark
     * @param y the y coordinate to mark
     */
    private void mark(int x, int y){
        // check if position is in the board
        if (x >= 0 && x < numCols && y >= 0 && y < numRows && crystals[x][y] != null){ 
            Crystal c = crystals[x][y];
            states[x][y] = 1; // mark crystal

            // if special crystal, activate it
            if (c.isSpecial() && !c.isActivated()) {
                c.setActivated(true);
                activateSpecial(c);
            }

            // if another dynamite, activate it
            if (c.getType().equals("d") && !c.isActivated()) { 
                c.setActivated(true);
                activateDynamite(x, y);
            }
        }
    }


    public Crystal[][] getCrystals(){
        return crystals;
    }
    public int getNumCols() {
        return numCols;
    }
    public int getNumRows() {
        return numRows;
    }
    public void setComboMultiplier(int multiplier){
        this.comboMultiplier = multiplier;
    }
    public int getComboMultiplier(){
        return comboMultiplier;
    }
    public int getTargetsAcquired(){
        return targetsAcquired;
    }
    public void setTargetsAcquired(int targetsAcquired){
        this.targetsAcquired = targetsAcquired;
    }
}
