import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

/**
 * Drawing class is responsible for rendering the game state and handling user interactions.
 * Etends JPanel and implements MouseListener and MouseMotionListener to capture mouse events for gameplay.
 */

public class Drawing extends JPanel implements MouseListener, MouseMotionListener {
    private int screen; // represents screen state; 0 for home, 1 for level select, 2 for game
    private Game game; // reference to game object
    private Board board; // reference to board object
    private int prevLevel; // saves previous level when entering infinite mode


    // graphics for home page
    private Image homeBg; 
    private Image logo; 
    private Image play; 
    private Image quit;


    // graphics for level select page
    private Image levelSelectBg;
    private Image checkmark;
    private Image levelCover;
    private Image home;


    // graphics for game page
    private Image gameBg;
    private Image infiniteMode; 
    private Image returnButton;
    private Image returnMenu;
    private Image failedMenu;
    private Image completeMenu;
    private boolean onReturnMenu = false;

    // item graphics; stores default and special versions for crysstals
    private Image[] rCrystal;
    private Image[] gCrystal;
    private Image[] pCrystal;
    private Image[] oCrystal;
    private Image[] piCrystal;
    private Image[] dynamite;


    // user interaction with crystals on the board
    private Crystal draggedCrystal;
    private boolean dragging = false; 
    private int dragStartX;
    private int dragStartY;
    private int startBoardX;
    private int startBoardY;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    // play button dimensions and position
    private int playX = 770;
    private int playY = 257;
    private int playWidth = 384;
    private int playHeight = 116;

    // quit button dimensions and position
    private int quitX = 770;
    private int quitY = 373;
    private int quitWidth = 384;
    private int quitHeight = 116;

    // home button dimensions and position
    private int homeX = 49;
    private int homeY = 610;
    private int homeWidth = 86;

     // return button dimensions and position
    private int returnX = 32;
    private int returnY = 618;
    private int returnWidth = 78;

    // infinite mode button dimensions and position
    private int infiniteX = 861;
    private int infiniteY = 417;
    private int infiniteSize = 125;

     // yes/no button dimensions and position
    private int ynX = 429;
    private int ynY = 327;
    private int ynWidth = 198;
    private int ynHeight = 59;

     // exit button dimensions and position
    private int exitX = 529;
    private int exitY = 394;
    private int exitWidth = 223;
    private int exitHeight = 61;

    // timers for animation
    private Timer cascadeTimer;
    private Timer fallTimer;
    private int step = 0; // represents step of the cascade process

    // crystal pixel size
    private int crystalSize = 68; 

    // board dimensions and position
    private int boardX = 356;
    private int boardY = 74;
    private int boardWidth = 569;
    private int boardHeight = 574;
    
    // custom font
    private Font reggaeOne;

    private final String[] types = new String[]{"r", "o", "g", "p", "pi", "d"}; // types of crystals
    private Image[][] images; // holds graphics for both versions of crystals + dynamite


