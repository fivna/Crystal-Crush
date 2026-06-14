import java.io.*;
/**
 * Game class represents the overall game state, including current level, board, and high score.
 */

public class Game {
    private int levelIndex; // levelIndex starts at 0, but levelNum starts at 1
    private Level[] levels; // holds all levels in game
    private Level currentLevel; // current level being played
    private Board board; // board for current level
    private int highScore = 0;

    /**
    * Constructor for Game class. Initializes levels, current level, and board.
    * 
    * @param levels array of Level objects representing all levels in the game
    * @param levelIndex index of the current level to start from
    * @param numRows number of rows for the board
    * @param numCols number of columns for the board
    */
    public Game(Level[] levels, int levelIndex, int numRows, int numCols) {
        this.levels = levels;
        this.levelIndex = levelIndex;
        this.currentLevel = levels[levelIndex];
        this.board = new Board(numRows, numCols, this);
    }


    /**
     * Retrieves the high score from a text file and stores it in the highScore variable.
     */
    public void retrieveHighScore(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/highScore.txt"));
            highScore = Integer.parseInt(reader.readLine());

            reader.close();
        } catch(Exception e){};
    }
    

    /**
     * Returns the current level number (1-indexed).
     * 
     * @return the current level number
     */
    public int getLevelNum(){
        return this.levelIndex + 1; // returns actual level number, since index starts from 0
    }


    /**
     * Sets the high score and writes it to a text file.
     * 
     * @param score the new high score to set
     */
    public void setHighScore(int score){
        this.highScore = score;
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/highScore.txt"));
            writer.write(String.valueOf(score));

            writer.close();
        } catch(Exception e){};
    }


    /**
     * Advances game to next level.
     */
    public void nextLevel(){
        levelIndex += 1;
        currentLevel = levels[levelIndex];
    }


    /**
     * Sets current level using specific index.
     * 
     * @param levelIndex the index of the level to set as current level
     */
    public void setLevel(int levelIndex){
        currentLevel = levels[levelIndex];
        this.levelIndex = levelIndex;
    }

   
    public Level getCurrentLevel() {
        return currentLevel;
    }
    public Board getBoard(){
        return board;
    }
    public int getHighScore(){
        return highScore;
    }
    public int getLevelIndex(){
        return levelIndex;
    }
}