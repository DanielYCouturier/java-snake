public enum Direction { // immutable data type for manipulating relative coordinates
   UP, DOWN, LEFT, RIGHT, COPY;

   // used to ensure you cant move snake backwards
   public boolean notInverse (final Direction other) { 
      if (this == COPY || other == COPY) {
         return false;
      }
      if (this == Direction.LEFT && other != Direction.RIGHT) {
         return true;
      }
      if (this == Direction.RIGHT && other != Direction.LEFT) {
         return true;
      }
      if (this == Direction.DOWN && other != Direction.UP) {
         return true;
      }
      if (this == Direction.UP && other != Direction.DOWN) {
         return true;
      }
      return false;
   }
}
