package york.ca;

import com.fazecast.jSerialComm.SerialPort;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import york.ca.DataController;
import york.ca.SerialPortService;
import java.io.IOException;
import java.io.OutputStream;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;



public class Main extends Application {
    private final static int MAX_POTENTIOMETER_VALUE = 1 << 10;

    public static void main(String[] args) {
        launch(args);
    }

    private Node makeButtonRow(OutputStream outputStream) {
        var hbox = new HBox();
        hbox.setSpacing(10.0);

        var button = new Button("button");
        button.setOnMousePressed((mouseEvent) -> {

            try {
                outputStream.write(255);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        button.setOnMouseReleased((mouseEvent) -> {

            try {
                outputStream.write(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        hbox.getChildren().addAll(button);
        return hbox;
    }


    @Override
    public void start(Stage stage) {

        var sp = SerialPortService.getSerialPort("/dev/cu.usbserial-1410");
        var outputStream = sp.getOutputStream();
        var pane = new BorderPane();

        var button = new Button("A button");
        var slider = new Slider();
        slider.setMin(0.0);
        slider.setMax(100.0);
        var label = new Label();

        // Add a 'listener' to the {@code valueProperty} of the slider. The listener
        //  should write the {@code byteValue()} of the new slider value to the output stream.

        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            try {
                outputStream.write(newValue.byteValue());
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        pane.setCenter(slider);
        pane.setPadding(new Insets(0, 20, 0, 20));


        var scene = new Scene(pane, 400, 200);
        var rowA = makeButtonRow(outputStream);

        pane.getChildren().addAll(rowA);
        stage.setScene(scene);
        stage.show();




        var now = System.currentTimeMillis();
        var xAxis = new NumberAxis("time", now, now + 50000, 10000); // creates the x-axis (which automatically updates)
        var yAxis = new NumberAxis("value", 0, MAX_POTENTIOMETER_VALUE, 10); // creates the y-axis


        var valuecolumn = new TableColumn<XYChart.Data<Number, Number>, Number>("value");
        valuecolumn.setCellValueFactory(row -> row.getValue().YValueProperty());

       // var outputStream = sp.getOutputStream();
        var controller = new DataController(); // create the controller
        var series = new XYChart.Series<>(controller.getDataPoints()); // creates the series (all the data)
        var lineChart = new LineChart<>(xAxis, yAxis, FXCollections.singletonObservableList(series)); // creates the chart
        sp.addDataListener(controller);
        lineChart.setTitle("Potentiometer");

        // generating the graph (I comment these lines to get the slider and button) , uncomment to show the graph

   //    Scene scene1 = new Scene(lineChart,800,600); // creates the JavaFX window

       //   stage.setScene(scene1);
       //    stage.show();

    }}








