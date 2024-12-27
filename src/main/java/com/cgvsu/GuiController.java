package com.cgvsu;

import com.cgvsu.ation.Rasterization;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cgvsu.ation.Rasterization.holst;

public class GuiController {
    final private float TRANSLATION = 1.5F;
    public ColorPicker colorPicker;
    public Slider brightnessSlider;
    public Button addNewModelButton;
    public ListView<Model> modelListView;
    public Button deleteModelButton;

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

    private List<Model> models = new ArrayList<>();
    private ObservableList<Model> modelObservableList;
    private Model activeModel;

    private Image textureImage;
    @FXML
    private ListView<Camera> cameraListView;
    @FXML
    private Button addNewCameraButton;

    @FXML
    private Button addNewLightButton;

    @FXML
    private Button deleteCameraButton;

    @FXML
    private Button transformModel;

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

    List<Parametrs> parametrs = new ArrayList<>();
    private List<Camera> cameras = new ArrayList<>(List.of(activeCamera));
    private ObservableList<Camera> cameraObservableList;
    private List<Lamp> lights = new ArrayList<>();

    private Timeline timeline;


    @FXML
    private void initialize() {
        loadDefaultModel();
        colorPicker.setValue(javafx.scene.paint.Color.RED);

        drawPolygonMeshCheckBox.setSelected(false);
        useTextureCheckBox.setSelected(false);
        useLightingCheckBox.setSelected(true);

        drawPolygonMeshCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            parametrs.get(models.indexOf(activeModel)).drawPolygonMash = isNowSelected;
            System.out.println("Рисовать полигональную сетку: " + isNowSelected);
        });

        useTextureCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (textureImage == null && isNowSelected) {
                useTextureCheckBox.setSelected(false);
                parametrs.get(models.indexOf(activeModel)).useTexture = false;
                System.out.println("Текстура не загружена, галочка снята.");
            } else {
                parametrs.get(models.indexOf(activeModel)).useTexture = isNowSelected;
                System.out.println("Использовать текстуру: " + isNowSelected);
            }
        });

        useLightingCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            parametrs.get(models.indexOf(activeModel)).useLighting = isNowSelected;
            System.out.println("Использовать освещение: " + isNowSelected);
        });

        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            for (int i = 0; i < models.size(); i++) {
                parametrs.get(i).brightnessLamp = newValue.floatValue();
            }
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

        if (activeCamera == null && !cameras.isEmpty()) {
            activeCamera = cameras.get(0);
        }
        cameraObservableList = FXCollections.observableArrayList(cameras);

        cameraListView.getSelectionModel().select(activeCamera);

        cameraListView.setItems(cameraObservableList);

        cameraListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                activeCamera = newValue;
            }
        });

        if (activeModel == null && !models.isEmpty()) {
            activeModel = models.get(0);
        }
        modelObservableList = FXCollections.observableArrayList(models);

        modelListView.getSelectionModel().select(activeModel);

        modelListView.setItems(modelObservableList);

        modelListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                activeModel = newValue;
            }
        });


        addNewCameraButton.setOnAction(event -> {
            addNewCamera();
        });

        addNewLightButton.setOnAction(event -> {
            addNewLight();
        });

        deleteCameraButton.setOnAction(event -> {
            Camera selectedCamera = cameraListView.getSelectionModel().getSelectedItem();
            if (selectedCamera != null) {
                cameras.remove(selectedCamera); // Удаляем из исходного списка
                cameraObservableList.remove(selectedCamera); // Удаляем из ObservableList

                if (!cameraObservableList.isEmpty()) {
                    activeCamera = cameraObservableList.get(0); // Установка первой камеры как активной
                    cameraListView.getSelectionModel().select(0);
                } else {
                    activeCamera = null; // Нет камер
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Удаление камеры");
                alert.setHeaderText(null);
                alert.setContentText("Нет выбранной камеры для удаления.");
                alert.showAndWait();
            }
        });
        deleteModelButton.setOnAction(event -> {
            Model selectModel = modelListView.getSelectionModel().getSelectedItem();
            if (selectModel != null) {
                parametrs.remove(models.indexOf(selectModel));
                models.remove(selectModel); // Удаляем из исходного списка
                modelObservableList.remove(selectModel); // Удаляем из ObservableList
                if (!modelObservableList.isEmpty()) {
                    activeModel = modelObservableList.get(0); // Установка первой камеры как активной
                    modelListView.getSelectionModel().select(0);
                } else {
                    activeModel = null; // Нет камер
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Удаление камеры");
                alert.setHeaderText(null);
                alert.setContentText("Нет выбранной камеры для удаления.");
                alert.showAndWait();
            }
        });


        transformModel.setOnAction(event -> {
            try {
                parametrs.get(models.indexOf(activeModel)).setRotationX(Float.parseFloat(rotationXField.getText()));
                parametrs.get(models.indexOf(activeModel)).setRotationY(Float.parseFloat(rotationYField.getText()));
                parametrs.get(models.indexOf(activeModel)).setRotationZ(Float.parseFloat(rotationZField.getText()));

                parametrs.get(models.indexOf(activeModel)).setScaleX(Float.parseFloat(scaleXField.getText()));
                parametrs.get(models.indexOf(activeModel)).setScaleY(Float.parseFloat(scaleYField.getText()));
                parametrs.get(models.indexOf(activeModel)).setScaleZ(Float.parseFloat(scaleZField.getText()));

                parametrs.get(models.indexOf(activeModel)).setTranslationX(Float.parseFloat(translationXField.getText()));
                parametrs.get(models.indexOf(activeModel)).setTranslationY(Float.parseFloat(translationYField.getText()));
                parametrs.get(models.indexOf(activeModel)).setTranslationZ(Float.parseFloat(translationZField.getText()));

                System.out.println("Параметры обновлены:");
                System.out.println("Вращение: X=" + parametrs.get(models.indexOf(activeModel)).getRotationX() + ", Y=" + parametrs.get(models.indexOf(activeModel)).getRotationY() + ", Z=" + parametrs.get(models.indexOf(activeModel)).getRotationZ());
                System.out.println("Масштабирование: X=" + parametrs.get(models.indexOf(activeModel)).getScaleX() + ", Y=" + parametrs.get(models.indexOf(activeModel)).getScaleY() + ", Z=" + parametrs.get(models.indexOf(activeModel)).getScaleZ());
                System.out.println("Перемещение: X=" + parametrs.get(models.indexOf(activeModel)).getTranslationX() + ", Y=" + parametrs.get(models.indexOf(activeModel)).getTranslationY() + ", Z=" + parametrs.get(models.indexOf(activeModel)).getTranslationZ());
            } catch (NumberFormatException e) {
                System.err.println("Ошибка: Введены некорректные данные. Убедитесь, что все поля содержат числа.");
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
            holst = new float[(int) canvas.getHeight()][(int) canvas.getWidth()];
            for (int i = 0; i < holst.length; i++) {
                Arrays.fill(holst[i], Float.MAX_VALUE);
            }
            for (Model mesh : models) {
                RenderEngine.render(canvas.getGraphicsContext2D(), activeCamera, mesh, (int) canvas.getWidth(), (int) canvas.getHeight(), parametrs.get(models.indexOf(mesh)), textureImage,
                        lights);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        // Добавляем обработчики событий мыши
        canvas.setOnMouseMoved(event -> activeCamera.rotateCam(event.getX(), event.getY(), false));
        canvas.setOnMouseDragged(event -> activeCamera.rotateCam(event.getX(), event.getY(), event.isPrimaryButtonDown()));
    }

    private void loadDefaultModel() {
        String filePath = "caracal_cube.obj";

        try {
            Path fileName = Path.of(filePath);
            String fileContent = Files.readString(fileName);
            Model model = ObjReader.read(fileContent);
            models.add(model); // Добавляем модель в список
            activeModel = model; // Устанавливаем activeModel
            parametrs.add(new Parametrs(
                    false,  // drawPolygonMash
                    false,  // useTexture
                    true,   // useLighting
                    0.01F,  // brightnessLamp
                    0.0F,   // rotationX
                    0.0F,   // rotationY
                    0.0F,   // rotationZ
                    1.0F,   // scaleX
                    1.0F,   // scaleY
                    1.0F,   // scaleZ
                    0.0F,   // translationX
                    0.0F,   // translationY
                    0.0F    // translationZ
            ));
            System.out.println("Model loaded successfully from: " + filePath);
        } catch (IOException exception) {
            System.err.println("Error loading model from path: " + filePath);
            exception.printStackTrace();
        }
    }

    private void addNewLight() {
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
        cameraObservableList.add(newCamera);
    }

    private void addModel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));

        Stage stage = (Stage) canvas.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            Model model = ObjReader.read(fileContent);
            models.add(model); // Добавляем модель в список
            activeModel = model; // Обновляем activeModel
            modelObservableList.add(model);

            // Добавляем параметры для новой модели
            Parametrs defaultParams = new Parametrs(
                    false,  // drawPolygonMash
                    false,  // useTexture
                    true,   // useLighting
                    0.01F,  // brightnessLamp
                    0.0F,   // rotationX
                    0.0F,   // rotationY
                    0.0F,   // rotationZ
                    1.0F,   // scaleX
                    1.0F,   // scaleY
                    1.0F,   // scaleZ
                    0.0F,   // translationX
                    0.0F,   // translationY
                    0.0F    // translationZ
            );
            parametrs.add(defaultParams);

            // Заполняем параметры на экране значениями по умолчанию
            rotationXField.setText("0.0");
            rotationYField.setText("0.0");
            rotationZField.setText("0.0");

            scaleXField.setText("1.0");
            scaleYField.setText("1.0");
            scaleZField.setText("1.0");

            translationXField.setText("0.0");
            translationYField.setText("0.0");
            translationZField.setText("0.0");

            xCoordinateLight.setText("0.0");
            yCoordinateLight.setText("0.0");
            zCoordinateLight.setText("0.0");

            dirXField.setText("0.0");
            dirYField.setText("0.0");
            dirZField.setText("0.0");

            drawPolygonMeshCheckBox.setSelected(defaultParams.drawPolygonMash);
            useTextureCheckBox.setSelected(defaultParams.useTexture);
            useLightingCheckBox.setSelected(defaultParams.useLighting);
            brightnessSlider.setValue(defaultParams.brightnessLamp);

            System.out.println("Model loaded successfully from: " + file);
        } catch (IOException exception) {
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

    private void applyDarkTheme() {
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
        activeCamera.movePosition(new Vector3f(side.x * TRANSLATION / 3, side.y * TRANSLATION / 3, side.z * TRANSLATION / 3));


    }

    @FXML
    public void D(ActionEvent actionEvent) {
        Vector3f v = new Vector3f();
        v = Vector3f.subtraction(activeCamera.getTarget(), activeCamera.getPosition());
        v.normalize();
        Vector3f side = new Vector3f();
        side = Vector3f.crossProduct(v, new Vector3f(0, 1, 0));
        side.normalize();
        activeCamera.movePosition(new Vector3f(-side.x * TRANSLATION / 3, -side.y * TRANSLATION / 3, -side.z * TRANSLATION / 3));
    }

    @FXML
    public void Q(ActionEvent actionEvent) {
        activeCamera = cameras.get(cameras.indexOf(activeCamera) + 1 >= cameras.size() ? 0 : cameras.indexOf(activeCamera) + 1);
        holst = new float[(int) canvas.getHeight()][(int) canvas.getWidth()];
        for (int i = 0; i < holst.length; i++) {
            Arrays.fill(holst[i], Float.MAX_VALUE);
        }
        for (Model mesh : models) {
            RenderEngine.render(canvas.getGraphicsContext2D(), activeCamera,
                    mesh, (int) canvas.getWidth(), (int) canvas.getHeight(),
                    parametrs.get(models.indexOf(mesh)),
                    textureImage,
                    lights);
        }
    }


}