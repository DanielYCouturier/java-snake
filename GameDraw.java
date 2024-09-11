import java.awt.Color;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.util.LinkedList;
import java.util.TreeSet;

/*
 * this class is heavily modeled after StdDraw by Robert Sedgewick and Kevin Wayne.
 * and many of the methods should be credited to their work:
 * https://introcs.cs.princeton.edu/java/stdlib/StdDraw.java.html
 * the original requirments for this project reommended the use
 * of the StdDraw class. this redesign is to prevent visual artifacts 
 * which appear because their implementation does not perfectly fit this project.
 */
public final class GameDraw implements KeyListener {
   // CONSTANT PARAMATERS; CAN MODIFY
   public static final Color BACKGROUND_COLOR = Color.BLACK;
   private static final Color BORDER_COLOR = Color.WHITE;
   private static final Color TEXT_COLOR = Color.BLUE;
   // NON PARAMATERS DO NOT CHANGE
   private boolean initialized = false;
   private int xmax, ymax; // game resolution
   private double xScale, yScale; // number of windowPixels per gamePixel
   // graphics objects
   private BufferedImage offscreenImage, onscreenImage;
   private Graphics2D offscreen, onscreen;
   private JFrame frame = new JFrame();
   // keyboard objects
   private LinkedList<Direction> queuedMoves = new LinkedList<Direction>();
   private TreeSet<Integer> keysDown = new TreeSet<Integer>();
   private static final int ESCAPE = 27;
   private static final int LEFT = 37; 
   private static final int UP = 38; 
   private static final int RIGHT = 39;
   private static final int DOWN = 40;
   private static final double PBW = 0.1; // pixel border width
   public GameDraw (final Coordinate windowRes, final Coordinate gameRes) {
      // whole method is just initializing window with correct resolution and scale
      xScale = windowRes.x() / gameRes.x();
      yScale = windowRes.y() / gameRes.y();
      xmax = gameRes.x();
      ymax = gameRes.y();
      // all JFrame and Graphic2d code credit to S&W
      offscreenImage = new BufferedImage(windowRes.x(), windowRes.y(), 
            BufferedImage.TYPE_INT_ARGB);
      onscreenImage = new BufferedImage(windowRes.x(), windowRes.y(), 
            BufferedImage.TYPE_INT_ARGB);
      offscreen = offscreenImage.createGraphics();
      onscreen = onscreenImage.createGraphics();
      frame.setTitle("Snake.java");
      frame.setContentPane(new JLabel(new ImageIcon(onscreenImage)));
      frame.addKeyListener(this);
      frame.setResizable(false);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
   }
   // draws edges of game map
   public void drawBorder () {
      filledRectangle(0, 0, xmax, ymax, BACKGROUND_COLOR);
      filledRectangle(xmax / 2.0, ymax / 2.0, (xmax - 1) / 2.0, (ymax - 1) / 2.0, 
            BORDER_COLOR);
      filledRectangle(xmax / 2.0, ymax / 2.0, (xmax - 2 +2*PBW) / 2.0, 
            (ymax - 2+2*PBW) / 2.0, BACKGROUND_COLOR);
   }

   // ensures coordinate is contained in game map
   public boolean isBounded (final Coordinate test) {
      if (test.x() >= xmax - 1) {
         return false;
      }
      if (test.x() <= 0) {
         return false;
      }
      if (test.y() >= ymax - 1) {
         return false;
      }
      if (test.y() <= 0) {
         return false;
      }
      return true;
   }

   public void drawPixel (final Direction orientation, 
         final Coordinate pixel, final Color color) {
      /*
       * given a game coordinate pixel has a width and height of 1 unit,
       * this method draws the actual grapical pixel slightly smaller than 1
       * in order to add edges where appropriate.
       * given an orientation of COPY (null) it assumes a normal pixel
       * but all other orientations add body segment interpolation,
       * in other words, it connects the new pixel to the previous one.
       */

      // if trying to erase the pixel to background color,
      // then w,h must be > 0.5 to draw over neighboring interpolated edges
      if (color == BACKGROUND_COLOR) {
         filledRectangle(pixel.x() + 0.5, pixel.y() + 0.5, 0.5+PBW, 0.5+PBW, color);
         return;
      }
      switch (orientation) {
      case UP: // draw next snake bodysegment assuming snake is moving UP
         filledRectangle(pixel.x() + 0.5, pixel.y() + 0.5-PBW, 0.5-PBW, 0.5, color);
         return;
      case DOWN: // draw next snake bodysegment assuming snake is moving DOWN
         filledRectangle(pixel.x() + 0.5, pixel.y() + 0.5+PBW, 0.5-PBW, 0.5, color);
         return;
      case LEFT: // draw next snake bodysegment assuming snake is moving LEFT
         filledRectangle(pixel.x() + 0.5+PBW, pixel.y() + 0.5, 0.5, 0.5-PBW, color);
         return;
      case RIGHT: // draw next snake bodysegment assuming snake is moving RIGHT
         filledRectangle(pixel.x() + 0.5-PBW, pixel.y() + 0.5, 0.5, 0.5-PBW, color);
         return;
      default: // "normal" pixel used for things like drawing food
         filledRectangle(pixel.x() + 0.5, pixel.y() + 0.5, 0.5-PBW, 0.5-PBW, color);
         return;
      }
   }

