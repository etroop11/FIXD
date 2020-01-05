package app;

//Javafx Imports
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class ErrorDisplay extends InfoDisplay{

	public ErrorDisplay(){
		super("", 0.0);
	}

	public Pane getDisplay(){
		VBox vb = new VBox();
		vb.getChildren().add(new Label("Error Retreiveing Data, Restart to try again."));	
		return vb;
	}

}