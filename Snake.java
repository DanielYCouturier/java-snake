import java.awt.Color;
import java.util.LinkedList;

public class Snake {
   private final LinkedList<Coordinate> body = new LinkedList<Coordinate>();
   private final GameDraw canvas; // shared pointer with other classes
   private final Color snakeColor;
   private int increase;
   // initialize snake
   public Snake (final GameDraw canvas, final Coordinate snakePos, 
         final int snakeSize, final Color snakeColor) { 
      body.add(snakePos);
      this.canvas = canvas;
      this.snakeColor = snakeColor;
      increase = Math.max(1, snakeSize - 1);
   }
   // TODO: draw more detailed head
   public void drawHead (final Direction direction) {
      canvas.drawPixel(direction, body.getFirst(), snakeColor);
   }
   // return head coordinate 
   public Coordinate getHead () {
      return body.getFirst();
   }
   // return snake length
   public int getSize () {
      return body.size();
   }

   public boolean update (final Direction direction) {
      if (increase == 0) { // if snake isnt growing
         // paint over old tail
         canvas.drawPixel(Direction.COPY, body.getLast(), GameDraw.BACKGROUND_COLOR); 
         body.removeLast(); // remove tail
      } else {
         increase--;
      }
      body.addFirst(body.getFirst().copy(direction)); // move head directions
      canvas.drawPixel(direction, body.getFirst(), snakeColor); // paint new head
      return !intersectsSnake(getHead()) && canvas.isBounded(getHead());
   }
   // increase body length on next update
   public void grow (final int amount) {
      increase += amount;
   }
   // if coordinate intersects snake
   public boolean intersectsSnake (final Coordinate input) {
      for (final Coordinate i : body) {
         if (input.equals(i) && input != i) {
            return true;
         }
      }
      return false;
   }
}
