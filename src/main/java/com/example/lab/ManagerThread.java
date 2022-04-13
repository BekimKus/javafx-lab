package com.example.lab;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.Random;

public class ManagerThread extends BaseAI {

    private PathTransition pathTransitionCircle = new PathTransition();
    private final Manager manager;
    private static boolean isPlay = true;
    private double radius = new Random().nextDouble(15, 50);
    private double moveX = 1;
    private double moveY = 1;

    public ManagerThread(Manager manager, int speed) {
        super(speed);
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            synchronized (manager) {
                ImageView imageView = manager.getImageView();

                double x = imageView.getX();
                double y = imageView.getY();

                if (x + radius > 450) {
                    x = 495 - radius;
                } else if (x + radius < 100) {
                    x = radius + 100;
                }
                if (y + radius > 500) {
                    y = 545 - radius;
                } else if (y + radius < 100) {
                    y = radius + 100;
                }

                Path path2 = createCirclePath(x, y, radius);

                pathTransitionCircle.setDuration(Duration.seconds(1.0 * radius * 0.18 / (getSpeed() * 0.25)));
                pathTransitionCircle.setPath(path2);
                pathTransitionCircle.setNode(imageView);
                pathTransitionCircle.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                pathTransitionCircle.setCycleCount(Timeline.INDEFINITE);
                pathTransitionCircle.setAutoReverse(false);
                pathTransitionCircle.setOrientation(PathTransition.OrientationType.NONE);
//                pathTransitionCircle.play();

                while (true) {
                    if (!isPlay && pathTransitionCircle.getStatus().equals(Animation.Status.RUNNING)) {
                        pathTransitionCircle.pause();
                    } else if (isPlay && !pathTransitionCircle.getStatus().equals(Animation.Status.RUNNING)) {
                        pathTransitionCircle.play();
                    }
                }

            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setPlay() {
        isPlay = true;
        pathTransitionCircle.play();
    }

    public void setPause() {
        isPlay = false;
        pathTransitionCircle.pause();
    }

    private Path createCirclePath(double centerX, double centerY, double radius) {
        ArcTo arcTo = new ArcTo();
        arcTo.setX(centerX - radius + 1);
        arcTo.setY(centerY - radius);
        arcTo.setSweepFlag(false);
        arcTo.setLargeArcFlag(true);
        arcTo.setRadiusX(radius);
        arcTo.setRadiusY(radius);

        Path path = new Path();
        path.getElements().addAll(
                new MoveTo(centerX - radius, centerY - radius),
                arcTo,
                new ClosePath());
        path.setStroke(Color.DODGERBLUE);
        path.getStrokeDashArray().setAll(5d, 5d);
        return path;
    }

    private double getRandom() {
        return new Random().nextDouble(-2, 2);
    }

    public PathTransition getPathTransitionCircle() {
        return pathTransitionCircle;
    }
}
