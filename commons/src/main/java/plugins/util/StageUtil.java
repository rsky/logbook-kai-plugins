package plugins.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import logbook.bean.AppConfig;
import logbook.bean.WindowLocation;
import logbook.internal.gui.WindowController;
import logbook.plugin.PluginContainer;

public class StageUtil {
    public static void show(String title, String fxmlPath, Window owner, ClassLoader classLoader) throws Exception {
        var loader = new FXMLLoader(PluginContainer.getInstance().getClassLoader().getResource(fxmlPath));
        loader.setClassLoader(classLoader);

        var stage = new Stage();
        var root = loader.<Parent>load();
        stage.setScene(new Scene(root));

        var controller = loader.<WindowController>getController();
        controller.setWindow(stage);

        stage.initOwner(owner);
        stage.setTitle(title);
        stage.setOnCloseRequest(event -> {
            if (!event.isConsumed()) {
                AppConfig.get()
                        .getWindowLocationMap()
                        .put(controller.getClass().getCanonicalName(), controller.getWindowLocation());
            }
        });

        var location = AppConfig.get()
                .getWindowLocationMap()
                .get(controller.getClass().getCanonicalName());
        if (location != null) {
            controller.setWindowLocation(location);
        }

        stage.show();
    }
}
