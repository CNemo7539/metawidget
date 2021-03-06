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

package org.metawidget.inspector.beanvalidation;

import static org.metawidget.inspector.InspectionResultConstants.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import junit.framework.TestCase;

import org.metawidget.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="http://kennardconsulting.com">Richard Kennard</a>
 */

public class BeanValidationInspectorTest
	extends TestCase {

	//
	// Public methods
	//

	public void testInspection() {

		BeanValidationInspector inspector = new BeanValidationInspector();
		Document document = XmlUtils.documentFromString( inspector.inspect( new Foo(), Foo.class.getName() ) );

		assertEquals( "inspection-result", document.getFirstChild().getNodeName() );

		// Entity

		Element entity = (Element) document.getDocumentElement().getFirstChild();
		assertEquals( ENTITY, entity.getNodeName() );
		assertEquals( Foo.class.getName(), entity.getAttribute( TYPE ) );
		assertFalse( entity.hasAttribute( NAME ) );

		// Properties

		Element property = XmlUtils.getChildWithAttributeValue( entity, NAME, "bar" );
		assertEquals( PROPERTY, property.getNodeName() );
		assertEquals( TRUE, property.getAttribute( REQUIRED ) );
		assertEquals( 2, property.getAttributes().getLength() );

		property = XmlUtils.getChildWithAttributeValue( entity, NAME, "baz" );
		assertEquals( PROPERTY, property.getNodeName() );
		assertEquals( "1", property.getAttribute( MAXIMUM_INTEGER_DIGITS ) );
		assertEquals( "2", property.getAttribute( MAXIMUM_FRACTIONAL_DIGITS ) );
		assertEquals( 3, property.getAttributes().getLength() );

		property = XmlUtils.getChildWithAttributeValue( entity, NAME, "range" );
		assertEquals( PROPERTY, property.getNodeName() );
		assertEquals( "1", property.getAttribute( MINIMUM_VALUE ) );
		assertEquals( "99", property.getAttribute( MAXIMUM_VALUE ) );
		assertEquals( "2", property.getAttribute( MINIMUM_LENGTH ) );
		assertEquals( "25", property.getAttribute( MAXIMUM_LENGTH ) );
		assertEquals( 5, property.getAttributes().getLength() );

		property = XmlUtils.getChildWithAttributeValue( entity, NAME, "telephone" );
		assertEquals( PROPERTY, property.getNodeName() );
		assertEquals( "[A-Za-z ]*", property.getAttribute( VALIDATION_PATTERN ) );
		assertEquals( 2, property.getAttributes().getLength() );
	}

	//
	// Inner class
	//

	public static class Foo {

		@NotNull
		public String getBar() {

			return null;
		}

		public void setBar( @SuppressWarnings( "unused" ) String bar ) {

			// Do nothing
		}

		@Digits( integer = 1, fraction = 2 )
		public String getBaz() {

			return null;
		}

		public void setBaz( @SuppressWarnings( "unused" ) String baz ) {

			// Do nothing
		}

		@Min( 1 )
		@Max( 99 )
		@Size( min = 2, max = 25 )
		public int getRange() {

			return 0;
		}

		public void setRange( @SuppressWarnings( "unused" ) int range ) {

			// Do nothing
		}

		@Pattern( regexp = "[A-Za-z ]*" )
		public String getTelephone() {

			return null;
		}
	}
}
