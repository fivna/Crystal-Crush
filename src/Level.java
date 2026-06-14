/**
 * Level class represents a level in the game.
 * Contains information about the level number, move limit, target score, and game status (win/loss).
 */

public class Level {
    private int levelNum;
    private int moveLimit;
    private int movesLeft;
    private int targetScore;
    private int numAcquired;
    private int score;
    private boolean hasWon;
    private boolean hasLost;
    private boolean infinite;
    private String targetType;


    /**
     * Constructor for Level class.
     * 
     * @param levelNum the level number
     * @param moveLimit maximum moves for the level
     * @param targetScore target number of crystals to acquire for level
     * @param targetType target crystal type for level
     */
    public Level (int levelNum, int moveLimit, int targetScore, String targetType){
        this.levelNum = levelNum;
        this.moveLimit = moveLimit;
        this.targetScore = targetScore;
        this.targetType = targetType;
        this.movesLeft = moveLimit;
        this.numAcquired = 0;
        this.score = 0;
        this.hasWon = false;
        this.hasLost = false;
    }


    /**
     * Overloaded constructor for infinite gamemode level.
     * 
     * @param levelNum the level number
     * @param moveLimit the move limit
     */
    public Level (int levelNum, int moveLimit){
        this.levelNum = levelNum;
        this.moveLimit = moveLimit;
        this.infinite = true; // set infinite mode to true
    }


    /**
     * Check if game has been won or lost.
     */
    public void updateGameStatus(){
        if (infinite) return; // ignore win/loss if in infinite mode

        // game won if enough crystals are collected
        if (numAcquired >= targetScore) { 
            hasWon = true;
            score += movesLeft * 200; // add 200 points for every move left
        }

        // game lost if max moves exceeded
        else if (movesLeft <= 0) hasLost = true; 
    }


    /**
     * Reset level; reset moves left, score, and collected crystals
     */
    public void reset(){
        movesLeft = moveLimit;
        hasLost = false;
        score = 0;
        numAcquired = 0;
    }

    public int getLevelNum() {
        return levelNum;
    }
    public boolean isInfinite() {
        return infinite;
    }
    public int getMoveLimit() {
        return moveLimit;
    }
    public int getMovesLeft() {
        return movesLeft;
    }
    public int getTargetScore() {
        return targetScore;
    }
    public void setTargetScore(int targetScore) {
        this.targetScore = targetScore;
    }
    public int getScore() {
        return score;
    }
    public void increaseScore(int score) {
        this.score += score;
    }
    public boolean isWon() {
        return hasWon;
    }
    public boolean isLost() {
        return hasLost;
    }
    public String getTargetType() {
        return targetType;
    }
    public int getNumAcquired() {
        return numAcquired;
    }
    public void addNumAcquired(int numAcquired) {
        this.numAcquired += numAcquired;
    }
    public void decreaseMoves(){
        if (infinite) return;
        this.movesLeft--;
    }
}
