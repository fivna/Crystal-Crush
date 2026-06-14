/**
 * Crystal class represents a single crystal in the game. 
 * Contains properties such as type, position, and its state (activated, selected, falling).
 */

public class Crystal {
    private String type;
    private boolean isSpecial;
    private boolean activated;
    private int[] position;
    private boolean falling = false;
    private int py; // tracks y offset for falling animation
    private int speed; // falling speed
    private final int baseSpeed = 2; // intial speed; before acceleration
    private final int acceleration = 5; // speed increases by 5 every repaint


    /**
     * Constructor for a Crystal class.
     * 
     * @param type crystal type (red, green, orange, pink, or purple)
     * @param isSpecial true if it has a special effect, false otherwise
     * @param x the x position on the board
     * @param y the y position on the board
     */
    public Crystal(String type, boolean isSpecial, int x, int y){
        this.type = type;
        this.isSpecial = isSpecial;
        this.activated = false;
        this.position = new int[]{x, y};
        falling = false;
        speed = baseSpeed;
        py = 0; // set initial y offset to 0
    }


    /**
     * Move crystal down and add acceleration.
     */
    public void drop(){
        py += speed;
        speed += acceleration;
    }


    /**
     * Reset speed to initial speed.
     */
    public void resetFall(){
        speed = baseSpeed;
    }


    public boolean isActivated() {
        return activated;
    }
    public void setActivated(boolean activated) {
        this.activated = activated;
    }
    public int[] getPosition(){
        return position;
    }
    public void setPosition(int x, int y){
        this.position[0] = x;
        this.position[1] = y;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public boolean isSpecial() {
        return isSpecial;
    }
    public void setSpecial(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }
    public boolean isFalling() {
        return falling;
    }
    public void setFalling(boolean falling) {
        this.falling = falling;
    }
    public int getPy() {
        return py;
    }
    public void setPy(int py) {
        this.py = py;
    }
}
