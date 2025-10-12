package RouteMapMaker;
	
import java.util.Optional;

import RouteMapMaker.controllers.UIController;
import RouteMapMaker.factories.AlertFactory;
import RouteMapMaker.factories.SceneFactory;
import RouteMapMaker.models.Configuration;
import RouteMapMaker.services.ErrorReporter;
import RouteMapMaker.services.FileSaveDialogService;
import RouteMapMaker.services.MainURManager;
import RouteMapMaker.factories.FileChooserFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	private final Configuration config = new Configuration();
	private final SceneFactory sceneFactory = new SceneFactory(config);
	private final AlertFactory alertFactory = new AlertFactory(config);
	private final FileChooserFactory fileChooserFactory = new FileChooserFactory(config);
	private final FileSaveDialogService fileSaveDialogService = new FileSaveDialogService();

	@Override
	public void start(Stage primaryStage) {
		try {
			config.read();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/RouteMapMaker/views/UIController.fxml"));
			loader.setControllerFactory(param -> {
				if (param == UIController.class) {
					return new UIController(config, sceneFactory, alertFactory, fileChooserFactory, fileSaveDialogService);
				} else {
					throw new RuntimeException();
				}
			});
			AnchorPane root = (AnchorPane)loader.load();
			UIController uic = loader.getController();
			uic.setObject(primaryStage);
			Scene scene = sceneFactory.createScene(root,800,500);
			primaryStage.setScene(scene);
			primaryStage.setTitle("路線図メーカー - main");
			//primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("ro.png")));
			primaryStage.setOnCloseRequest((WindowEvent t) ->{
				if(!MainURManager.urManager.isSaveNeeded()) {
					//保存は既に済んでいるので終了
					System.exit(0);
				}
				Alert alert = alertFactory.createAlert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("終了の確認");
				alert.setHeaderText(null);
				alert.setContentText("保存されていない変更があります．\n終了してよろしいですか？");
				Optional<ButtonType> result = alert.showAndWait();
				if(result.get() == ButtonType.OK){
					System.exit(0);
				}else{
					t.consume();
				}
			});
			//キャッチされない広域例外はここまで上がってくる
			Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
				e.printStackTrace();
				ErrorReporter.report(e);
			});
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
