package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import javax.swing.*;
import javax.swing.text.html.CSS;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

public class Main extends Application {

	static final int SCENE_WIDTH = 450;
	static JSONParser parser = new JSONParser();
	static JSONObject mainJsonObject;
	static JSONArray monitoredSites;
	static Long intervalINT;
	static String CONFIG_LOCATION;
	static int i = 0;
	static ScrollPane sitesToMonitorScrollPane;
	static Button addSiteToMonitor;
	static Button changeMonitorScheduler;
	static Button twitterUploadButton;
	static Button slackUploadButton;
	static TextField addASite = new TextField();
	static Text sitesToMonitorTXT;
	static Scene mainScene;
	static GridPane mainGrid;
	static Label monitorScheduler;
	static Group root;
	static ChoiceBox selectInterval;
	static GridPane uploadPane;
	static TextField addKeywordTXT;
	static Button addKeywordBTN;
	static Button run;

	static ListView<String> itemsList = new ListView<String>();
	static Stage stage;

	public static void main(String[] args) {
		launch(args);
	}

	public void addSite(String siteLink) {
		JSONArray monitoredSites = new JSONArray();
		monitoredSites = (JSONArray) mainJsonObject.get("sites");
		monitoredSites.add(siteLink);
		mainJsonObject.put("sites", monitoredSites);

		try (FileWriter file = new FileWriter(CONFIG_LOCATION)) {

			file.write(mainJsonObject.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setInterval(Long interval) {

		mainJsonObject.put("interval", interval);

		try (FileWriter file = new FileWriter(CONFIG_LOCATION)) {

			file.write(mainJsonObject.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setTwitterActive(boolean isActive) {
		JSONObject twitter = (JSONObject) mainJsonObject.get("twitter");
		twitter.put("active", isActive);
		mainJsonObject.put("twitter", twitter);

		try (FileWriter file = new FileWriter(CONFIG_LOCATION)) {

			file.write(mainJsonObject.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSlackActive(boolean isActive) {
		JSONObject slack = (JSONObject) mainJsonObject.get("slackBot");
		slack.put("active", isActive);
		mainJsonObject.put("slackBot", slack);

		try (FileWriter file = new FileWriter(CONFIG_LOCATION)) {

			file.write(mainJsonObject.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addKeyword(String keyWord) {
		JSONArray keywords = new JSONArray();
		keywords = (JSONArray) mainJsonObject.get("keywords");
		keywords.add(keyWord);
		mainJsonObject.put("keywords", keywords);

		try (FileWriter file = new FileWriter(CONFIG_LOCATION)) {

			file.write(mainJsonObject.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initializeParser() {
		try {
			Object obj = parser.parse(new FileReader(CONFIG_LOCATION));

			mainJsonObject = (JSONObject) obj;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			File file = new File("newfile.txt");
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select config.json file");
			File file1 = fileChooser.showOpenDialog(stage);
			CONFIG_LOCATION = file1.getPath();
			Writer r = null;

			try {
				r = new BufferedWriter(new FileWriter(file));
				r.write(CONFIG_LOCATION);
				r.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			initializeParser();
			e.printStackTrace();

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		File file = new File("newfile.txt");
		stage = primaryStage;
		if (file.createNewFile()) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select config.json file");
			File file1 = fileChooser.showOpenDialog(primaryStage);
			CONFIG_LOCATION = file1.getPath();
			Writer r = new BufferedWriter(new FileWriter(file));
			r.write(CONFIG_LOCATION);
			r.flush();
		} else {
			System.out.println("File already exists.");
			BufferedReader r = new BufferedReader(new FileReader(file));
			CONFIG_LOCATION = r.readLine();
		}

		initializeParser();
		mainScene = new Scene(new Group(), 500, 500);

		mainGrid = new GridPane();
		mainGrid.setPadding(new Insets(10, 10, 10, 10));
		mainGrid.setHgap(10);
		mainGrid.setVgap(10);

		// ------------------------------------ List of monitored sites
		// ------------------------------------

		sitesToMonitorScrollPane = new ScrollPane();
		sitesToMonitorScrollPane.setFitToHeight(true);
		sitesToMonitorScrollPane.setVmax(440);
		sitesToMonitorScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		sitesToMonitorScrollPane.setPrefSize(115, 150);
		mainGrid.add(sitesToMonitorScrollPane, 0, 0);

		sitesToMonitorTXT = new Text("Sites to monitor: \n\n");

		monitoredSites = new JSONArray();
		monitoredSites = (JSONArray) mainJsonObject.get("sites");
		for (Object i : monitoredSites) {
			sitesToMonitorTXT.setText(sitesToMonitorTXT.getText() + "\n\n" + (String) i);
		}
		sitesToMonitorScrollPane.setContent(sitesToMonitorTXT);

		// ------------------------------------

		Separator separator = new Separator();
		separator.setHalignment(HPos.CENTER);
		mainGrid.add(separator, 0, 1);

		// ------------------------------------ Add a site
		// ------------------------------------

		addASite.setText("Enter A Site To Add");
		addASite.setOnMouseClicked(event -> {
			if (addASite.getText().equals("Enter A Site To Add"))
				addASite.setText("");
		});
		addASite.setPrefSize(SCENE_WIDTH - 100, 20);
		mainGrid.add(addASite, 0, 2);

		// ------------------------------------

		addSiteToMonitor = new Button("Add");
		addSiteToMonitor.setOnAction((ActionEvent e) -> {
			System.out.println("CLICKED");
			addSite(addASite.getText());
			for (Object i : monitoredSites) {
				sitesToMonitorTXT.setText(sitesToMonitorTXT.getText() + "\n\n" + (String) i);
			}
		});
		mainGrid.add(addSiteToMonitor, 1, 2);

		// ------------------------------------

		separator = new Separator();
		separator.setHalignment(HPos.CENTER);
		mainGrid.add(separator, 0, 3);

		// ------------------------------------ Choose the interval of searching
		// ------------------------------------

		monitorScheduler = new Label("Monitor Scheduler");
		mainGrid.add(monitorScheduler, 0, 4);

		// ------------------------------------
		intervalINT = (Long) mainJsonObject.get("interval");

		selectInterval = new ChoiceBox(FXCollections.observableArrayList("10 Seconds", "20 Seconds", "30 Seconds",
				"1 Minute", "2 Minutes", "5 Minutes", "10 Minutes", "30 Minutes"));
		selectInterval.setPrefSize(SCENE_WIDTH - 60, 20);
		System.out.println(intervalINT);

		if (intervalINT == (60000)) {
			selectInterval.getSelectionModel().select("1 Minute");

		} else if (intervalINT == (120000)) {
			selectInterval.getSelectionModel().select("2 Minutes");

		} else if (intervalINT == (300000)) {
			selectInterval.getSelectionModel().select("5 Minutes");

		} else if (intervalINT == (600000)) {
			selectInterval.getSelectionModel().select("10 Minutes");

		} else if (intervalINT == (1800000)) {
			selectInterval.getSelectionModel().select("30 Minutes");

		} else if (intervalINT == (10000)) {
			selectInterval.getSelectionModel().select("10 Seconds");
		} else if (intervalINT == (20000)) {
			selectInterval.getSelectionModel().select("20 Seconds");
		} else if (intervalINT == (30000)) {
			selectInterval.getSelectionModel().select("30 Seconds");
		}

		mainGrid.add(selectInterval, 0, 5);

		// ------------------------------------

		changeMonitorScheduler = new Button("Change");
		changeMonitorScheduler.setOnAction((ActionEvent e) -> {
			System.out.println("CLICKED");
			if (selectInterval.getSelectionModel().getSelectedItem().equals("1 Minute")) {
				setInterval((long) 10000);

			} else if (selectInterval.getSelectionModel().getSelectedItem().equals("2 Minutes")) {
				setInterval((long) 20000);

			} else if (selectInterval.getSelectionModel().getSelectedItem().equals("5 Minutes")) {
				setInterval((long) 50000);

			} else if (selectInterval.getSelectionModel().getSelectedItem().equals("10 Minutes")) {
				setInterval((long) 100000);

			} else if (selectInterval.getSelectionModel().getSelectedItem().equals("30 Minutes")) {
				setInterval((long) 300000);

			} else if (selectInterval.getSelectionModel().getSelectedItem().equals("10 Seconds")) {
				setInterval((long) 10000);

			} else if (selectInterval.getSelectionModel().getSelectedItem().equals("20 Seconds")) {
				setInterval((long) 20000);

			} else if (selectInterval.getSelectionModel().getSelectedItem().equals("30 Seconds")) {
				setInterval((long) 30000);

			}

		});
		mainGrid.add(changeMonitorScheduler, 1, 5);

		// ------------------------------------

		separator = new Separator();
		separator.setScaleY(1);
		separator.setHalignment(HPos.CENTER);
		mainGrid.add(separator, 0, 6);

		// ------------------------------------ Configure Twitter/Slack upload
		// ------------------------------------

		uploadPane = new GridPane();
		uploadPane.setVgap(10);
		uploadPane.setHgap(10);
		mainGrid.add(uploadPane, 0, 7);

		// ------------------------------------

		twitterUploadButton = new Button();
		if ((boolean) ((JSONObject) mainJsonObject.get("twitter")).get("active")) {
			twitterUploadButton.setText("Twitter Upload is On");
			twitterUploadButton.setStyle("-fx-background-color: #228B22; ");
		} else {
			twitterUploadButton.setText("Twitter Upload is Off");
			twitterUploadButton.setStyle("-fx-background-color: #DC143C; ");
		}
		twitterUploadButton.setOnAction((ActionEvent e) -> {
			if (twitterUploadButton.getText().equals("Twitter Upload is Off")) {
				twitterUploadButton.setText("Twitter Upload is On");
				setTwitterActive(true);
				twitterUploadButton.setStyle("-fx-background-color: #008000");
			} else {
				twitterUploadButton.setText("Twitter Upload is Off");
				setTwitterActive(false);
				twitterUploadButton.setStyle("-fx-background-color: #DC143C");
			}

		});
		uploadPane.add(twitterUploadButton, 0, 0);

		// ------------------------------------

		slackUploadButton = new Button();
		if ((boolean) ((JSONObject) mainJsonObject.get("slackBot")).get("active")) {
			slackUploadButton.setText("Slack Upload is On");

			slackUploadButton.setStyle("-fx-background-color: #228B22; ");
		} else {
			slackUploadButton.setText("Slack Upload is Off");

			slackUploadButton.setStyle("-fx-background-color: #DC143C; ");
		}
		slackUploadButton.setOnAction((ActionEvent e) -> {
			if (slackUploadButton.getText().equals("Slack Upload is Off")) {
				slackUploadButton.setText("Slack Upload is On");
				setSlackActive(true);
				slackUploadButton.setStyle("-fx-background-color: #008000");
			} else {
				slackUploadButton.setText("Slack Upload is Off");
				setSlackActive(false);
				slackUploadButton.setStyle("-fx-background-color: #DC143C");
			}

		});
		uploadPane.add(slackUploadButton, 1, 0);

		// ------------------------------------

		separator = new Separator();
		separator.setScaleY(1);
		separator.setHalignment(HPos.CENTER);
		mainGrid.add(separator, 0, 8);

		// ------------------------------------ Add a keyword
		// ------------------------------------

		addKeywordTXT = new TextField();
		addKeywordTXT.setText("Enter A Keyword To Add");
		addKeywordTXT.setOnMouseClicked(event -> {
			if (addKeywordTXT.getText().equals("Enter A Keyword To Add"))
				addKeywordTXT.setText("");
		});
		addKeywordTXT.setPrefSize(SCENE_WIDTH - 100, 20);
		mainGrid.add(addKeywordTXT, 0, 9);

		// ------------------------------------

		addKeywordBTN = new Button("Add");
		addKeywordBTN.setOnAction((ActionEvent e) -> {
			System.out.println("CLICKED");
			addKeyword(addKeywordTXT.getText());
			addKeywordTXT.setText("");
		});
		mainGrid.add(addKeywordBTN, 1, 9);

		// ------------------------------------

		separator = new Separator();
		separator.setScaleY(1);
		separator.setHalignment(HPos.CENTER);
		mainGrid.add(separator, 0, 10);

		// ------------------------------------ Search for sites
		// ------------------------------------

		run = new Button("Run");
		mainGrid.add(run, 0, 11);

		Label output = new Label("");
		mainGrid.add(output, 0, 12);

		run.setOnAction(((ActionEvent) -> {

			System.out.print("Executed Command");
			try {
				String b = null;
				try {
					b = executeRunCommand();
				} catch (InvalidExitValueException | InterruptedException | TimeoutException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				output.setText(b);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}));

		root = (Group) mainScene.getRoot();
		root.getChildren().add(mainGrid);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	private String executeRunCommand()
			throws IOException, InvalidExitValueException, InterruptedException, TimeoutException {
		ProcessExecutor t = new ProcessExecutor();
		File y = new File("/Users/Arseniy/shopify-monitor");
		t.directory(y);
		System.out.println(y.isDirectory());

		t.command("npm", "start").redirectOutput(System.out).execute();
		return "hi";

	}

}
