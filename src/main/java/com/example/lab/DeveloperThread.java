package com.example.lab;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.Random;

public class DeveloperThread extends BaseAI {

    private final TranslateTransition transition = new TranslateTransition();
    private final Developer developer;
    private static boolean isPlay = true;
    private final int moveTime;
    private double moveX = getRandom();
    private double moveY = getRandom();
    private boolean isEnd = false;

    public DeveloperThread(Developer developer, int speed, int moveTime) {
        super(speed);
        this.developer = developer;
        this.moveTime = moveTime;
        setPriority(8);
    }

    @Override
    public void run() {
        try {
            synchronized (developer) {
                ImageView imageView = developer.getImageView();
                double seconds = (new Random().nextDouble(1, 4.5)) / (getSpeed() * 0.25);
                double x0 = imageView.getX();
                double y0 = imageView.getX();
                double x1 = 0;
                double y1 = 0;

                while (true) {
                    if (isEnd) {
                        break;
                    }
                    if (!transition.getStatus().equals(Animation.Status.RUNNING)) {
                        x1 = new Random().nextDouble(50, 100);
                        y1 = new Random().nextDouble(50, 70);

//                        x1 = 50;
//                        y1 = 50;

//                        if (x0 + x1 < 100) {
//                            x1 = -x1;
//                        } else if (x0 + x1 > 170) {
//                            x1 = -x1;
//                        }
//                        if (y0 + y1 < 100) {
//                            y1 = -y1;
//                        } else if (y0 + y1 > 220) {
//                            y1 = -y1;
//                        }

                        if (x0 + x1 < 100) {
//                            x1 = 70-x0;
                            x1 = -x1;
                        } else if (x0 + x1 < 70) {
                            x1 = 70 - x0;
                        } else if (x0 + x1 > 450) {
                            x1 = 450 - x0;
                        } else if (x0 + x1 > 330) {
//                            x1 = 330-x0;
                            x1 = -x1;
                        }

                        if (y0 + y1 < 100) {
//                            y1 = 70-y0;
                            y1 = -y1;
                        } else if (y0 + y1 < 70) {
                            y1 = 70 - y0;
                        } else if (y0 + y1 > 500) {
                            y1 = 500 - y0;
                        } else if (y0 + y1 > 380) {
//                            y1 = 380-y0;
                            y1 = -y1;
                        }

                        transition.setDuration(Duration.seconds(seconds));
                        transition.setNode(imageView);
                        transition.toXProperty().setValue(x0);
                        transition.toYProperty().setValue(y0);
                        transition.setToX(x1);
                        transition.setToY(y1);
                        transition.setCycleCount(0);
//                        transition.setFromX(x0);
//                        transition.setFromY(y0);
//                        transition.setByX(x1);
//                        transition.setByY(y1);
//                        transition.play();

                        if (!isPlay && transition.getStatus().equals(Animation.Status.RUNNING)) {
                            transition.pause();
                        } else if (isPlay && !transition.getStatus().equals(Animation.Status.RUNNING)) {
                            transition.play();
                        }
                    }

                    developer.wait(Math.round(seconds) * 1000L);
                    transition.stop();
//                    x0 = transition.toXProperty().get();
//                    y0 = transition.toYProperty().get();
                    x0 += x1;
                    y0 += y1;
//                    System.out.println(getName() + " " + (int) x0 + " " + (int) y0);
//                    System.out.println(getName() + " " + transition.getFromX() + " " + transition.getFromY());



                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public void setPlay() {
        isPlay = true;
    }

    public void setPause() {
        isPlay = false;
    }

    private Path createPath(double x, double y) {
        LineTo lineTo = new LineTo(x, y);

        Path path = new Path();
        path.getElements().addAll(
                new MoveTo(x, y),
                lineTo,
                new ClosePath()); // close 1 px gap.
        path.setStroke(Color.MAGENTA);
        path.getStrokeDashArray().setAll(5d, 5d);
        return path;
    }

    private double getRandom() {
        return new Random().nextDouble(-2, 2);
    }

    private void move1() throws InterruptedException {
        int countMoveChanges = 0;
        while (true) {
            if ((Habitat.getTimeInSeconds() - developer.getAppearanceTime()) / moveTime
                    != countMoveChanges) {
                moveX = getRandom();
                moveY = getRandom();
                countMoveChanges++;
            }

            ImageView imageView = developer.getImageView();

            double x = imageView.getX() + moveX * getSpeed() * 0.5;
            double y = imageView.getY() + moveY * getSpeed() * 0.5;

            if (x > 500) {
                x = 0;
            } else if (x < 0) {
                x = 500;
            }
            if (y > 550) {
                y = 0;
            } else if (y < 0) {
                y = 550;
            }

            imageView.setX(x);
            imageView.setY(y);
            developer.setImageView(imageView);

            developer.wait(70L);
        }
    }

    public TranslateTransition getTransition() {
        return transition;
    }
}
