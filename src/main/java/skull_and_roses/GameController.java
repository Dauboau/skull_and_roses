package skull_and_roses;

import java.io.IOException;
import javafx.fxml.FXML;

public class GameController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("menu");
    }
}