package skull_and_roses;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class MenuController {

    @FXML
    private void switchToSecondary() throws IOException {
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
