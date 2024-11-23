package com.cgvsu;

import com.cgvsu.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import javax.vecmath.Vector3f;

import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;



public class GuiController {

    final private float TRANSLATION = 1F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;


    private Model mesh = null;

    private Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    private double lastMouseX;
    private double lastMouseY;
    private boolean isMousePressed = false;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        addMouseHandlers();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }
    private void addMouseHandlers() {
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
                isMousePressed = true;
            }
        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                isMousePressed = false;
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (isMousePressed) {
                    double deltaX = event.getSceneX() - lastMouseX;
                    double deltaY = event.getSceneY() - lastMouseY;

                    updateCameraDirection(deltaX, deltaY);

                    lastMouseX = event.getSceneX();
                    lastMouseY = event.getSceneY();
                }
            }
        });
    }

    private void updateCameraDirection(double deltaX, double deltaY) {
        // Вычисление вектора направления камеры
        Vector3f direction = new Vector3f();
        direction.sub(camera.getTarget(), camera.getPosition());
        direction.normalize();

        // Вычисление вектора, перпендикулярного направлению камеры и вертикальной оси
        Vector3f side = new Vector3f();
        side.cross(direction, new Vector3f(0, 1, 0));
        side.normalize();

        // Вычисление вектора, перпендикулярного направлению камеры и вектору side
        Vector3f up = new Vector3f();
        up.cross(side, direction);
        up.normalize();

        // Обновление направления камеры на основе движения мыши
        float yaw = (float) deltaX * 0.001f; // Угол поворота вокруг оси Y
        float pitch = (float) deltaY * 0.001f; // Угол поворота вокруг оси X

        // Поворот направления камеры вокруг оси Y
        Vector3f newDirection = new Vector3f();
        newDirection.x = (float) (direction.x * Math.cos(yaw) + side.x * Math.sin(yaw));
        newDirection.y = (float) (direction.y * Math.cos(yaw) + side.y * Math.sin(yaw));
        newDirection.z = (float) (direction.z * Math.cos(yaw) + side.z * Math.sin(yaw));

        // Поворот направления камеры вокруг оси X
        newDirection.x = (float) (newDirection.x * Math.cos(pitch) + up.x * Math.sin(pitch));
        newDirection.y = (float) (newDirection.y * Math.cos(pitch) + up.y * Math.sin(pitch));
        newDirection.z = (float) (newDirection.z * Math.cos(pitch) + up.z * Math.sin(pitch));

        // Обновление цели камеры
        camera.setTarget(
                new Vector3f(camera.getPosition().x + newDirection.x,
                        camera.getPosition().y + newDirection.y,
                        camera.getPosition().z + newDirection.z));
    }


    @FXML
    public void handleCameraForw(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v.sub(camera.getTarget(), camera.getPosition());
        v.normalize();
        camera.moveTarget(new Vector3f(v.x * TRANSLATION, v.y * TRANSLATION, v.z * TRANSLATION));
        camera.movePosition(new Vector3f(v.x * TRANSLATION, v.y * TRANSLATION, v.z * TRANSLATION));
    }

    @FXML
    public void handleCameraBack(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v.sub(camera.getTarget(), camera.getPosition());
        v.normalize();
        camera.moveTarget(new Vector3f(-v.x * TRANSLATION, -v.y * TRANSLATION, -v.z * TRANSLATION));
        camera.movePosition(new Vector3f(-v.x * TRANSLATION, -v.y * TRANSLATION, -v.z * TRANSLATION));
    }

    @FXML
    public void A(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v.sub(camera.getTarget(), camera.getPosition());
        v.normalize();
        Vector3f side = new Vector3f();
        side.cross(v, new Vector3f(0, 1, 0));
        side.normalize();
        camera.moveTarget(new Vector3f(side.x * TRANSLATION, side.y * TRANSLATION, side.z * TRANSLATION));
        camera.movePosition(new Vector3f(side.x * TRANSLATION, side.y * TRANSLATION, side.z * TRANSLATION));
    }

    @FXML
    public void D(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v.sub(camera.getTarget(), camera.getPosition());
        v.normalize();
        Vector3f side = new Vector3f();
        side.cross(v, new Vector3f(0, 1, 0));
        side.normalize();
        camera.moveTarget(new Vector3f(-side.x * TRANSLATION, -side.y * TRANSLATION, -side.z * TRANSLATION));
        camera.movePosition(new Vector3f(-side.x * TRANSLATION, -side.y * TRANSLATION, -side.z * TRANSLATION));
    }
}