import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame(); 

        // initialize 8 levels within game
        Level[] levels = {
            new Level(1, 30, 30, "p"),
            new Level(2, 30, 35, "o"),
            new Level(3, 25, 35, "r"),
            new Level(4, 20, 28, "pi"),
            new Level(5, 30, 60, "p"),
            new Level(6, 18, 36, "r"),
            new Level(7, 15, 26, "o"),
            new Level(8, 1) // infinite game mode
        };

        int numCols = 8;
        int numRows = 8;
        int levelNum = 0; // start from first level
        
        Game game = new Game(levels, levelNum, numCols, numRows); // initialize a new game
        game.retrieveHighScore(); // retrieve high score from text file
       
        Drawing drawing = new Drawing(game); // initialize drawing panel with game
        frame.setSize(1280, 720);
        frame.setTitle("Crystal Crush");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(drawing);  
        frame.pack();
        frame.setVisible(true);
    }
}
