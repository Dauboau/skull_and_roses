package skull_and_roses;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class MenuController {

    @FXML
    private void zero_vs_zero() throws IOException {
        App.game = new Game(Game.Type.ZERO_vs_ZERO);
        App.setRoot("game");
    }

    @FXML
    private void one_vs_zero() throws IOException {
        App.game = new Game(Game.Type.ONE_vs_ZERO);
        App.setRoot("game");
    }

    @FXML
    private void player_vs_zero() throws IOException {
        App.game = new Game(Game.Type.PLAYER_vs_ZERO);
        App.setRoot("game");
    }

    @FXML
    private void player_vs_one() throws IOException {
        App.game = new Game(Game.Type.PLAYER_vs_ONE);
        App.setRoot("game");
    }

    @FXML
    private ImageView imageView;

    @FXML
    private GridPane rootPane;

    @FXML
    public void initialize() {
        
        // Dynamic Size 
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitWidth(newVal.doubleValue() * 0.5);
        });
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitHeight(newVal.doubleValue() * 0.5);
        });

    }

}