    /**
     * Constructor for drawing class.
     * 
     * @param game reference to game object
     */
    public Drawing(Game game){
        this.game = game;
        this.board = game.getBoard();
        GraphicsEnvironment GE = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // initialize font
        try {
            File fontFile = new File("src/resources/fonts/ReggaeOne-Regular.ttf");
            reggaeOne = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(50f);
            GE.registerFont(reggaeOne);
        } catch (Exception e) {}
        
        homeBg = new ImageIcon("src/resources/home/0-background.png").getImage();
        logo = new ImageIcon("src/resources/home/0-logo.png").getImage();
        play = new ImageIcon("src/resources/home/0-play.png").getImage();
        quit = new ImageIcon("src/resources/home/0-quit.png").getImage();

        levelSelectBg = new ImageIcon("src/resources/levelSelect/1-background.png").getImage();
        checkmark = new ImageIcon("src/resources/levelSelect/1-checkmark.png").getImage();
        levelCover = new ImageIcon("src/resources/levelSelect/1-levelCover.png").getImage();
        home = new ImageIcon("src/resources/levelSelect/1-home.png").getImage();

        gameBg = new ImageIcon("src/resources/game/2-background.png").getImage();
        returnButton = new ImageIcon("src/resources/game/2-return.png").getImage();
        returnMenu = new ImageIcon("src/resources/game/2-returnMenu.png").getImage();
        infiniteMode = new ImageIcon("src/resources/game/2-infiniteMode.png").getImage();

        pCrystal = new Image[]{new ImageIcon("src/resources/game/crystals/2-purple.png").getImage(), new ImageIcon("src/resources/game/crystals/2-purpleS.png").getImage()};
        rCrystal = new Image[]{new ImageIcon("src/resources/game/crystals/2-red.png").getImage(), new ImageIcon("src/resources/game/crystals/2-redS.png").getImage()};
        oCrystal = new Image[]{new ImageIcon("src/resources/game/crystals/2-orange.png").getImage(), new ImageIcon("src/resources/game/crystals/2-orangeS.png").getImage()};
        gCrystal = new Image[]{new ImageIcon("src/resources/game/crystals/2-green.png").getImage(), new ImageIcon("src/resources/game/crystals/2-greenS.png").getImage()};
        piCrystal = new Image[]{new ImageIcon("src/resources/game/crystals/2-pink.png").getImage(), new ImageIcon("src/resources/game/crystals/2-pinkS.png").getImage()};
        dynamite = new Image[]{new ImageIcon("src/resources/game/crystals/2-dynamite.png").getImage()};

        images = new Image[][]{rCrystal, oCrystal, gCrystal, pCrystal, piCrystal, dynamite};

        completeMenu = new ImageIcon("src/resources/game/2-complete.png").getImage();
        failedMenu = new ImageIcon("src/resources/game/2-failed.png").getImage();
        
        screen = 0; // set default screen to home
        setPreferredSize(new Dimension(1280, 720));
        addMouseListener(this);
        addMouseMotionListener(this);
    }


    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        if (screen == 0) drawHome(g); // home screen
        else if (screen == 1) drawLevelSelect(g); // level select screen
        else if (screen == 2) drawGame(g); // game screen

    }


    /**
     * Draws the home screen.
     * 
     * @param g graphics object used for drawing
     */
    private void drawHome(Graphics g){
        g.drawImage(homeBg, 0, 0, getWidth(), getHeight(), this); 
        g.drawImage(logo, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(play, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(quit, 0, 0, getWidth(), getHeight(), this);
    }


    /**
     * Draws the level select screen.
     * 
     * @param g graphics object used for drawing
     */
    private void drawLevelSelect(Graphics g){
        g.drawImage(levelSelectBg, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(home, 0, 0, getWidth(), getHeight(), this);
        int levelNum = game.getLevelNum(); // get level number


        // show completed and hidden/locked levels
        for (int i = 1; i <= 7; i++){

            // show completed levels
            if (i < levelNum){
                if (i <= 4) g.drawImage(checkmark, (i-1) * 194, 0, getWidth(), getHeight(), this); // row 1
                else g.drawImage(checkmark, (i-5) * 194, 155, getWidth(), getHeight(), this); // row 2
            }
            
            // show hidden/locked levels
            else if (i > levelNum){
                if (i <= 4) g.drawImage(levelCover, (i-1) * 194, 0, getWidth(), getHeight(), this); // row 1
                else g.drawImage(levelCover, (i-5) * 194, 155, getWidth(), getHeight(), this); // row 2
            }
        }


        // display high score 
        Graphics g2d = (Graphics2D) g;
        g2d.setColor(Color.white);
        Font font = new Font("HelveticaNeue", Font.BOLD, 36);
        g.setFont(font);
        String text = "Highest Score: " + String.valueOf(game.getHighScore());
        g.drawString(text, 465, 640);
    }


    /**
     * Draws game screen, including game board, level card, and return button.
     * 
     * @param g graphics object used for drawing
     */
    private void drawGame(Graphics g){
        g.drawImage(gameBg, 0, 0, getWidth(), getHeight(), this);
        drawBoard(g); // draw game board
        drawLevelCard(g);
        g.drawImage(returnButton, 0, 0, getWidth(), getHeight(), this);

        if (onReturnMenu){ // if return button pressed
            fadeScreen(g);
            g.drawImage(returnMenu, 0, 0, getWidth(), getHeight(), this);
        }

        if (game.getCurrentLevel().isWon()){ // check if game won
            fadeScreen(g);
            g.drawImage(completeMenu, 0, 0, getWidth(), getHeight(), this);
            
            // display total score on win screen
            Graphics g2d = (Graphics2D) g;
            g2d.setColor(Color.white);
            Font font = new Font("HelveticaNeue", Font.ITALIC, 25);
            g.setFont(font);
            String text = "Total Score: " + String.valueOf(game.getCurrentLevel().getScore());
            g.drawString(text, centerHorizontal(font, text, g), 350);
        }
        else if (game.getCurrentLevel().isLost()){ // game lost
            fadeScreen(g);
            g.drawImage(failedMenu, 0, 0, getWidth(), getHeight(), this);

            // display total score on loss screen
            Graphics g2d = (Graphics2D) g;
            g2d.setColor(Color.white);
            Font font = new Font("HelveticaNeue", Font.ITALIC, 25);
            g.setFont(font);
            String text = "Total Score: " + String.valueOf(game.getCurrentLevel().getScore());
            g.drawString(text, centerHorizontal(font, text, g), 300);
        }
    }

    
    /**
     * Draws level card to display moves remaining, moves limit, and score.
     * 
     * @param g graphics object used for drawing
     */ 
    private void drawLevelCard(Graphics g){
        Graphics g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);

        if (game.getCurrentLevel().isInfinite()){
            g.drawImage(infiniteMode, 0, 0, getWidth(), getHeight(), this); // special info card for infinite level

            // only display total score
            String totalScore = String.valueOf(game.getCurrentLevel().getScore()); 
            Font font2 = new Font("HelveticaNeue", Font.BOLD, 25);
            g.setFont(font2);
            g.drawString("Score: " + totalScore, 1020, 400);
            return;
        }
        
        // draw level number for normal level
        g2d.setFont(reggaeOne);
        String levelNum = String.valueOf(game.getLevelNum());
        g.drawString(levelNum, 1097, 267);

        // draw moves remaining
        Font font = new Font("HelveticaNeue", Font.BOLD, 16);
        g.setFont(font);
        g.drawString("Moves", 1031, 329);
        g.drawString("Remaining:", 1031, 345);

        String movesLeft = String.valueOf(game.getCurrentLevel().getMovesLeft());
        Font font1 = new Font("HelveticaNeue", Font.BOLD, 50);
        g.setFont(font1);
        g.drawString(movesLeft, 1136, 347);

        // draw number of crystals still required 
        String required = String.valueOf(game.getCurrentLevel().getTargetScore() - game.getCurrentLevel().getNumAcquired()); 
        if (Integer.parseInt(required) <= 0) required = "0";
        g.drawString(required, 1122, 440);

        // draw target crystal
        String type = game.getCurrentLevel().getTargetType();
        for (int i = 0; i < types.length; i++){
            if (type.equals(types[i])) g.drawImage(images[i][0], 1040, 390, 70, 70, this);
        }

        // display total score
        String totalScore = String.valueOf(game.getCurrentLevel().getScore());
        Font font2 = new Font("HelveticaNeue", Font.BOLD, 25);
        g.setFont(font2);
        g.drawString("Score: " + totalScore, 1020, 515);
    }


    /**
     * Draws all crystals on the board.
     * 
     * @param g graphics object for drawing
     */
    private void drawBoard(Graphics g){
        Crystal[][] crystals = board.getCrystals();

        for (int x = 0; x < board.getNumCols(); x++){
            for (int y = 0; y < board.getNumRows(); y++){
                int crystalX = 357 + 72*x; // calculate absolute x pixel position
                int crystalY = 75 + 72*y; // calculate absolute y pixel position
                
                if (crystals[x][y] != null){
                    Crystal crystal = crystals[x][y]; 

                    // draw crystal at its absolute position if not being dragged
                    if (crystal != draggedCrystal) drawCrystal(crystal, g, crystalX, crystalY + crystal.getPy()); 
                }
            }
        }


        if (draggedCrystal != null){ // crystal is currently being dragged by user
            int[] pos = draggedCrystal.getPosition(); 
            int crystalX = 357 + 72*pos[0] + dragOffsetX; // calculate new x position
            int crystalY = 75 + 72*pos[1] + dragOffsetY; // calculate new y position

            drawCrystal(draggedCrystal, g, crystalX, crystalY); // draw crystal where user is holding i t
        }
    }


    /**
     * Helper method to draw a crystal at specific position; accounts for special version of crystal.
     * 
     * @param crystal the crystal to draw
     * @param g graphics object used for drawing
     * @param crystalX the x pixel position to draw the crystal at
     * @param crystalY the y pixel position to draw the crystal at
     */
    private void drawCrystal(Crystal crystal, Graphics g, int crystalX, int crystalY){
        for (int i = 0; i < types.length; i++){
            if (crystal.getType().equals(types[i])) {
                if (crystal.isSpecial()) g.drawImage(images[i][1], crystalX, crystalY, crystalSize, crystalSize, this); // draws special crystal 
                else g.drawImage(images[i][0], crystalX, crystalY, crystalSize, crystalSize, this); // draws default crysatl
            }
        }
    }


    /**
     * Starts the cascade process after a valid move, including activating special crystals, updating game state, and starting timers for animation.
     * 
     * @param c1 first crystal being swapped
     * @param c2 second crystal being swapped
     */
    private void startCascade(Crystal c1, Crystal c2){
        boolean special = c1.isSpecial() || c2.isSpecial() || c1.getType().equals("d") || c2.getType().equals("d"); // check if item is special
        
        if (board.isValidMove(c1, c2)){ // check if valid move
            if (!game.getCurrentLevel().isInfinite()) game.getCurrentLevel().decreaseMoves(); // decrease moves remaining if not in infinite mode
            board.swapElements(c1, c2); // swap crystals


            // activate special crystals
            if (special){
                board.activateSpecial(c1);
                board.activateSpecial(c2);
            }
            
            step = 0; // reset step for cascade process

            // start timer for cascade animation
            int delay = 200;
            cascadeTimer = new Timer(delay, e-> runCascade());
            cascadeTimer.start();
        }
        else return; // return if invalid move
        repaint();
    }


    /**
     * Runs the cascade process, including activating special crystals, crushing crystals, dropping crystals, and checking for combos and game status updates. 
     * Called repeatedly by cascadeTimer to create animation effect.
     */
    private void runCascade(){
        if (step == 0) board.updateStates(); // scan board and update states
        else if (step == 1){
            board.crush(); // remove crystals that have been marked
            game.getCurrentLevel().addNumAcquired(board.getTargetsAcquired()); // update number of target crystals collected
        } 
        else if (step == 2) {
            board.drop(); // update crystal positions and fill empty spaces
            board.setTargetsAcquired(0); // reset target crystals acquired for next cascade
            cascadeTimer.stop(); // stop cascade timer while crystals are dropping
            startFallAnimation(); // start fall animation for crystals
            return;
        }
        else if (step == 3){
            if (!board.isFullyCrushed()) { // check if another cascade is required
                board.setComboMultiplier(board.getComboMultiplier() + 1); // increase combo multiplier for next cascade
                step = -1; // reset process
            }
            else {
                game.getCurrentLevel().updateGameStatus(); // update game status, checking win/loss conditions
                board.setComboMultiplier(1); // reset multiplier
                cascadeTimer.stop();
                repaint();
                return;
            }
        }
        step++; // go to next step
        repaint();
    }


    /**
     * Starts the falling animation for crystals that need to drop after a cascade. 
     * Called by runCascade after crystals have been marked to drop.
     */
    private void startFallAnimation(){
        fallTimer = new Timer(16, e->{
            boolean stillFalling = false;
            Crystal[][] crystals = board.getCrystals();

            for (int x = 0; x < board.getNumCols(); x++){
                for (int y = 0; y < board.getNumRows(); y++){
                    Crystal c = crystals[x][y];

                    // if crystal needs to fall, update its position
                    if (c != null && c.isFalling()){ 
                        c.drop();
                        if (c.getPy()>=0){ // if past its target position, set offset to 0
                            c.setPy(0);
                            c.setFalling(false);
                        }
                        else stillFalling = true;   
                    }
                }
            }
            repaint();

            if (!stillFalling){
                fallTimer.stop(); // stop timer if no more crystals are falling
                step++;

                cascadeTimer.start(); // resume cascade timer
                repaint();
            }
        });
        fallTimer.start();
    }


    /**
     * Helper method to stop all timers and reset step.
     */
    private void stopTimers(){
        board.setComboMultiplier(1);
        if (cascadeTimer != null){
            cascadeTimer.stop();
            cascadeTimer = null;
        }
        if (fallTimer != null){
            fallTimer.stop();
            fallTimer = null;
        }
        step = 0;
    }


    /**
     * Helper method to fade screen.
     * 
     * @param g graphics object used for drawing
     */
    private void fadeScreen(Graphics g){
        Color fade = new Color(0, 0, 0,95);
        g.setColor(fade);
        g.fillRect(0, 0, 1280, 720);
    }

    
     /**
     * Helper method to center text horizontally on the screen.
     * 
     * @param font font used for text
     * @param text string to display
     * @param g graphics object used for drawing
     * @return x coordinate of centered text
     */
    private int centerHorizontal(Font font, String text, Graphics g){
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text); // get width of text
        int x = (1280 - textWidth)/2; // calculate new x coordinate

        return x;
    }


    @Override
    public void mousePressed(MouseEvent e){
        int x = e.getX();
        int y = e.getY();

        if (screen == 0){ // on home screen
            if (inButton(x, y, playX, playY, playWidth, playHeight)) screen = 1; // play button pressed 
            else if (inButton(x, y, quitX, quitY, quitWidth, quitHeight)) System.exit(0); // quit button pressed
            repaint();
        }
        else if (screen == 1){ // on level select sceren
            int currentLevel = game.getLevelNum();
            int levelX = 286 + ((currentLevel - 1)%4) * 194; // calculate x position of current level button
            int levelY = (currentLevel/5)*155 + 266; // calculate y position of current level button
            int levelWidth = 110;

            if (inButton(x, y, homeX, homeY, homeWidth, homeWidth)) screen = 0; // home button pressed
            else if (inButton(x, y, levelX, levelY, levelWidth, levelWidth)) { // level button pressed
                screen = 2; // switch to game screen
                prevLevel = game.getLevelIndex(); // store level index in case infinite mode is entered
                board.initialize();
            }
            else if (inButton(x, y, infiniteX, infiniteY, infiniteSize, infiniteSize)) { // infinite gamemode button pressed
                screen = 2; // switch to game screen
                prevLevel = game.getLevelIndex();
                game.setLevel(7); 
                board.initialize();
            }
            repaint();
        }
        else if (screen == 2){ // on game screen

            // on return menu screen
            if (onReturnMenu){

                // yes pressed, return to level select
                if (inButton(x, y, ynX, ynY, ynWidth, ynHeight)) { 
                    screen = 1; 

                    onReturnMenu = false;
                    stopTimers();
                    Level l = game.getCurrentLevel();

                    if (l.isInfinite()) {
                        if (l.getScore() > game.getHighScore()) game.setHighScore(l.getScore()); // update high score
                        l.reset(); // reset score
                        game.setLevel(prevLevel); // return to previous level before infinite mode was entered
                        repaint();

                        return;
                    }

                    l.reset(); // reset level
                }

                // no pressed, return to game
                else if (inButton(x, y, ynX + 221, ynY, ynWidth, ynHeight)) onReturnMenu = false;
            }

            // check if game won
            else if (game.getCurrentLevel().isWon()){ 
                if (inButton(x, y, exitX, exitY, exitWidth, exitHeight)){ // exit button pressed, return to level select
                    screen = 1;

                    Level l = game.getCurrentLevel();
                    if (l.getScore() > game.getHighScore()) game.setHighScore(l.getScore()); // update high score if won

                    game.nextLevel(); // go to next level
                }
            }

            // check if game lost
            else if (game.getCurrentLevel().isLost()){ 
                if (inButton(x, y, exitX, exitY, exitWidth, exitHeight)){ // exit button pressed, return to level select
                    screen = 1;
                    stopTimers();
                    game.getCurrentLevel().reset(); // reset level 
                }
            }

            // check if return button pressed
            else if (inButton(x, y, returnX, returnY, returnWidth, returnWidth)) onReturnMenu = true;
            
            // check if user is trying to drag a crystal
            else { 
                int boardLocalX = (x - boardX) / 72; // calculate x position on board grid
                int boardLocalY = (y - boardY) / 72; // calculate y position on board grid

                boolean cascading = (cascadeTimer != null && cascadeTimer.isRunning()) || (fallTimer != null && fallTimer.isRunning());

                // only allow user to drag once animations are done, and if click is within bounds of board
                if (!cascading && inButton(x, y, boardX, boardY, boardWidth, boardHeight)){
                    dragging = true;

                    dragStartX = x; // mouse start position
                    dragStartY = y; // mouse end position

                    startBoardX = boardLocalX; // x position of crystal being dragged
                    startBoardY = boardLocalY; // y position of crystal being dragged
                    
                    draggedCrystal = board.getCrystals()[startBoardX][startBoardY];
                    dragOffsetX = 0; // how far horizontally crystal has been dragged
                    dragOffsetY = 0; // how far vertically crystal has been dragged

                }
            }
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e){
        if (!dragging) return;
        dragging = false;

        // reset dragged crystal and offsets
        draggedCrystal = null;
        dragOffsetX = 0;
        dragOffsetY = 0;
        
        int dx = e.getX() - dragStartX; // calculate change in x
        int dy = e.getY() - dragStartY; // calculate change in y


        // tracks grid position crystal should be moved to
        int targetX = startBoardX; 
        int targetY = startBoardY;

        int dragThreshold = 40;


        // if more horizontal than vertical movement is tracked
        if (Math.abs(dx) > Math.abs(dy)){ 
            if (dx > dragThreshold) targetX++; // move crystal right
            else if (dx < -dragThreshold) targetX--; // move crystal left
        }

        // if more vertical than horizontal movement is tracked
        else {
            if (dy > dragThreshold) targetY++; // move crystal down
            else if (dy < -dragThreshold) targetY--; // move crystal up
        }

        // ensure being dragged within board limits
        if (targetX < 0 ||
            targetX >= board.getNumCols() ||
            targetY < 0 ||
            targetY >= board.getNumRows()){
            return;
        }

        Crystal c1 = board.getCrystals()[startBoardX][startBoardY]; // first crystal
        Crystal c2 = board.getCrystals()[targetX][targetY]; // second crystal

        startCascade(c1, c2); // swap two crystals 

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e){}

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}

    @Override
    public void mouseMoved(MouseEvent e){}

    @Override
    public void mouseDragged(MouseEvent e){
        if (dragging){
            int rawDx = e.getX() - dragStartX; // raw change in x
            int rawDy = e.getY() - dragStartY; // raw change in y

            int cellSize = 72;


            // if more horizontal than vertical movement
            if (Math.abs(rawDx) > Math.abs(rawDy)){
                if (rawDx > 0 && startBoardX >= board.getNumCols() - 1) return; // can't go right
                if (rawDx < 0 && startBoardX <= 0) return; // can't go left

                dragOffsetX = restrict(rawDx, -cellSize, cellSize); // only allow horizontal movement
                dragOffsetY = 0; // no vertical movement
            }

            // if more vertical than horizontal movement
            else {
                if (rawDy > 0 && startBoardY >= board.getNumRows() - 1) return; // can't go down
                if (rawDy < 0 && startBoardY <= 0) return; // can't go up

                dragOffsetY = restrict(rawDy, -cellSize, cellSize); // only allow vertical movement
                dragOffsetX = 0; // no horizontal movement
            }
            
            repaint();
        }
    }


    /**
     * Helper method that only allows user to drag crystal within adjacent boxes.
     * @param value number of pixels moved
     * @param min pixels to the left that can be moved to
     * @param max pixels to the right that can be moved to
     * @return max number of pixels to be moved to
     */
    private int restrict(int value, int min, int max){
        return Math.max(min, Math.min(max, value)); // return maximum change in position of crystal
    }


    /**
     * Helper method to determine whether or not mouse was pressed in a target location.
     * 
     * @param x x posiion of click
     * @param y y position of click
     * @param buttonX x position of button on screen
     * @param buttonY y position of button on screen
     * @param buttonWidth width of button
     * @param buttonHeight height of button
     * @return true if given coordinates lie within the button, false otherwise
     */
    private boolean inButton(int x, int y, int buttonX, int buttonY, int buttonWidth, int buttonHeight){
        if (x >= buttonX && 
            x <= buttonX + buttonWidth && 
            y >= buttonY && 
            y <= buttonY + buttonHeight){
                return true;
        }
        return false;
    }
}
    