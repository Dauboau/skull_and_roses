package skull_and_roses;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Game game;
    public static GameController gameController;
    public static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("menu"), 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
        game.players.forEach(p -> System.out.println(p.name + " winned: " + p.nWins));
        System.exit(0);
    }

}

/*
Acknowledgements:
-p1S.png, Cp1s.png, p2S.png and Cp2s.png: "Designed by Ajipebriana / Freepik"
-p1F.png, Cp1f.png, p2F.png and Cp2f.png: "Designed by djvstock / Freepik"
*/