   // does not use geom library because of artifacts
   public void filledRectangle (final double x, final double y, 
         final double halfWidth, final double halfHeight, final Color color) {
      offscreen.setColor(color);
      final Long arg0 = Math.round(xScale * (x - halfWidth)); // x0
      final Long arg1 = Math.round(yScale * (ymax - y - halfHeight)); // y0
      final Long arg2 = Math.round(2 * xScale * halfWidth); // w
      final Long arg3 = Math.round(2 * yScale * halfHeight); // h
      offscreen.fillRect(arg0.intValue(), arg1.intValue(), arg2.intValue(), arg3.intValue());
   }

   // is called on Game update to get queue of any inputs since last update
   public LinkedList<Direction> getRecentInputs () {
      final LinkedList<Direction> output = new LinkedList<Direction>();
      for (int i = 0; i < queuedMoves.size(); i++) {
         output.add(queuedMoves.get(i)); // LinkedList.deepCopy
      }
      queuedMoves = new LinkedList<Direction>(); // reset to empty array
      return output;
   }

   // used for menuing
   public boolean isInput () {
      return keysDown.size() > 0;
   }

   // game directional movement
   private void addMove (final KeyEvent e) {
      Direction newKey = Direction.COPY; // if no directional keys pressed return here
      switch (e.getKeyCode()) { // e.getKeyCode() might not work cross-platform
      // check if input is arrow key
      case ESCAPE: // esc key pressed
         frame.dispose();
            System.exit(0); 
         return;
      case LEFT:
         newKey = Direction.LEFT;
         break;
      case UP:
         newKey = Direction.UP;
         break;
      case RIGHT:
         newKey = Direction.RIGHT;
         break;
      case DOWN:
         newKey = Direction.DOWN;
         break;
      default:
         break;
      }
      switch (Character.toUpperCase(e.getKeyChar())) { // should always work cross-platform
      // check if input is wasd
      case 'W':
         newKey = Direction.UP;
         break;
      case 'A':
         newKey = Direction.LEFT;
         break;
      case 'S':
         newKey = Direction.DOWN;
         break;
      case 'D':
         newKey = Direction.RIGHT;
         break;
      default:
         break;
      }
      if (newKey == Direction.COPY) { // continue if directional key;
         return;
      }
      // rest of method is to make sure two identical sequential inputs are cached
      if (queuedMoves.size() == 0) {
         queuedMoves.addLast(newKey);
         return;
      }
      if (newKey != queuedMoves.getLast()) {
         queuedMoves.addLast(newKey);
      }
   }

   // credit to S&W. TODO: proper BufferMethod for performance, and to not force window focus
   public void show () {
      if (!initialized) {
         frame.setVisible(true);
      }
      onscreen.drawImage(offscreenImage, 0, 0, null);
      frame.repaint();
   }

   // Credit to S&W
   public void text (final double x, final double y, final String text) {
      final Font font = new Font("SansSerif", Font.PLAIN, 16);
      offscreen.setColor(TEXT_COLOR);
      offscreen.setFont(font);
      final FontMetrics metrics = offscreen.getFontMetrics();
      final double xs = x * xScale;
      final double ys = y * yScale;
      final Double arg1 = xs-metrics.stringWidth(text)/2.0;
      final Double arg2 = ys+metrics.getDescent();
      offscreen.drawString(text, arg1.floatValue(), arg2.floatValue());
   }

   // credit to S&W
   @Override
   public void keyPressed (final KeyEvent e) {
      keysDown.add(e.getKeyCode());
      addMove(e); // call game helper method
   }

   // credit to S&W
   @Override
   public void keyReleased (final KeyEvent e) {
      keysDown.removeAll(keysDown); // remove all because sticky keys when alt tab
   }

   // isnt used in program
   @Deprecated
   @Override
   public void keyTyped (final KeyEvent e) {
   }

}
