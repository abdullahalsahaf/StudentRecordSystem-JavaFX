package application;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main_view.fxml"));
        Scene scene = new Scene(loader.load());

        // attach CSS
        scene.getStylesheets().add(getClass().getResource("/view/app.css").toExternalForm());

        // get controller and save on close 
        final MainController controller = loader.getController();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override public void handle(WindowEvent event) {
                controller.onAppClose();
            }
        });

        primaryStage.setTitle("Student Record Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(640);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
