package com.cgvsu;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.Lamp;
import com.cgvsu.render_engine.Parametrs;
import com.cgvsu.render_engine.RenderEngine;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    public Button addNewModelButton;
    public TextField xCoordinateModel;
    public TextField yCoordinateModel;
    public TextField zCoordinateModel;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;
    @FXML
    private CheckBox drawPolygonMeshCheckBox;
    @FXML
    private ToggleButton themeToggle;

    @FXML
    private CheckBox useTextureCheckBox;

    @FXML
    private CheckBox useLightingCheckBox;

    private List<Model> models = new ArrayList<>();;

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

    @FXML
    private TextField rotationXField;
    @FXML
    private TextField rotationYField;
    @FXML
    private TextField rotationZField;
    @FXML
    private TextField scaleXField;
    @FXML
    private TextField scaleYField;
    @FXML
    private TextField scaleZField;
    @FXML
    private TextField translationXField;
    @FXML
    private TextField translationYField;
    @FXML
    private TextField translationZField;

    Camera activeCamera = new Camera(
            new Vector3f(0, 0, 120),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    Parametrs parametrs = new Parametrs(false, false, true, 0.01F);
    private List<Camera> cameras = new ArrayList<>(List.of(activeCamera));
    private List<Lamp> lights = new ArrayList<>();

    private Timeline timeline;


    @FXML
    private void initialize() {
        colorPicker.setValue(javafx.scene.paint.Color.RED);

        drawPolygonMeshCheckBox.setSelected(false);
        useTextureCheckBox.setSelected(false);
        useLightingCheckBox.setSelected(true);

        drawPolygonMeshCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            parametrs.drawPolygonMash = isNowSelected;
            System.out.println("Рисовать полигональную сетку: " + isNowSelected);
        });

        useTextureCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (textureImage == null && isNowSelected) {
                useTextureCheckBox.setSelected(false);
                parametrs.useTexture = false;
                System.out.println("Текстура не загружена, галочка снята.");
            } else {
                parametrs.useTexture = isNowSelected;
                System.out.println("Использовать текстуру: " + isNowSelected);
            }
        });

        useLightingCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            parametrs.useLighting = isNowSelected;
            System.out.println("Использовать освещение: " + isNowSelected);
        });

        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            parametrs.brightnessLamp = newValue.floatValue();
        });

        themeToggle.setOnAction(event -> {
            if (themeToggle.isSelected()) {
                themeToggle.setText("Светлая тема");
                applyDarkTheme();
            } else {
                themeToggle.setText("Тёмная тема");
                applyLightTheme();
            }
        });
        addNewModelButton.setOnAction(event -> {
            addModel();
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

            for (Model mesh:models){
                RenderEngine.render(canvas.getGraphicsContext2D(), activeCamera,
                        mesh, (int) width, (int) height,
                        parametrs,
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
            models.add(ObjReader.read(fileContent));
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

    private void addModel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));

        // Предполагаем, что у вас есть доступ к текущему окну (Stage)
        Stage stage = (Stage) canvas.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return; // Пользователь закрыл диалог без выбора файла
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            models.add(ObjReader.read(fileContent));
            System.out.println("Model loaded successfully from: " + file);
            // todo: обработка ошибок
        } catch (IOException exception) {
            // Обработка исключения, например, вывод сообщения об ошибке
            exception.printStackTrace();
        }
    }

    private void deleteCamera() {
        cameras.remove(activeCamera);
        Q(new ActionEvent());
    }
    @FXML
    private void applyLightTheme() {
        anchorPane.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;"
        );

        themeToggle.setStyle(
                "-fx-background-color: #E0E0E0;" +
                        "-fx-text-fill: black;"
        );
        drawContent();

    }
    private void drawContent() {
        javafx.scene.paint.Color backgroundColor = themeToggle.isSelected() ? javafx.scene.paint.Color.web("#2B2B2B") : javafx.scene.paint.Color.WHITE;
        javafx.scene.paint.Color drawColor = themeToggle.isSelected() ? javafx.scene.paint.Color.WHITE : Color.BLACK;

        var gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(drawColor);
        gc.setLineWidth(1);
        for (int i = 0; i < canvas.getWidth(); i += 50) {
            gc.strokeLine(i, 0, i, canvas.getHeight());
        }
        for (int j = 0; j < canvas.getHeight(); j += 50) {
            gc.strokeLine(0, j, canvas.getWidth(), j);
        }
    }
    private void applyDarkTheme(){
        anchorPane.setStyle(
                "-fx-background-color: #2B2B2B;" +
                        "-fx-text-fill: white;"
        );

        themeToggle.setStyle(
                "-fx-background-color: #3C3F41;" +
                        "-fx-text-fill: white;"
        );
        drawContent();

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
            models.add(ObjReader.read(fileContent));
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

        for (Model mesh:models){
            RenderEngine.render(canvas.getGraphicsContext2D(), activeCamera,
                    mesh, (int) canvas.getWidth(), (int) canvas.getHeight(),
                    parametrs,
                    textureImage,
                    lights);
        }
    }

}