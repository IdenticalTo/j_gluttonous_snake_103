package com.flyingBird.www;

import com.flyingBird.www.entity.Direction;
import com.flyingBird.www.entity.Grade;
import com.flyingBird.www.entity.Node;
import com.flyingBird.www.entity.Snake;
import com.flyingBird.www.util.FileUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class MainFrame extends Application {
    // private static final int WIDTH = 900;
    // private static final int HEIGHT = 600;
    private static final int GIRDSIZE = 15;
    private static final int GIRDWIDTH = 600;
    private static final int GIRDHEIGHT = 600;

    private Snake snake;
    private Node food;
    private Timer timer;
    private TimerTask timerTask;

    // 创建画布
    Canvas canvas = new Canvas(GIRDWIDTH, GIRDHEIGHT);
    GraphicsContext gc = canvas.getGraphicsContext2D();

    private VBox operate;
    private Button button;
    private Label scoreLabel;
    private Label useTimeLabel;
    private int score = 0;
    private long useTime;
    private Timer useTimer;
    private Label partingLine;
    private Label bestGradeLabel;
    private Label bestScoreLabel;
    private Label bestUseTimeLabel;
    private String css = getClass().getResource("Style.css").toExternalForm();

    // 用于判断是否为启动游戏界面以来初次游戏
    private static int count = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 初始化蛇
        initSnake();
        // 初始化食物
        initFood();
        // 初始化画布
        initCanvas();
        // 初始化用户操作界面
        initPane();

        // 将画布和用户操作界面添加到场景中
        HBox root = new HBox();
        root.getChildren().addAll(canvas, operate);
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    if (snake.getDirection() != Direction.DOWN) {
                        snake.setDirection(Direction.UP);
                    }
                    break;
                case DOWN:
                    if (snake.getDirection() != Direction.UP) {
                        snake.setDirection(Direction.DOWN);
                    }
                    break;
                case LEFT:
                    if (snake.getDirection() != Direction.RIGHT) {
                        snake.setDirection(Direction.LEFT);
                    }
                    break;
                case RIGHT:
                    if (snake.getDirection() != Direction.LEFT) {
                        snake.setDirection(Direction.RIGHT);
                    }
                    break;
            }
        });
        // 显示场景
        primaryStage.setTitle("贪吃蛇");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initCanvas(){
        gc.clearRect(0, 0, GIRDWIDTH, GIRDHEIGHT);
        // 绘制格子背景
        for (int i = 0; i <= 40; i++) {
            gc.strokeLine(0, i*GIRDSIZE, GIRDWIDTH, i*GIRDSIZE); // 绘制横线
            gc.strokeLine(i*GIRDSIZE, 0, i*GIRDSIZE, GIRDHEIGHT); // 绘制竖线
        }
        // 绘制蛇
        LinkedList<Node> body = snake.getBody();
        for (Node node:
                body) {
            gc.fillRect(node.getX() * GIRDSIZE, node.getY() * GIRDSIZE, GIRDSIZE, GIRDSIZE);
        }
        // 绘制食物
        gc.fillRect(food.getX() * GIRDSIZE, food.getY() * GIRDSIZE, GIRDSIZE, GIRDSIZE);
    }
    private void initSnake(){
        snake = new Snake();
    }
    private void initFood(){
        food = new Node();
        food.random();
    }
    private void initTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                snake.move();
                initCanvas();
                // 判断是否吃到食物
                Node head = snake.getBody().getFirst();
                if (head.getX() == food.getX() && head.getY() == food.getY()) {
                    snake.eat(food);
                    food.random();
                    score += 1;
                    Platform.runLater(() -> {
                        scoreLabel.setText("得分：" + score + "分");
                    });
                }
                // 判断蛇是否死亡
                if (!snake.isLiving()) {
                    timer.cancel();
                    useTimer.cancel();

                    // 写入成绩
                    Grade grade = new Grade(String.valueOf(score), useTimeLabel.getText().split("：")[1]);
                    FileUtil.writeFile(grade);

                    // 更新成绩
                    Grade bestGrade = FileUtil.readFile();
                    Platform.runLater(() -> {
                        bestScoreLabel.setText("得分：" + bestGrade.getScore());
                        bestUseTimeLabel.setText("使用时间：" + bestGrade.getUseTime());
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 100);
    }
    private void initPane(){
        operate = new VBox();
        operate.setMinWidth(300);
        operate.setAlignment(Pos.CENTER);
        operate.getStyleClass().add("operate");
        operate.getStylesheets().add(css);

        scoreLabel = new Label("得分：" + score + "分");
        useTimeLabel = new Label("使用时间：" + "00:00:00");
        scoreLabel.getStyleClass().add("font");
        scoreLabel.getStylesheets().add(css);
        useTimeLabel.getStyleClass().add("font");
        useTimeLabel.getStylesheets().add(css);

        button = new Button();
        button.setText("开始游戏");
        button.getStyleClass().add("startBtn");
        button.getStylesheets().add(css);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (count > 0) {
                    initSnake();
                    initFood();
                    initCanvas();

                    score = 0;
                    Platform.runLater(() -> {
                        scoreLabel.setText("得分：" + score + "分");
                        useTimeLabel.setText("使用时间：" + "00:00:00");
                    });
                } else {
                    count++;
                }
                // 设置定时器使得蛇移动并吃食物
                initTimer();
                // 初始化用户操作界面计时器
                initUseTimer();
            }
        });

        partingLine = new Label("---------------");
        bestGradeLabel = new Label("历史最佳成绩");
        bestScoreLabel = new Label();
        bestUseTimeLabel = new Label();
        partingLine.getStyleClass().add("best");
        partingLine.getStylesheets().add(css);
        bestGradeLabel.getStyleClass().add("best");
        bestGradeLabel.getStylesheets().add(css);
        bestScoreLabel.getStyleClass().add("best");
        bestScoreLabel.getStylesheets().add(css);
        bestUseTimeLabel.getStyleClass().add("best");
        bestUseTimeLabel.getStylesheets().add(css);
        Grade grade = FileUtil.readFile();
        if (grade.getScore() == null || grade.getUseTime() == null) {
            bestScoreLabel.setText("得分：" + "暂无数据");
            bestUseTimeLabel.setText("使用时间：" + "暂无数据");
        } else {
            bestScoreLabel.setText("得分：" + grade.getScore());
            bestUseTimeLabel.setText("使用时间：" + grade.getUseTime());
        }

        operate.getChildren().addAll(scoreLabel, useTimeLabel, button, partingLine, bestGradeLabel, bestScoreLabel, bestUseTimeLabel);
    }

    private void initUseTimer() {
        // ... 现有的定时器初始化代码 ...
        useTimer = new Timer();

        // 记录游戏开始时间
        useTime = System.currentTimeMillis();

        // 添加一个任务来更新时间标签
        TimerTask updateTimeTask = new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = (currentTime - useTime) / 1000; // 转换为秒
                int seconds = (int) (elapsedTime % 60);
                int minutes = (int) (elapsedTime / 60);
                int hours = (int) (elapsedTime / 60 / 60);
                String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                Platform.runLater(() -> {
                    // 你的JavaFX代码
                    useTimeLabel.setText("使用时间：" + timeString);
                });
            }
        };

        // 使用另一个定时器来定期更新时间，例如每秒更新一次
        useTimer.scheduleAtFixedRate(updateTimeTask, 1000, 1000);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
