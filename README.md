# Snake (Java)
[Download Jar](https://github.com/DanielCouturier/java-snake/blob/main/SnakeGame.jar)

The requirements for this project were to re-create the popular game "Snake" in Java. Originally created in 1997 for Nokia devices, the game features a snake, with its head represented by a single pixel, that moves across a 2d grid at a constant speed and a player-controlled direction. The objective is to to collide the head of the snake with a food pixel, which increases the snakes length. As the snake gets longer its body segments create a trail behind the head. If the head collides with this trail or the boundary of the grid, the player loses. The goal is to achieve the longest snake possible in real time, with the available space shrinking as the snake grows. 

In my implementation, I prioritized UI quality over optional gameplay features. Specifically, I dedicated significant time to learning the fundamentals of Java Swing rather than relying on the recommended library written by the textbook authors. This approach had the advantage of minimizing visual artifacts, especially with snake segments that didn't align perfectly with grid pixels. Additionally, it allowed for input buffering, enabling the capture of simultaneous key presses within the same frame, and greatly improving responsiveness. 

This project was originally submitted as a final project for CSE 1002 (Into to Software Development II) (2022)

Targeted for Java 17

![image](https://github.com/user-attachments/assets/6c71357b-e26a-4b51-951e-3399eef4a28d)

# Basic Design
* Coordinate.java - (x,y) tuple with extra functionality.
* Direction.java - enum(UP,DOWN,LEFT,RIGHT,COPY) with extra functionality. 
* GameDraw.java - wrapper class for relavent Swing code.
* Snake.java - object class to store and manipulate snake segments as Coordinates.
* Game.java - main game loop and control code.
