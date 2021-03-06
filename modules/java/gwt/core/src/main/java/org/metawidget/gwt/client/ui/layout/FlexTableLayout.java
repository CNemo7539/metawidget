// Metawidget
//
// This file is dual licensed under both the LGPL
// (http://www.gnu.org/licenses/lgpl-2.1.html) and the EPL
// (http://www.eclipse.org/org/documents/epl-v10.php). As a
// recipient of Metawidget, you may choose to receive it under either
// the LGPL or the EPL.
//
// Commercial licenses are also available. See http://metawidget.org
// for details.

package org.metawidget.gwt.client.ui.layout;

import static org.metawidget.inspector.InspectionResultConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.metawidget.gwt.client.ui.Facet;
import org.metawidget.gwt.client.ui.GwtMetawidget;
import org.metawidget.gwt.client.ui.Stub;
import org.metawidget.layout.iface.AdvancedLayout;
import org.metawidget.util.simple.SimpleLayoutUtils;
import org.metawidget.util.simple.StringUtils;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Layout to arrange widgets in a <code>FlexTable</code>, with one column for labels and another for
 * the widget.
 *
 * @author <a href="http://kennardconsulting.com">Richard Kennard</a>
 */

public class FlexTableLayout
	implements AdvancedLayout<Widget, Panel, GwtMetawidget> {

	//
	// Private statics
	//

	private static final int	LABEL_AND_COMPONENT_AND_REQUIRED	= 3;

	//
	// Private members
	//

	private final int			mNumberOfColumns;

	private final String		mTableStyleName;

	private final String[]		mColumnStyleNames;

	private final String		mFooterStyleName;

	//
	// Constructor
	//

	public FlexTableLayout() {

		this( new FlexTableLayoutConfig() );
	}

	public FlexTableLayout( FlexTableLayoutConfig config ) {

		mNumberOfColumns = config.getNumberOfColumns();
		mTableStyleName = config.getTableStyleName();
		mColumnStyleNames = config.getColumnStyleNames();
		mFooterStyleName = config.getFooterStyleName();
	}

	//
	// Public methods
	//

	public void onStartBuild( GwtMetawidget metawidget ) {

		// Do nothing
	}

	public void startContainerLayout( Panel container, GwtMetawidget metawidget ) {

		State state = getState( container, metawidget );
		FlexTable flexTable = new FlexTable();
		flexTable.setStyleName( mTableStyleName );
		container.add( flexTable );

		state.formatter = flexTable.getFlexCellFormatter();
	}

	public void layoutWidget( Widget widget, String elementName, Map<String, String> attributes, Panel container, GwtMetawidget metawidget ) {

		// Do not render empty stubs

		if ( widget instanceof Stub && ( (Stub) widget ).getWidgetCount() == 0 ) {
			return;
		}

		// Calculate row and actualColumn. Note FlexTable doesn't work quite as might be
		// expected. Specifically, it doesn't understand 'colspan' in relation to previous rows. So
		// if you do...
		//
		// layout.setWidget( row, 0, widget1 );
		// layout.setColSpan( row, 0, 2 );
		// layout.setWidget( row, 2, widget2 );
		//
		// ...you'll actually get...
		//
		// <td colspan="2">widget1</td>
		// <td/>
		// <td>widget2</td>
		//
		// ...note FlexTable inserts an extra <td/> because it thinks column 1 is missing. Therefore
		// the actualColumn is always just the next column, regardless of what state.mCurrentColumn
		// is

		int actualColumn;
		FlexTable flexTable = (FlexTable) ( (ComplexPanel) container ).getWidget( 0 );
		int row = flexTable.getRowCount();

		int numberOfColumns = getEffectiveNumberOfColumns( container );

		State state = getState( container, metawidget );

		if ( state.currentColumn < numberOfColumns && row > 0 ) {
			row--;
			actualColumn = flexTable.getCellCount( row );
		} else {
			state.currentColumn = 0;
			actualColumn = 0;
		}

		// Special support for large components

		boolean spanAllColumns = willFillHorizontally( widget, attributes );

		if ( spanAllColumns && state.currentColumn > 0 ) {
			state.currentColumn = 0;
			actualColumn = 0;
			row++;
		}

		// Label

		String labelText = metawidget.getLabelString( attributes );

		if ( SimpleLayoutUtils.needsLabel( labelText, elementName ) ) {

			// Note: GWT Labels are not real HTML labels, and have no 'for' attribute

			Label label = new Label( labelText + StringUtils.SEPARATOR_COLON );
			String styleName = getStyleName( state.currentColumn * LABEL_AND_COMPONENT_AND_REQUIRED, metawidget );

			if ( styleName != null ) {
				state.formatter.setStyleName( row, actualColumn, styleName );
			}

			flexTable.setWidget( row, actualColumn, label );
		}

		// Widget

		// Widget column (null labels get collapsed, blank Strings get preserved)

		if ( labelText != null ) {
			// Zero-column layouts need an extra row

			if ( numberOfColumns == 0 ) {
				state.currentColumn = 0;
				actualColumn = 0;
				row++;
			} else {
				actualColumn++;
			}
		}

		String styleName = getStyleName( ( state.currentColumn * LABEL_AND_COMPONENT_AND_REQUIRED ) + 1, metawidget );

		if ( styleName != null ) {
			state.formatter.setStyleName( row, actualColumn, styleName );
		}

		flexTable.setWidget( row, actualColumn, widget );

		// Colspan

		int colspan;

		// Metawidgets and large components span all columns

		if ( spanAllColumns ) {
			colspan = ( numberOfColumns * LABEL_AND_COMPONENT_AND_REQUIRED ) - 2;
			state.currentColumn = numberOfColumns;

			if ( labelText == null ) {
				colspan++;
			}

			// Metawidgets span the required column too

			if ( widget instanceof GwtMetawidget ) {
				colspan++;
			}
		} else if ( labelText == null ) {

			// Components without labels span two columns

			colspan = 2;
		} else {
			
			// Everyone else spans just one
			
			colspan = 1;
		}

		if ( colspan > 1 ) {
			state.formatter.setColSpan( row, actualColumn, colspan );
		}

		// Required

		if ( !( widget instanceof GwtMetawidget ) ) {
			layoutRequired( attributes, container, metawidget );
		}

		state.currentColumn++;
	}

	public void endContainerLayout( Panel container, GwtMetawidget metawidget ) {

		// Do nothing
	}

	public void onEndBuild( GwtMetawidget metawidget ) {

		Facet facet = metawidget.getFacet( "buttons" );

		if ( facet != null ) {
			State state = getState( metawidget, metawidget );
			FlexTable flexTable = (FlexTable) metawidget.getWidget( 0 );
			int row = flexTable.getRowCount();

			int numberOfColumns = getEffectiveNumberOfColumns( metawidget );

			if ( numberOfColumns > 0 ) {
				state.formatter.setColSpan( row, 0, numberOfColumns * LABEL_AND_COMPONENT_AND_REQUIRED );
			}

			if ( mFooterStyleName != null ) {
				state.formatter.setStyleName( row, 0, mFooterStyleName );
			}

			flexTable.setWidget( row, 0, facet );
		}
	}

	//
	// Protected methods
	//

	protected void layoutRequired( Map<String, String> attributes, Widget container, GwtMetawidget metawidget ) {

		State state = getState( container, metawidget );
		FlexTable flexTable = (FlexTable) ( (ComplexPanel) container ).getWidget( 0 );
		int row = flexTable.getRowCount() - 1;
		int column = flexTable.getCellCount( row );

		state.formatter.setStyleName( row, column, getStyleName( ( state.currentColumn * LABEL_AND_COMPONENT_AND_REQUIRED ) + 2, metawidget ) );

		if ( attributes != null && TRUE.equals( attributes.get( REQUIRED ) ) && !TRUE.equals( attributes.get( READ_ONLY ) ) && !metawidget.isReadOnly() ) {
			flexTable.setText( row, column, "*" );
			return;
		}

		// Render an empty div, so that the CSS can force it to a certain
		// width if desired for the layout (browsers seem to not respect
		// widths set on empty table columns)
		//
		// Note: don't do <div/>, as we may not be XHTML

		flexTable.setHTML( row, column, "<div></div>" );
	}

	/**
	 * @param metawidget
	 *            the Metawidget doing the layout
	 */

	protected String getStyleName( int styleName, GwtMetawidget metawidget ) {

		if ( mColumnStyleNames == null || mColumnStyleNames.length <= styleName ) {
			return null;
		}

		return mColumnStyleNames[styleName];
	}

	protected boolean willFillHorizontally( Widget widget, Map<String, String> attributes ) {

		if ( widget instanceof GwtMetawidget ) {
			return true;
		}

		return SimpleLayoutUtils.isSpanAllColumns( attributes );
	}

	//
	// Private methods
	//

	/**
	 * Get the number of columns to use in the layout.
	 * <p>
	 * Nested Metawidgets are always just single column.
	 */

	private int getEffectiveNumberOfColumns( Widget container ) {

		if ( container.getParent() instanceof FlexTable && container.getParent().getParent() instanceof GwtMetawidget ) {
			return 1;
		}

		return mNumberOfColumns;
	}

	private State getState( Widget container, GwtMetawidget metawidget ) {

		@SuppressWarnings( "unchecked" )
		Map<Widget, State> stateMap = (Map<Widget, State>) metawidget.getClientProperty( getClass() );

		if ( stateMap == null ) {
			stateMap = new HashMap<Widget, State>();
			metawidget.putClientProperty( getClass(), stateMap );
		}

		State state = stateMap.get( container );

		if ( state == null ) {
			state = new State();
			stateMap.put( container, state );
		}

		return state;
	}

	//
	// Inner class
	//

	/**
	 * Simple, lightweight structure for saving state.
	 */

	/* package private */static class State {

		/* package private */FlexCellFormatter	formatter;

		/* package private */int				currentColumn;
	}
}
