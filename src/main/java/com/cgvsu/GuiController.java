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
import com.cgvsu.math.Vector3f;

import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {
    final private float TRANSLATION = 1.5F;

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

        // Добавляем обработчики событий мыши
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
    }

    private void handleMousePressed(MouseEvent event) {
        lastMouseX = event.getSceneX();
        lastMouseY = event.getSceneY();
        isMousePressed = true;
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isMousePressed) {
            double deltaX = event.getSceneX() - lastMouseX;
            double deltaY = event.getSceneY() - lastMouseY;

            // Изменяем направление камеры в зависимости от движения мыши
            camera.rotate((float) deltaY * 0.1F, (float) deltaX * 0.1F);

            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        isMousePressed = false;
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

    @FXML
    public void handleCameraForw(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v = Vector3f.subtraction(camera.getTarget(), camera.getPosition());
        v.normalize();
        camera.moveTarget(new Vector3f(v.x * TRANSLATION, v.y * TRANSLATION, v.z * TRANSLATION));
        camera.movePosition(new Vector3f(v.x * TRANSLATION, v.y * TRANSLATION, v.z * TRANSLATION));
    }

    @FXML
    public void handleCameraBack(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v = Vector3f.subtraction(camera.getTarget(), camera.getPosition());
        v.normalize();
        camera.moveTarget(new Vector3f(-v.x * TRANSLATION, -v.y * TRANSLATION, -v.z * TRANSLATION));
        camera.movePosition(new Vector3f(-v.x * TRANSLATION, -v.y * TRANSLATION, -v.z * TRANSLATION));
    }

    @FXML
    public void A(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v = Vector3f.subtraction(camera.getTarget(), camera.getPosition());
        v.normalize();
        Vector3f side = new Vector3f();
        side = Vector3f.crossProduct(v, new Vector3f(0, 1, 0));
        side.normalize();
        camera.moveTarget(new Vector3f(side.x * TRANSLATION/3, side.y * TRANSLATION/3, side.z * TRANSLATION/3));
        camera.movePosition(new Vector3f(side.x * TRANSLATION/3, side.y * TRANSLATION/3, side.z * TRANSLATION/3));
    }

    @FXML
    public void D(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v = Vector3f.subtraction(camera.getTarget(), camera.getPosition());
        v.normalize();
        Vector3f side = new Vector3f();
        side = Vector3f.crossProduct(v, new Vector3f(0, 1, 0));
        side.normalize();
        camera.moveTarget(new Vector3f(-side.x * TRANSLATION/3, -side.y * TRANSLATION/3, -side.z * TRANSLATION/3));
        camera.movePosition(new Vector3f(-side.x * TRANSLATION/3, -side.y * TRANSLATION/3, -side.z * TRANSLATION/3));
    }
}