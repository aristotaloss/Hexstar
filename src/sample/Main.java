package sample;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.Random;

public class Main extends Application {

	public static class DataRow {
		private StringProperty address;
		private StringProperty[] data = new StringProperty[16];

		public DataRow(int addr, byte[] b) {
			address =  new SimpleStringProperty(String.format("%08X", addr));
			for (int i=0; i<b.length; i++) {
				data[i] = new SimpleStringProperty(String.format("%02X", b[i]));
			}
		}

		public StringProperty addressProperty() {
			return address;
		}

		public StringProperty b0Property() {
			return data[0];
		}

		public StringProperty b1Property() {
			return data[1];
		}

		public StringProperty b2Property() {
			return data[2];
		}

		public StringProperty b3Property() {
			return data[3];
		}

		public StringProperty b4Property() {
			return data[4];
		}

		public StringProperty b5Property() {
			return data[5];
		}

		public StringProperty b6Property() {
			return data[6];
		}

		public StringProperty b7Property() {
			return data[7];
		}

		public StringProperty b8Property() {
			return data[8];
		}

		public StringProperty b9Property() {
			return data[9];
		}

		public StringProperty bAProperty() {
			return data[0xA];
		}

		public StringProperty bBProperty() {
			return data[0xB];
		}

		public StringProperty bCProperty() {
			return data[0xC];
		}

		public StringProperty bDProperty() {
			return data[0xD];
		}

		public StringProperty bEProperty() {
			return data[0xE];
		}

		public StringProperty bFProperty() {
			return data[0xF];
		}
	}

	@Override public void start(Stage stage) throws Exception {
		setUserAgentStylesheet(STYLESHEET_MODENA);
		Parent root = FXMLLoader.load(getClass().getResource("hexpanel.fxml"));
		stage.getIcons().add(new Image("/resources/icon-48.png"));
		stage.getIcons().add(new Image("/resources/icon-32.png"));
		stage.getIcons().add(new Image("/resources/icon-16.png"));

		TableView<DataRow> table = (TableView<DataRow>) root.lookup("#hextable");
		table.setContextMenu(new ContextMenu(new MenuItem("Copy"), new MenuItem("Cut"), new MenuItem("Delete")));
		table.getSelectionModel().setCellSelectionEnabled(true);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		ObservableListWrapper<DataRow> wrapper = (ObservableListWrapper<DataRow>) table.itemsProperty().get();

		FileInputStream fis = new FileInputStream("hexstar.iml");
		byte[] input = new byte[16];
		int num = 0;
		int addr = 0;
		while ((num = fis.read(input)) > 0) {
			wrapper.add(new DataRow(addr += 16, input));
		}

		table.getColumns().forEach(c -> {
			c.setStyle("-fx-alignment: CENTER;");
			c.setSortable(false);
		});
		table.getColumns().get(0).setStyle("-fx-alignment: CENTER-RIGHT;");

		stage.setTitle("Autism Simulator 0.2-SNAPSHOT");
		stage.setScene(new Scene(root, 570, 400));
		stage.show();
	}


	public static void main(String[] args) {
		launch(args);
	}
}
