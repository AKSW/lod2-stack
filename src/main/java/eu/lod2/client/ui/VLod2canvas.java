package eu.lod2.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.dom.client.Document;
import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.animation.Animatable;
import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Text;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;


public class VLod2canvas extends Composite implements Paintable, ClickHandler
{

	/** Set the CSS class name to allow styling. */
	public static final String CLASSNAME = "v-lod2canvas";

	private HorizontalPanel panel;

	private DrawingArea canvas;

	/** Component identifier in UIDL communications. */
	String uidlId;

	/** Reference to the server connection object. */
	ApplicationConnection client;
 
	/** Selected circle */
  
	String selectedCircle;

	/** keep track of the Objects on the screen */
	private Map<Circle, Circle> objectReference = new HashMap<Circle, Circle>();


	public VLod2canvas() {
		//    setElement(Document.get().createDivElement());

		// AbsolutePanel panel = new AbsolutePanel();
		HorizontalPanel panel = new HorizontalPanel();

		canvas = new DrawingArea(800, 500);
		panel.add(canvas);

		Rectangle rect = new Rectangle(10, 10, 100, 50);
		canvas.add(rect);

		this.addColoredCircle(413, 163, "#ff7f00", "Authoring");
		this.addColoredCircle(122, 353, "#007fff", "Repair");
		this.addColoredCircle(291, 400, "#ff007f", "Fusing");
		this.addColoredCircle(427, 314, "#ff0000", "Enrichment");
		this.addColoredCircle(249, 104, "#ffff00", "Exploration");
		this.addColoredCircle(104, 189, "#7fff00", "Extraction");


		final Button button = new Button("GWT click me");
		panel.add(button);

		//	  setStyleName(CLASSNAME);

		DOM.setStyleAttribute(canvas.getElement(), "border", "1px solid black");

		initWidget(panel);

	}

	public void onClick(ClickEvent event) {
		Object sender = event.getSource();
		if (sender instanceof Circle) {
				Circle sc = null;
				sc = (Circle) sender; 
				sc.setFillColor("#FFFFFF");
			}

	};

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		// This call should be made first. Ensure correct implementation,
		// and let the containing layout manage caption, etc.
		if (client.updateComponent(this, uidl, true)) {
			return;
		}; 

		this.client = client;
		uidlId = uidl.getId();

		final String sCircle = uidl.getStringVariable("selected"); 
		if (selectedCircle != sCircle) {
				selectedCircle = sCircle;
						
		};

		if (canvas != null) {  
			final int cwidth = uidl.getIntVariable("width"); 
			canvas.setWidth(cwidth);
			/*
			if (cwidth > 100) {
				canvas.setWidth(cwidth);
			} else {
				canvas.setWidth(cwidth);
			};
			*/

			Rectangle rect = new Rectangle(20, 20, 100, 50);
			canvas.add(rect);
		};

		//	 getElement().setInnerHTML("It works!");


	};


    // create a colored circle with a centered label on position X,Y 
    // radius is fixed
    // textcolor is fixed
    private void addColoredCircle(int x, int y, String color, String label) {

		Circle c1 = new Circle(x, y, 65);
		c1.setFillColor(color);
		Text t1 = new Text(x, y, label);
		canvas.add(c1);
		canvas.add(t1);
		final int tw1 = t1.getTextWidth() / 2 ;
		t1.setX(t1.getX() - tw1);
		t1.setFillColor("#000000");
		objectReference.put(c1, c1);
		c1.addClickHandler(this);


    }; 

};


