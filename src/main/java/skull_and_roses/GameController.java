package skull_and_roses;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

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
    private GridPane p1Grid;

    @FXML
    private Label p1;

    @FXML
    private GridPane p2Grid;

    @FXML
    private Label p2;

    @FXML
    private Label instructionsLabel;

    @FXML
    private void menu() throws IOException {
        App.setRoot("menu");
    }

    ExecutorService executor = Executors.newSingleThreadExecutor();
    /**
     * The initialize method is called when the game starts.
     */
    @FXML
    public void initialize() {
        executor.execute(App.game.start());
        App.gameController = this;

        App.scene.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            tick_on_typed(character);
        });

    }

    @FXML
    public void tick_on_click(){
        App.game.tick();
    }

    @FXML
    public void tick_on_typed(String character){
        App.game.tick_keyboard(character);
    }

    @FXML
    public void reset(){
        App.game.reset();
        executor.execute(App.game.start());
    }

    public void setPlayerLabels(String p1Name, String p2Name, String p1Type, String p2Type){
        Platform.runLater(() -> {
            p1.setText(p1Name + "(" + p1Type + ")");
            p2.setText(p2Name + "(" + p2Type + ")");
        });
    }

    public void updateLabels(int bid, String stage, String player, String action){
        Platform.runLater(() -> {
            bidLabel.setText("Bid: " + bid);
            stageLabel.setText("Stage: " + stage);
            playerLabel.setText("Player: " + player);
            actionLabel.setText(action);
        });
    }

    public void updateInstructions(String instructions){
        Platform.runLater(() -> {
            instructionsLabel.setText(instructions);
        });
    }

    public void addToken(ImageView token,int playerIndex, int tokenIndex){
        Platform.runLater(() -> {
            switch (playerIndex) {

                case 0:
                    p1Grid.add(token, tokenIndex, 0);
                    break;

                case 1:
                    p2Grid.add(token, tokenIndex, 0);
                    break;
            
                default:
                    break;

            }
        });
    }

    public void removeToken(ImageView token, int playerIndex){
        Platform.runLater(() -> {
            switch (playerIndex) {

                case 0:
                    p1Grid.getChildren().remove(token);
                    break;

                case 1:
                    p2Grid.getChildren().remove(token);
                    break;
            
                default:
                    break;

            }
        });
    }
}