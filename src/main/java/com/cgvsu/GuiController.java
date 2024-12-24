package com.cgvsu;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.Lamp;
import com.cgvsu.render_engine.RenderEngine;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GuiController {
    final private float TRANSLATION = 1.5F;
    public ColorPicker colorPicker;
    public Slider brightnessSlider;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;
    @FXML
    private CheckBox drawPolygonMeshCheckBox;

    @FXML
    private CheckBox useTextureCheckBox;

    @FXML
    private CheckBox useLightingCheckBox;

    private Model mesh = null;

    private Image textureImage;

    @FXML
    private Button addNewCameraButton;

    @FXML
    private Button addNewLightButton;

    @FXML
    private Button deleteCameraButton;

    @FXML
    private TextField xCoordinateField;

    @FXML
    private TextField yCoordinateField;

    @FXML
    private TextField zCoordinateField;

    @FXML
    private TextField xCoordinateLight;

    @FXML
    private TextField yCoordinateLight;

    @FXML
    private TextField zCoordinateLight;

    @FXML
    private TextField dirXField;

    @FXML
    private TextField dirYField;

    @FXML
    private TextField dirZField;

    Camera activeCamera = new Camera(
            new Vector3f(0, 0, 75),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);
    private List<Camera> cameras = new ArrayList<>(List.of(activeCamera));
    private List<Lamp> lights = new ArrayList<>();

    private Timeline timeline;


    @FXML
    private void initialize() {
        drawPolygonMeshCheckBox.setSelected(false);
        useTextureCheckBox.setSelected(false);
        useLightingCheckBox.setSelected(true);

        drawPolygonMeshCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            System.out.println("Рисовать полигональную сетку: " + isNowSelected);
        });

        useTextureCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (textureImage == null && isNowSelected) {
                useTextureCheckBox.setSelected(false);
                System.out.println("Текстура не загружена, галочка снята.");
            } else {
                System.out.println("Использовать текстуру: " + isNowSelected);
            }
        });

        useLightingCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            System.out.println("Использовать освещение: " + isNowSelected);
        });


        addNewCameraButton.setOnAction(event -> {
            addNewCamera();
        });

        addNewLightButton.setOnAction(event -> {
            addNewLight();
        });

        deleteCameraButton.setOnAction(event -> {
            if (cameras.size()>1) {
                deleteCamera();
            }
        });

        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            activeCamera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), activeCamera,
                        mesh, (int) width, (int) height,
                        drawPolygonMeshCheckBox.isSelected(),
                        useTextureCheckBox.isSelected(),
                        useLightingCheckBox.isSelected(),
                        textureImage,
                        lights);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        // Добавляем обработчики событий мыши
        canvas.setOnMouseMoved(event -> activeCamera.rotateCam(event.getX(), event.getY(), false));
        canvas.setOnMouseDragged(event -> activeCamera.rotateCam(event.getX(), event.getY(), event.isPrimaryButtonDown()));

        loadDefaultModel();
    }

    private void loadDefaultModel() {
        String filePath = "caracal_cube.obj";

        try {
            Path fileName = Path.of(filePath);
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            System.out.println("Model loaded successfully from: " + filePath);
        } catch (IOException exception) {
            System.err.println("Error loading model from path: " + filePath);
            exception.printStackTrace();
        }
    }

    private void addNewLight(){
        Lamp newLight = new Lamp(new Vector3f(Float.parseFloat(xCoordinateLight.getText()),
                Float.parseFloat(yCoordinateLight.getText()),
                Float.parseFloat(zCoordinateLight.getText())), Lamp.convert(colorPicker.getValue()));
        lights.add(newLight);
    }
    private void addNewCamera() {
        Camera newCamera = new Camera(
                new Vector3f(Float.parseFloat(xCoordinateField.getText()),
                        Float.parseFloat(yCoordinateField.getText()),
                        Float.parseFloat(zCoordinateField.getText())),
                new Vector3f(Float.parseFloat(dirXField.getText()),
                        Float.parseFloat(dirYField.getText()),
                        Float.parseFloat(dirZField.getText())),
                1.0F,
                1,
                0.01F,
                100
        );
        activeCamera = newCamera;
        cameras.add(activeCamera);
    }

    private void deleteCamera() {
        cameras.remove(activeCamera);
        Q(new ActionEvent());
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
    private void onLoadTextureMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        fileChooser.setTitle("Load Texture");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file != null) {
            textureImage = new Image(file.toURI().toString());
            System.out.println("Текстура загружена: " + file.getName());
        }
    }

    @FXML
    public void handleCameraForw(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v = Vector3f.subtraction(activeCamera.getTarget(), activeCamera.getPosition());
        v.normalize();
        activeCamera.movePosition(new Vector3f(v.x * TRANSLATION, v.y * TRANSLATION, v.z * TRANSLATION));
    }

    @FXML
    public void handleCameraBack(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v = Vector3f.subtraction(activeCamera.getTarget(), activeCamera.getPosition());
        v.normalize();
        activeCamera.movePosition(new Vector3f(-v.x * TRANSLATION, -v.y * TRANSLATION, -v.z * TRANSLATION));
    }

    @FXML
    public void A(ActionEvent actionEvent) {

        float length = Vector3f.lenghtTwoVectors(activeCamera.getTarget(), activeCamera.getPosition());
        Vector3f v = new Vector3f();
        v = Vector3f.subtraction(activeCamera.getTarget(), activeCamera.getPosition());
        v.normalize();
        Vector3f side = new Vector3f();
        side = Vector3f.crossProduct(v, new Vector3f(0, 1, 0));
        side.normalize();
        activeCamera.movePosition(new Vector3f(side.x * TRANSLATION/3, side.y * TRANSLATION/3, side.z * TRANSLATION/3));


    }

    @FXML
    public void D(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v = Vector3f.subtraction(activeCamera.getTarget(), activeCamera.getPosition());
        v.normalize();
        Vector3f side = new Vector3f();
        side = Vector3f.crossProduct(v, new Vector3f(0, 1, 0));
        side.normalize();
        activeCamera.movePosition(new Vector3f(-side.x * TRANSLATION/3, -side.y * TRANSLATION/3, -side.z * TRANSLATION/3));
    }

    @FXML
    public void Q(ActionEvent actionEvent) {
        activeCamera = cameras.get(cameras.indexOf(activeCamera)+1 >= cameras.size() ? 0 : cameras.indexOf(activeCamera)+1);

        RenderEngine.render(canvas.getGraphicsContext2D(), activeCamera,
                mesh, (int) canvas.getWidth(), (int) canvas.getHeight(),
                drawPolygonMeshCheckBox.isSelected(),
                useTextureCheckBox.isSelected(),
                useLightingCheckBox.isSelected(),
                textureImage,
                lights);
    }

}