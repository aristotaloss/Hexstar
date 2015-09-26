package com.bartpelle.hexstar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LongStringConverter;
import org.dockfx.DockNode;
import org.dockfx.DockPane;
import org.dockfx.DockPos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

@SuppressWarnings("all")
public class Hexstar extends Application {

	private void configureHexView(Parent root) throws IOException {
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
	}

	public void start(Stage stage) throws IOException {
		stage.setTitle("Hexstar: The Extendable & Powerful Hex Editor | 0.1-SNAPSHOT");

		stage.getIcons().add(new Image(Hexstar.class.getResourceAsStream("/icons/icon-48.png")));
		stage.getIcons().add(new Image(Hexstar.class.getResourceAsStream("/icons/icon-32.png")));
		stage.getIcons().add(new Image(Hexstar.class.getResourceAsStream("/icons/icon-16.png")));

		DockPane dockPane = new DockPane();
		TabPane tabs = new TabPane();

		// Load table view data
		TableView tableView = FXMLLoader.load(Hexstar.class.getResource("/layouts/hexview.fxml"));
		configureHexView(tableView);
		tabs.getTabs().addAll(new Tab("hexstar.iml", tableView), new Tab(".gitignore"), new Tab("example.bin"));

		// Load icons
		Image dockImage = new Image("/icons/dock/docknode.png");
		Image hexEditImage = new Image("/icons/table_edit.png");
		Image folderImage = new Image("/icons/folder.png");

		tabs.getTabs().forEach(tab -> {
			tab.setContextMenu(new ContextMenu(new MenuItem("Close") {
				{
					setOnAction(e -> {
						DockNode tabsDock = new DockNode(tableView, "Hex editor", new ImageView(hexEditImage));
						tabsDock.setPrefSize(600, 100);
						tabsDock.dock(dockPane, DockPos.TOP);
						tabsDock.undock();
						tabsDock.setFloating(true);
						tabsDock.resize(600, 600);
						tableView.resize(600, 600);
					});
				}
			}));
		});

		TableView<String> dataView = new TableView<>();
		dataView.setEditable(true);
		dataView.getColumns().addAll(new TableColumn<String, ObservableValue<String>>("Data type"), new TableColumn<String, ObservableValue<String>>("Value"));
		((TableColumn<String, Number>) dataView.getColumns().get(1)).setCellValueFactory((cap) -> {
			return new SimpleLongProperty(0);
		});
		((TableColumn<String, Long>) dataView.getColumns().get(1)).setCellFactory(param -> new TextFieldTableCell<>(new LongStringConverter()));



		DockNode tabsDock = new DockNode(tabs, "Hex editor", new ImageView(hexEditImage));
		tabsDock.setDockTitleBar(null);
		tabsDock.setPrefSize(600, 100);
		tabsDock.dock(dockPane, DockPos.TOP);

		DockNode tableDock = new DockNode(dataView, "Data explorer");
		tableDock.setPrefSize(300, 100);
		tableDock.dock(dockPane, DockPos.RIGHT);

		stage.setScene(new Scene(dockPane, 1080, 720));
		stage.sizeToScene();
		stage.show();

		DockNode treeDock = new DockNode(generateFileTree(new File("./")), "File browser", new ImageView(folderImage));
		treeDock.setPrefSize(100, 100);
		treeDock.dock(dockPane, DockPos.LEFT);

		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
		DockPane.initializeDefaultUserAgentStylesheet();
	}

	private TreeView<String> generateFileTree(File folder) {
		folder = folder.getAbsoluteFile();

		TreeItem<String> root = new TreeItem<>(folder.getName());
		TreeView<String> tree = new TreeView<>(root);
		tree.setShowRoot(true);

		// Recursively add the components
		populate(root, folder);

		return tree;
	}

	private void populate(TreeItem<String> tree, File folder) {
		// First the directories..
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				TreeItem<String> folderItem = new TreeItem<>(f.getName(), new ImageView("/icons/folder.png"));
				tree.getChildren().add(folderItem);
				populate(folderItem, f);
			}
		}

		// .. then the files
		for (File f : folder.listFiles()) {
			if (f.isFile()) {
				TreeItem<String> fileItem = new TreeItem<>(f.getName(), new ImageView("/icons/page_white.png"));
				tree.getChildren().add(fileItem);
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
