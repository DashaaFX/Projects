import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

//Dashnyam Puntsagnorov
//CSC 233 Final Project
//basketball class
class basketball {
    double x, y, width, height, distanceX, distanceY;
    //boolean that checks if basketball object is 'good' or 'bad' ball
    boolean isGoodBall; 
    //determine images for good and bad balls
    static final Image goodball = new Image("basketball.png");
    static final Image badball = new Image("badball.png");
    //constructor
    public basketball(double x, double y, double width, double height, boolean isGoodBall) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isGoodBall = isGoodBall;
    }

    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public void wrap()
    {
        // right edge of canvas
        if (x > 600)
            x = -width;
            
        // left edge of canvas
        if (x < -width)
            x = 600;
            
        // bottom edge of canvas
        if (y > 600)
            y = -height;
            
        // top edge of canvas
        if (y < -height)
            y = 600;
    }
    
    public void draw(GraphicsContext gc) {
        Image image;
        //determine if a ball is good ball or bad ball
        if (isGoodBall) {
            image = goodball;
        } else {
            image = badball;
        }
        //draw the ball on canvas
        gc.drawImage(image, x, y, width, height);
    }
}
