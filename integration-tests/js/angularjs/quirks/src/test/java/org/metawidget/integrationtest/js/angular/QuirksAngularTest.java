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

package org.metawidget.integrationtest.js.angular;

import org.metawidget.util.AngularScenarioRunnerTestCase;

/**
 * @author <a href="http://kennardconsulting.com">Richard Kennard</a>
 */

public class QuirksAngularTest
	extends AngularScenarioRunnerTestCase {

	//
	// Public methods
	//

	public void testQuirks() {

		runScenarioRunner( "http://localhost:8180/quirks-angularjs/test-runner.html" );
	}
	
	//
	// Protected methods
	//
	
	@Override
	protected int getExpectedNumberOfTests() {

		return 1;
	}	
}