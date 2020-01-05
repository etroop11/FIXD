package app;

//Javafx imports
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.control.TableView;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;


public class Style{
	public static final Color FONT_COLOR = Color.web("0x213348"),
	COLOR1 = Color.web("0x0085C2"), 
	COLOR2 = Color.web("0xDE8A8D"),
	COLOR3 = Color.web("0xB1DCF5"),
	COLOR4 = Color.web("0x815AD5"),
	COLOR5 = Color.web("0x9593E6"); 
	

	
	public static void setDefaultStyle(Stage s){
		s.setMinWidth(850);
		s.setMaxWidth(1000);
		s.setMinHeight(400);
		//s.sizeToScene();

	}

	public static void setDefaultStyle(Scene s){
		
	}

	public static void setDefaultStyle(BorderPane bp){
		/*bp.setBorder(new Border(new BorderStroke(
			COLOR2,
			BorderStrokeStyle.SOLID,
			new CornerRadii(0),
			BorderStroke.MEDIUM)));
		*/
	}


	public static void setDefaultStyle(Pane p){
		
	}

	public static void setTopPaneStyle(HBox hb){
		/*hb.setBorder(new Border(new BorderStroke(
			COLOR2,
			BorderStrokeStyle.SOLID,
			new CornerRadii(0),
			BorderStroke.MEDIUM)));
		*/
		hb.setAlignment(Pos.CENTER_LEFT);
		hb.setBackground(new Background(
			new BackgroundFill(
				COLOR1,
				new CornerRadii(0),
				new Insets(0,0,0,0))));
		hb.setPadding(new Insets(4));

	}

	public static void setTopPaneStyle(Label l){
		l.setFont(new Font("PT Mono Bold", 16));
		l.setTextFill(FONT_COLOR);
	}

	public static void setTopPaneStyle(TextField tf){
		tf.setMinWidth(50);
		tf.setMaxWidth(75);
		tf.setFont(new Font("PT Mono Bold", 16));
		tf.setStyle("-fx-text-inner-color:#213348");
		tf.setPadding(new Insets(2));
		tf.setAlignment(Pos.CENTER);
		tf.setBorder(new Border(
			new BorderStroke(
				FONT_COLOR,
				BorderStrokeStyle.SOLID,
				new CornerRadii(5),
				BorderStroke.DEFAULT_WIDTHS)));
		tf.setBackground(new Background(
			new BackgroundFill(
				COLOR3,
				new CornerRadii(5),
				new Insets(0,0,0,0))));
		tf.setPrefHeight(35);
	}

	public static void setTopPaneStyle(ComboBox c){
		c.setStyle("-fx-font: 16px \"PT Mono Bold\"");
		c.setPadding(new Insets(2));
		c.setBorder(new Border(
			new BorderStroke(
				FONT_COLOR,
				BorderStrokeStyle.SOLID,
				new CornerRadii(5),
				BorderStroke.DEFAULT_WIDTHS)));
		c.setBackground(new Background(new BackgroundFill(
				COLOR3,
				new CornerRadii(5),
				new Insets(0,0,0,0))));
		c.setPrefHeight(35);
	}

	public static void setTopPaneStyle(Button b){
		b.setFont(new Font("PT Mono Bold", 16));
		b.setPadding(new Insets(2));
		b.setTextFill(COLOR1);
		b.setBorder(new Border(
			new BorderStroke(
				FONT_COLOR,
				BorderStrokeStyle.SOLID,
				new CornerRadii(5),
				BorderStroke.DEFAULT_WIDTHS)));
		b.setBackground(new Background(new BackgroundFill(
				FONT_COLOR,
				new CornerRadii(5),
				new Insets(0,0,0,0))));
		b.setPrefHeight(35);
	}

	public static void setSidePaneStyle(Label l){
		l.setFont(new Font("PT Mono Bold", 14));
		l.setTextFill(FONT_COLOR);
		l.setAlignment(Pos.CENTER);
	}

	public static void setMidPaneStyle(VBox p){
		p.setBackground(new Background(new BackgroundFill(
				COLOR3,
				new CornerRadii(0),
				new Insets(0,0,0,0))));
	}

	public static void setMidPaneStyle(Label l){
		l.setFont(new Font("PT Mono Bold", 28));
		l.setTextFill(FONT_COLOR);
		l.setPadding(new Insets(2, 10, 2, 10));
	}

	public static void setMidPaneStyle(Label l, int id){
		if(id == 1){
			l.setFont(new Font("PT Mono Bold", 28));
			l.setTextFill(FONT_COLOR);
			l.setPadding(new Insets(2, 10, 2, 10));
		} else if (id == 2){
			l.setFont(new Font("PT Mono Bold", 28));
			l.setTextFill(FONT_COLOR);
			l.setPadding(new Insets(2, 10, 2, 10));
		}
	}


	public static void setDefaultStyle(TextField tf){
		//tf.setMinWidth(50);
		//tf.setMaxWidth(50);
	}

	public static void setDefaultStyle(Node n){

	}



	public static void setDefaultStyle(TableView t){
		t.setStyle("-fx-font: 13px \"PT Mono\"");
		t.setBackground(new Background(new BackgroundFill(
				COLOR3,
				new CornerRadii(5),
				new Insets(0,0,0,0))));
		t.setPrefHeight(900);
		t.setPrefWidth(200);
		t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	public static void setDefaultStyle(ListView l){
		l.setPrefHeight(900);
		l.setPrefWidth(100);
	}

}