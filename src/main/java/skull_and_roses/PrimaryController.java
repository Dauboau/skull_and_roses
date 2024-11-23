package skull_and_roses;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private ImageView imageView;

    @FXML
    private GridPane rootPane;

    @FXML
    public void initialize() {
        // Ajuste o tamanho do ImageView para 50% do tamanho do contêiner pai
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitWidth(newVal.doubleValue() * 0.5);
        });

        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitHeight(newVal.doubleValue() * 0.5);
        });
    }

}
