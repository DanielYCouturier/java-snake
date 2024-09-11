import java.util.Random;

public record Coordinate(int x, int y) { // 2-tuple data type with extra methods.
   // returns adjacent coordinate in a direction
   public Coordinate copy (final Direction direction) { 
      switch (direction) {
      case UP:
         return new Coordinate(x, y + 1);
      case DOWN:
         return new Coordinate(x, y - 1);
      case LEFT:
         return new Coordinate(x - 1, y);
      case RIGHT:
         return new Coordinate(x + 1, y);
      default:
         return new Coordinate(x, y);
      }
   }

   public static Coordinate random (final Coordinate input) {
      final Random RNG = new Random();
      return new Coordinate(RNG.nextInt(input.x - 2) + 1, RNG.nextInt(input.y - 2) + 1);
   }
}
