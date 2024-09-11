import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
/*
 * Author: Daniel Couturier, xxdanielcouturierxx@gmail.com
 * Course: CSE 1002, Section 1, Fall 2022
 * Project: Snake
*/
public class Game {
   // GAME PARAMATERS (feel free to change)
   private static final Coordinate RESOLUTION = new Coordinate(32, 16);
   private static final Coordinate WINDOW_RES = new Coordinate(1024, 512);
   private static final int FOOD_COUNT = 2; // number of apples on screen
   private static final Color FOOD_COLOR = Color.RED;
   private static final long SNAKE_SPEED = 100; // time in ms between steps
   private static final Coordinate SNAKE_START 
         = new Coordinate(RESOLUTION.x() / 2, RESOLUTION.y() / 2);
   private static final int SNAKE_GROWTH_RATE = 2; 
   private static final int SNAKE_SIZE = 3;
   private static final Color SNAKE_COLOR = Color.GREEN;
   // DO NOT MODIFY; GAME OBJECTS
   private static int highScore = 0;
   private static Snake snek;
   private static ArrayList<Coordinate> food;
   private static GameDraw canvas;
   private static Direction previousMove = Direction.COPY;
   private static Direction preMove = Direction.COPY;
   private static long lastUpdate;
   public static void main (final String[] args) {
      canvas = new GameDraw(WINDOW_RES, RESOLUTION);
      while (true) { // menu loop
         init();
         game();
         endScreen();
      }
   }

   private static void init () {
      // re-initialize objects to effective starting states
      snek = new Snake(canvas, SNAKE_START, SNAKE_SIZE, SNAKE_COLOR);
      food = new ArrayList<Coordinate>();
      for (int i = 0; i < FOOD_COUNT; i++) {
         addFood();
      }
      previousMove = Direction.COPY;
      preMove = Direction.COPY;
      canvas.getRecentInputs();
      lastUpdate = System.nanoTime();
      // draw re-initialized objects
      canvas.drawBorder();
      drawFood();
      snek.drawHead(Direction.COPY);
      canvas.text(RESOLUTION.x() / 2, RESOLUTION.y() / 2 + 1, 
            "Press WASD or arrow keys to start");
      canvas.text(RESOLUTION.x() / 2, RESOLUTION.y() / 2 + 2, "Press ESCAPE to close game");
      canvas.show();
      waitForNewInput(); // cant continue to game() until user presses new key
   }

   private static void game () {
      while (update()) { // update returns true if game should continue
         canvas.show(); // canvas draw call
         pause(SNAKE_SPEED); // wait until enough time has elapsed to update again
      }
   }

   private static void endScreen () { // draw end screen
      highScore = Math.max(highScore, snek.getSize());
      canvas.text(RESOLUTION.x() / 2, RESOLUTION.y() / 2 - 1, "SCORE: " + snek.getSize());
      canvas.text(RESOLUTION.x() / 2, RESOLUTION.y() / 2, "High Score: " + highScore);
      canvas.text(RESOLUTION.x() / 2, RESOLUTION.y() / 2 + 1, "Press any key to continue");
      canvas.show();
      waitForNewInput(); // cant loop back to startScreen() until user presses new key
   }

   private static void pause (final long amount) { // precondition lastupdate is recent
      /*
       * rather than pausing a fixed amount, pause however much is needed to maintain
       * 60 fps. actually the game fps is correlated to snakeSpeed but same idea
       */
      final long waitUntil = lastUpdate + (amount * 1000000);
      while (waitUntil > System.nanoTime()) {
        ;
      }
      lastUpdate = waitUntil;
   }

   private static void waitForNewInput () { // user must release all keys then press one
      lastUpdate = System.nanoTime();
      while (canvas.isInput()) {
         pause(1);
      }
      while (!canvas.isInput()) {
         pause(1);
      }
   }

   private static boolean update () {
      final Direction currentMove = parseNextMove(canvas.getRecentInputs());
      if (currentMove == Direction.COPY) { // if no moves have been entered since init
         return true; // dont update
      }
      // else call snake.update()
      previousMove = currentMove;
      final boolean output = snek.update(currentMove);
      if (intersectsFood()) {
         snek.grow(SNAKE_GROWTH_RATE);
      }
      // no reason to reset currentMove to null because how snake is supposed to work
      return output;
   }

   private static Direction parseNextMove (final LinkedList<Direction> queuedMoves) {
      // if this is the first move entered in game loop
      if (queuedMoves.size() != 0 && previousMove == Direction.COPY) {
         // this code will only be called once per init
         // to draw over text once game starts
         canvas.drawBorder();
         snek.drawHead(Direction.COPY);
         drawFood();
         canvas.show();
         return queuedMoves.getFirst();
      }
      /*
       * the following switch statement implements input buffering
       * because the controls felt terrible without it.
       */
      switch (queuedMoves.size()) {
      case 1:
         // if exactly one key has been pressed since last update
         if (queuedMoves.getFirst().notInverse(previousMove)) { // if only move is valid
            preMove = Direction.COPY; // assert no premove exists
            return queuedMoves.getFirst(); // return input move
         }
         // else fall through because only entered an invalid move
      case 0: // if no inputs entered
         if (preMove.notInverse(previousMove)) { // but premove exists and is valid
            final Direction output = preMove; // return premove
            preMove = Direction.COPY; // set to null for next loop
            return output;
         } else {
            return previousMove; // premove isnt valid and no moves input
         }
      default: // only cares about first 2 inputs
         // if first input is valid relative to previous
         if (queuedMoves.getFirst().notInverse(previousMove)) {
            // if second input is valid relative to first
            if (queuedMoves.get(1).notInverse(queuedMoves.getFirst())) {
               // edge case for people who hold down keys too long
               if (queuedMoves.getFirst() == previousMove) {
                  return queuedMoves.get(1);
               }
               // premove is valid
               preMove = queuedMoves.get(1);
            }
            // first move is valid
            return queuedMoves.getFirst();
         } else {
            // neither is valid
            return previousMove;
         }

      }
   }

   private static void addFood () { // generates valid coordinate for food
      Coordinate newFood;
      do {
         newFood = Coordinate.random(RESOLUTION);
      } while (food.contains(newFood) || snek.intersectsSnake(newFood));
      food.add(newFood);
      canvas.drawPixel(Direction.COPY, newFood, FOOD_COLOR);
   }

   private static void drawFood () { // draws all food in food array
      for (final Coordinate eachFood : food) {
         canvas.drawPixel(Direction.COPY, eachFood, FOOD_COLOR);
      }
   }

   private static boolean intersectsFood () { // if snake collides with ANY food
      for (final Coordinate eachFood : food) {
         if (eachFood.equals(snek.getHead())) {
            addFood();
            food.remove(eachFood);
            return true;
         }
      }
      return false;
   }
}
