package skull_and_roses;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameController {

    @FXML
    private Label stageLabel;

    @FXML
    private Label bidLabel;

    @FXML
    private Label playerLabel;

    @FXML
    private Label actionLabel;

    @FXML
    private void menu() throws IOException {
        App.setRoot("menu");
    }

    /**
     * The initialize method is called when the game starts.
     */
    @FXML
    public void initialize() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(App.game.start());
        App.gameController = this;

    }

    @FXML
    public void tick_on_click(){
        System.out.println("Tick");
        App.game.tick();
    }

    public void updateLabels(int bid, int stage, String player, String action){
        Platform.runLater(() -> {
            bidLabel.setText("Bid: " + bid);
            stageLabel.setText("Stage: " + stage);
            playerLabel.setText("Player: " + player);
            actionLabel.setText(action);
        });
    }
    
}