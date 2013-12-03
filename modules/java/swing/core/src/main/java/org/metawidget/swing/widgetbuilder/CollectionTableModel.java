// Metawidget
//
// This library is dual licensed under both LGPL and a commercial
// license.
//
// LGPL: this library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
// Commercial License: See http://metawidget.org for details

package org.metawidget.swing.widgetbuilder;

import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;

/**
 * Simple implementation of a <code>TableModel</tt> that supports <tt>Collections</tt>s.
 */

class CollectionTableModel<T>
	extends AbstractTableModel {

	//
	// Private members
	//

	private List<T>			mList;

	private List<String>	mColumns;

	//
	// Constructor
	//

	public CollectionTableModel( Collection<T> collection, List<String> columns ) {

		if ( collection instanceof List<?> ) {
			mList = (List<T>) collection;
		} else if ( collection != null ) {
			mList = CollectionUtils.newArrayList( collection );
		}
		mColumns = columns;
	}

	//
	// Public methods
	//

	public int getColumnCount() {

		return mColumns.size();
	}

	@Override
	public String getColumnName( int columnIndex ) {

		if ( columnIndex >= getColumnCount() ) {
			return null;
		}

		return mColumns.get( columnIndex );
	}

	public int getRowCount() {

		if ( mList == null ) {
			return 0;
		}
		
		return mList.size();
	}

	public T getValueAt( int rowIndex ) {

		if ( rowIndex >= getRowCount() ) {
			return null;
		}

		return mList.get( rowIndex );
	}

	public Object getValueAt( int rowIndex, int columnIndex ) {

		if ( columnIndex >= getColumnCount() ) {
			return null;
		}

		T t = getValueAt( rowIndex );

		if ( t == null ) {
			return null;
		}

		return ClassUtils.getProperty( t, getColumnName( columnIndex ) );
	}
}
