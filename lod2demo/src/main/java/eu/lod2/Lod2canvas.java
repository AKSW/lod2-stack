package eu.lod2;

import java.util.Map;


import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractComponent;

import com.vaadin.ui.ClientWidget;
//import com.vaadin.Application;

import eu.lod2.client.ui.VLod2canvas;

/**
 * Server side component for the Lod2canvas widget.
 */
@ClientWidget(VLod2canvas.class)
public class Lod2canvas extends AbstractSelect
{

	private int canvaswidth = 400;

	public int getCanvasWidth() {
		return (int) canvaswidth; 
	}

   /** initialize the selected value **/

	public Lod2canvas () {
			super();
			setValue(new String("extraction"));
   };

   /** The property value of the field is a String. */
    @Override
    public Class<?> getType() {
        return String.class;
    }

    /** Retrieve the currently selected value. 
	 * This is needed to hide the type cast */
    public String getSelected() {
        return (String) getValue();
    }


	@Override
		public void paintContent(PaintTarget target) throws PaintException {
			super.paintContent(target);

			// TODO Paint any component specific content by setting attributes
			// These attributes can be read in updateFromUIDL in the widget.
			//
			target.addVariable(this, "width", getCanvasWidth());
			target.addVariable(this, "selected", getSelected());
		}


	@Override
		public void changeVariables(Object source, Map variables) {
			 // Sets the currently selected 
        if (variables.containsKey("selected") && !isReadOnly()) {
            final String newValue = (String) variables.get("selected");
            // Changing the property of the component will
            // trigger a ValueChangeEvent
            setValue(newValue, true);
		}
		};

	/*
	@Override
		public void onClick(ClickEvent event) {
			canvaswidth=100;
		};
		*/

};
