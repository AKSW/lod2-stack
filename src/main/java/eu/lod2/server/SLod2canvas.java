package eu.lod2.server;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractComponent;

import com.vaadin.ui.ClientWidget;
import eu.lod2.client.ui.VLod2canvas;

/**
 * Server side component for the Lod2canvas widget.
 */
@ClientWidget(VLod2canvas.class)
public class SLod2canvas extends AbstractComponent
	{

    public SLod2canvas () {
        super();
    }


    public float getWidth() {
        return (float) 500.0;
    }


    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        // TODO Paint any component specific content by setting attributes
        // These attributes can be read in updateFromUIDL in the widget.
				//
      target.addVariable(this, "width", getWidth());
    }

	};
