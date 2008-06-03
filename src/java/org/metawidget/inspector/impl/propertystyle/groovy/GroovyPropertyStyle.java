// Metawidget
//
// This library is free software; you can redistribute it and/or
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

package org.metawidget.inspector.impl.propertystyle.groovy;

import groovy.lang.GroovySystem;
import groovy.lang.MetaBeanProperty;
import groovy.lang.MetaClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.reflection.CachedField;
import org.metawidget.inspector.InspectorException;
import org.metawidget.inspector.impl.propertystyle.AbstractProperty;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.inspector.impl.propertystyle.PropertyStyle;
import org.metawidget.util.ArrayUtils;
import org.metawidget.util.CollectionUtils;

/**
 * PropertyStyle for Groovy-style properties.
 * <p>
 * Groovy-style properties can <em>almost</em> be handled using <code>JavaBeanPropertyStyle</code>,
 * because the Groovy compiler automatically generates JavaBean-style getters and setters.
 * Unfortunately, it does not also copy any annotations defined on the property to the generated
 * getter and setters. This <code>PropertyStyle</code> is designed to access those annotations.
 *
 * @author Richard Kennard
 */

public class GroovyPropertyStyle
	implements PropertyStyle
{
	//
	//
	// Private statics
	//
	//

	private final static Class<?>[]	DEFAULT_EXCLUDE_TYPES	= new Class<?>[] { Class.class, MetaClass.class };

	//
	//
	// Private members
	//
	//

	private Class<?>[]				mExcludeTypes;

	//
	//
	// Constructor
	//
	//

	public GroovyPropertyStyle()
	{
		this( DEFAULT_EXCLUDE_TYPES );
	}

	public GroovyPropertyStyle( Class<?>[] excludeTypes )
	{
		mExcludeTypes = excludeTypes;
	}

	//
	//
	// Public methods
	//
	//

	public Map<String, Property> getProperties( Class<?> clazz )
	{
		Map<String, Property> propertiesToReturn = CollectionUtils.newHashMap();

		@SuppressWarnings( "unchecked" )
		List<MetaBeanProperty> properties = GroovySystem.getMetaClassRegistry().getMetaClass( clazz ).getProperties();

		for ( MetaBeanProperty property : properties )
		{
			// Ignore certain types

			if ( ArrayUtils.contains( mExcludeTypes, property.getType() ) )
				continue;

			propertiesToReturn.put( property.getName(), new GroovyProperty( property ) );
		}

		return propertiesToReturn;
	}

	//
	//
	// Inner classes
	//
	//

	/**
	 * Groovy-based property.
	 */

	private static class GroovyProperty
		extends AbstractProperty
	{
		//
		//
		// Private members
		//
		//

		private MetaBeanProperty	mProperty;

		//
		//
		// Constructor
		//
		//

		public GroovyProperty( MetaBeanProperty property )
		{
			super( property.getName(), property.getType() );

			mProperty = property;
		}

		//
		//
		// Public methods
		//
		//

		public boolean isReadable()
		{
			return ( mProperty.getGetter() != null );
		}

		public Object read( Object obj )
		{
			try
			{
				return mProperty.getProperty( obj );
			}
			catch ( Exception e )
			{
				throw InspectorException.newException( e );
			}
		}

		public boolean isWritable()
		{
			return ( mProperty.getSetter() != null );
		}

		public <T extends Annotation> T getAnnotation( Class<T> annotation )
		{
			CachedField field = mProperty.getField();

			if ( field != null )
				return field.field.getAnnotation( annotation );

			// TODO: fetch annotations from method

			return null;
		}

		public Type getGenericType()
		{
			CachedField field = mProperty.getField();

			if ( field != null )
				return field.field.getGenericType();

			return null;
		}
	}
}
