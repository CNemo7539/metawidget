// Metawidget ${project.version}
//
// This file is dual licensed under both the LGPL
// (http://www.gnu.org/licenses/lgpl-2.1.html) and the EPL
// (http://www.eclipse.org/org/documents/epl-v10.php). As a
// recipient of Metawidget, you may choose to receive it under either
// the LGPL or the EPL.
//
// Commercial licenses are also available. See http://metawidget.org
// for details.

/**
 * @author <a href="http://kennardconsulting.com">Richard Kennard</a>
 */

( function() {

	'use strict';

	describe( "The IdProcessor", function() {

		it( "assigns ids to widgets", function() {

			var processor = new metawidget.widgetprocessor.IdProcessor();

			var widget = simpleDocument.createElement( 'input' );
			var mw = {};
			processor.processWidget( widget, "property", {
				name: "foo"
			}, mw );
			expect( widget.toString() ).toBe( 'input id="foo"' );

			// With subpath

			widget = simpleDocument.createElement( 'input' );
			mw.path = 'foo.bar';
			processor.processWidget( widget, "property", {
				name: "baz"
			}, mw );
			expect( widget.toString() ).toBe( 'input id="fooBarBaz"' );

			// With root

			widget = simpleDocument.createElement( 'input' );
			mw.path = 'foo.bar';
			processor.processWidget( widget, "entity", {}, mw );
			expect( widget.toString() ).toBe( 'input id="fooBar"' );
		} );

		it( "does not reassigns ids", function() {

			var processor = new metawidget.widgetprocessor.IdProcessor();

			var widget = simpleDocument.createElement( 'input' );
			widget.setAttribute( 'id', 'do-not-touch' );
			var mw = {};
			processor.processWidget( widget, "property", {
				name: "foo"
			}, mw );
			expect( widget.toString() ).toBe( 'input id="do-not-touch"' );
		} );
	} );

	describe( "The RequiredAttributeProcessor", function() {

		it( "assigns the required attribute to widgets", function() {

			var processor = new metawidget.widgetprocessor.RequiredAttributeProcessor();

			var widget = simpleDocument.createElement( 'input' );
			var mw = {};

			processor.processWidget( widget, "property", {}, mw );
			expect( widget.hasAttribute( 'required' ) ).toBe( false );

			processor.processWidget( widget, "property", {
				required: "false"
			}, mw );
			expect( widget.hasAttribute( 'required' ) ).toBe( false );

			processor.processWidget( widget, "property", {
				required: "true"
			}, mw );
			expect( widget.getAttribute( 'required' ) ).toBe( 'required' );
		} );
	} );

	describe( "The PlaceholderAttributeProcessor", function() {

		it( "assigns the placeholder attribute to widgets", function() {

			var processor = new metawidget.widgetprocessor.PlaceholderAttributeProcessor();

			var widget = simpleDocument.createElement( 'input' );
			var mw = {};

			processor.processWidget( widget, "property", {}, mw );
			expect( widget.hasAttribute( 'placeholder' ) ).toBe( false );

			processor.processWidget( widget, "property", {
				placeholder: "Foo"
			}, mw );
			expect( widget.getAttribute( 'placeholder' ) ).toBe( 'Foo' );
		} );
	} );

	describe( "The DisabledAttributeProcessor", function() {

		it( "assigns the disabled attribute to widgets", function() {

			var processor = new metawidget.widgetprocessor.DisabledAttributeProcessor();

			var widget = simpleDocument.createElement( 'input' );
			var mw = {};

			processor.processWidget( widget, "property", {}, mw );
			expect( widget.hasAttribute( 'disabled' ) ).toBe( false );

			processor.processWidget( widget, "property", {
				disabled: true
			}, mw );
			expect( widget.getAttribute( 'disabled' ) ).toBe( 'disabled' );
		} );
	} );

	describe( "The SimpleBindingProcessor", function() {

		it( "processes widgets and binds them", function() {

			var processor = new metawidget.widgetprocessor.SimpleBindingProcessor();
			var attributes = {
				name: "foo",
			};
			var mw = {
				toInspect: {
					foo: "fooValue",
					bar: "barValue",
					baz: "bazValue",
					boolean: true,
					select: false,
					number: 42,
					password: 'fooBar',
					search: 'My Search',
					array: [ 'Bar' ],
					multiArray: [ 2, 4 ]
				},
				path: "testPath"
			};

			processor.onStartBuild( mw );

			// Inputs

			var widget = simpleDocument.createElement( 'input' );
			widget.setAttribute( 'id', 'fooId' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'input id="fooId" name="fooId"' );
			expect( widget.value ).toBe( 'fooValue' );

			// Button inputs

			attributes = {
				name: "bar"
			};
			widget = simpleDocument.createElement( 'input' );
			widget.setAttribute( 'type', 'button' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'input type="button"' );
			expect( widget.onclick.toString() ).toContain( 'return mw.toInspect[attributes.name]();' );

			// Submit inputs

			widget = simpleDocument.createElement( 'input' );
			widget.setAttribute( 'type', 'submit' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'input type="submit"' );
			expect( widget.onclick.toString() ).toContain( 'return mw.toInspect[attributes.name]();' );

			// Outputs

			widget = simpleDocument.createElement( 'output' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'output' );
			expect( widget.innerHTML ).toBe( 'barValue' );

			// Enums

			attributes = {
				name: "baz",
				enum: [ "bazValue1", "bazValue", "bazValue3" ],
				enumTitles: [ "1", "2", "3" ]
			};
			widget = simpleDocument.createElement( 'output' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'output' );
			expect( widget.innerHTML ).toBe( '2' );

			attributes = {
				name: "baz",
				enum: [ "bazValue1", "bazValue", "bazValue3" ],
				enumTitles: []
			};
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'output' );
			expect( widget.innerHTML ).toBe( 'bazValue' );

			attributes = {
				name: "multiArray",
				type: "array",
				enum: [ 1, 2, 3, 4, 5 ],
				enumTitles: [ "One", "Two", "Three", "Four", "Five" ]
			};
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'output' );
			expect( widget.innerHTML ).toBe( 'Two, Four' );

			// Masked

			attributes = {
				name: "password",
				masked: true
			};
			widget = simpleDocument.createElement( 'output' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'output' );
			expect( widget.innerHTML ).toBe( '******' );

			// Search

			attributes = {
				name: "search",
				componentType: "search"
			};
			widget = simpleDocument.createElement( 'input' );
			widget.setAttribute( 'type', 'search' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'input type="search"' );
			expect( widget.value ).toBe( 'My Search' );
			widget.value = 'My Search Updated';
			processor.save( mw );
			expect( mw.toInspect.search ).toBe( 'My Search Updated' );

			// Textareas

			attributes = {
				name: "baz"
			};
			widget = simpleDocument.createElement( 'textarea' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'textarea' );
			expect( widget.innerHTML ).toBe( 'bazValue' );

			// Checkboxes

			attributes = {
				name: "boolean"
			};
			widget = simpleDocument.createElement( 'input' );
			widget.setAttribute( 'type', 'checkbox' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'input type="checkbox"' );
			expect( widget.checked ).toBe( true );

			// Arrays of check boxes

			attributes = {
				name: "array",
				type: "array",
				enum: [ 'Foo', 'Bar', 'Baz' ]
			};
			widget = simpleDocument.createElement( 'div' );
			widget.setAttribute( 'id', 'array' );
			var childNode1 = simpleDocument.createElement( 'input' );
			childNode1.value = 'Foo';
			var labelNode1 = simpleDocument.createElement( 'label' );
			labelNode1.appendChild( childNode1 );
			widget.appendChild( labelNode1 );
			var childNode2 = simpleDocument.createElement( 'input' );
			childNode2.value = 'Bar';
			var labelNode2 = simpleDocument.createElement( 'label' );
			labelNode2.appendChild( childNode2 );
			widget.appendChild( labelNode2 );
			var childNode3 = simpleDocument.createElement( 'input' );
			childNode3.value = 'Baz';
			var labelNode3 = simpleDocument.createElement( 'label' );
			labelNode3.appendChild( childNode3 );
			widget.appendChild( labelNode3 );
			processor.processWidget( widget, "property", attributes, mw );
			expect( childNode1.getAttribute( 'name' ) ).toBe( 'array' );
			expect( childNode1.checked ).toBe( false );
			expect( childNode2.getAttribute( 'name' ) ).toBe( 'array' );
			expect( childNode2.checked ).toBe( true );
			expect( childNode3.getAttribute( 'name' ) ).toBe( 'array' );
			expect( childNode3.checked ).toBe( false );
			childNode1.checked = true;
			childNode2.checked = false;
			childNode3.checked = true;
			processor.save( mw );
			expect( mw.toInspect.array[0] ).toBe( 'Foo' );
			expect( mw.toInspect.array[1] ).toBe( 'Baz' );
			expect( mw.toInspect.array.length ).toBe( 2 );

			delete mw.toInspect.array;
			processor.processWidget( widget, "property", attributes, mw );
			expect( childNode1.checked ).toBe( false );
			expect( childNode2.checked ).toBe( false );
			expect( childNode3.checked ).toBe( false );

			// Select boxes

			attributes = {
				name: "select"
			};
			widget = simpleDocument.createElement( 'select' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'select' );
			expect( widget.value ).toBe( false );

			// Radio button booleans

			attributes = {
				name: "boolean",
				type: "boolean",
				componentType: "radio",
				enum: [ true, false ]
			};
			widget = simpleDocument.createElement( 'div' );
			widget.setAttribute( 'id', 'topId' );
			var labelTrue = simpleDocument.createElement( 'label' );
			var inputTrue = simpleDocument.createElement( 'input' );
			inputTrue.setAttribute( 'type', 'radio' );
			inputTrue.value = 'true';
			labelTrue.appendChild( inputTrue );
			var labelFalse = simpleDocument.createElement( 'label' );
			var inputFalse = simpleDocument.createElement( 'input' );
			inputFalse.setAttribute( 'type', 'radio' );
			inputFalse.value = 'false';
			labelFalse.appendChild( inputFalse );
			widget.appendChild( labelTrue );
			widget.appendChild( labelFalse );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'div id="topId"' );
			expect( inputTrue.toString() ).toBe( 'input type="radio" name="topId"' );
			expect( inputTrue.checked ).toBe( true );
			expect( inputFalse.toString() ).toBe( 'input type="radio" name="topId"' );
			expect( inputFalse.checked ).toBe( false );
			inputTrue.checked = false;
			inputFalse.checked = true;
			expect( mw.toInspect.boolean ).toBe( true );
			processor.save( mw );
			expect( mw.toInspect.boolean ).toBe( false );

			// Radio button non-booleans

			attributes = {
				name: "foo",
				componentType: "radio",
				enum: [ 'fooValue', 'fooNewValue' ]
			};
			widget = simpleDocument.createElement( 'div' );
			widget.setAttribute( 'id', 'topId' );
			var label1 = simpleDocument.createElement( 'label' );
			var input1 = simpleDocument.createElement( 'input' );
			input1.setAttribute( 'type', 'radio' );
			input1.value = 'fooValue';
			label1.appendChild( input1 );
			var label2 = simpleDocument.createElement( 'label' );
			var input2 = simpleDocument.createElement( 'input' );
			input2.setAttribute( 'type', 'radio' );
			input2.value = 'fooNewValue';
			label2.appendChild( input2 );
			widget.appendChild( label1 );
			widget.appendChild( label2 );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.toString() ).toBe( 'div id="topId"' );
			expect( input1.toString() ).toBe( 'input type="radio" name="topId"' );
			expect( input1.checked ).toBe( true );
			expect( input2.toString() ).toBe( 'input type="radio" name="topId"' );
			expect( input2.checked ).toBe( false );
			input1.checked = false;
			input2.checked = true;
			expect( mw.toInspect.foo ).toBe( "fooValue" );
			processor.save( mw );
			expect( mw.toInspect.foo ).toBe( "fooNewValue" );

			// Root-level

			widget = simpleDocument.createElement( 'output' );
			processor.processWidget( widget, "entity", {}, mw );
			expect( widget.toString() ).toBe( 'output' );
			expect( widget.innerHTML ).toBe( mw.toInspect );
		} );

		it( "supports nested widgets", function() {

			var element = simpleDocument.createElement( 'div' );
			var mw = new metawidget.Metawidget( element, {
				layout: new metawidget.layout.SimpleLayout()
			} );

			mw.toInspect = {
				nested: {
					"nestedFoo": "nestedFooValue"
				}
			};
			mw.buildWidgets();

			expect( element.childNodes[0].toString() ).toBe( 'div id="nested"' );
			expect( element.childNodes[0].childNodes[0].toString() ).toBe( 'input type="text" id="nestedNestedFoo" name="nestedNestedFoo"' );
			expect( element.childNodes[0].childNodes[0].value ).toBe( 'nestedFooValue' );
			expect( element.childNodes[0].childNodes.length ).toBe( 1 );
			expect( element.childNodes.length ).toBe( 1 );

			element.childNodes[0].childNodes[0].value = 'nestedFooValue1';
			mw.getWidgetProcessor( function( testInstanceOf ) {

				return testInstanceOf instanceof metawidget.widgetprocessor.SimpleBindingProcessor;
			} ).save( mw );
			expect( mw.toInspect.nested.nestedFoo ).toBe( 'nestedFooValue1' );
		} );

		it( "supports paths", function() {

			var element = simpleDocument.createElement( 'div' );
			var mw = new metawidget.Metawidget( element, {
				layout: new metawidget.layout.SimpleLayout()
			} );

			mw.toInspect = {
				firstname: 'bar'
			};
			mw.path = 'toInspect.firstname';
			mw.buildWidgets();

			expect( element.childNodes[0].toString() ).toBe( 'input type="text" id="toInspectFirstname" name="toInspectFirstname"' );
			expect( element.childNodes[0].value ).toBe( 'bar' );
			expect( element.childNodes.length ).toBe( 1 );

			element.childNodes[0].value = 'baz';

			// Should set properties

			mw.getWidgetProcessor( function( testInstanceOf ) {

				return testInstanceOf instanceof metawidget.widgetprocessor.SimpleBindingProcessor;
			} ).save( mw );
			expect( mw.toInspect.firstname ).toBe( 'baz' );

			// Should not set sub-properties

			mw.toInspect = {
				firstname: {}
			};
			mw.getWidgetProcessor( function( testInstanceOf ) {

				return testInstanceOf instanceof metawidget.widgetprocessor.SimpleBindingProcessor;
			} ).save( mw );
			expect( mw.toInspect.firstname.firstname ).toNotBe( 'baz' );

			// Should support read-only top-level

			mw.toInspect = 'Bar';
			delete mw.path;

			mw.buildWidgets();

			expect( element.childNodes[0].toString() ).toBe( 'input type="text"' );
			expect( element.childNodes[0].value ).toBe( 'Bar' );
			expect( element.childNodes.length ).toBe( 1 );

			element.childNodes[0].value = 'Baz';

			mw.getWidgetProcessor( function( testInstanceOf ) {

				return testInstanceOf instanceof metawidget.widgetprocessor.SimpleBindingProcessor;
			} ).save( mw );
			expect( mw.toInspect ).toBe( 'Bar' );
		} );

		it( "creates children just-in-time", function() {

			var element = simpleDocument.createElement( 'div' );
			var mw = new metawidget.Metawidget( element, {
				inspector: function() {

					return {
						properties: {
							firstname: {
								type: 'string'
							}
						}
					};
				},
				layout: new metawidget.layout.SimpleLayout()
			} );

			mw.toInspect = {};
			mw.path = 'toInspect.jit';
			mw.buildWidgets();

			expect( element.childNodes[0].toString() ).toBe( 'input type="text" id="toInspectJitFirstname" name="toInspectJitFirstname"' );
			expect( element.childNodes[0].value ).toBeUndefined();
			expect( element.childNodes.length ).toBe( 1 );

			element.childNodes[0].value = 'baz';

			mw.getWidgetProcessor( function( testInstanceOf ) {

				return testInstanceOf instanceof metawidget.widgetprocessor.SimpleBindingProcessor;
			} ).save( mw );
			expect( mw.toInspect.jit.firstname ).toBe( 'baz' );
		} );

		it( "can reload from HTTP request", function() {

			var element = simpleDocument.createElement( 'div' );
			var mw = new metawidget.Metawidget( element, {
				inspector: new metawidget.inspector.JsonSchemaInspector( {
					properties: {
						firstname: {
							type: 'string'
						},
						nested: {
							properties: {
								surname: {
									type: 'string'
								},
								retired: {
									type: 'boolean'
								},
								state: {
									type: 'array',
									enum: [ 'ACT', 'NSW', 'NT', 'QLD', 'SA', 'TAS', 'VIC', 'WA' ]
								}
							}
						}

					}
				} )
			} );

			mw.toInspect = {};
			mw.buildWidgets();

			var processor = mw.getWidgetProcessor( function( testInstanceOf ) {

				return testInstanceOf instanceof metawidget.widgetprocessor.SimpleBindingProcessor;
			} );

			processor.reload( {
				firstname: 'FooFirstname',
				nestedSurname: 'FooSurname',
				nestedRetired: true,
				nestedExtraData: 'Hacker',
				nestedState: [ 'ACT', 'NSW' ]
			}, mw );
			expect( mw.toInspect.firstname ).toBeUndefined();
			processor.save( mw );
			expect( mw.toInspect.firstname ).toBe( 'FooFirstname' );
			expect( mw.toInspect.nested.surname ).toBe( 'FooSurname' );
			expect( mw.toInspect.nested.retired ).toBe( true );
			expect( mw.toInspect.nested.extraData ).toBeUndefined();
			expect( mw.toInspect.nested.state[0] ).toBe( 'ACT' );
			expect( mw.toInspect.nested.state[1] ).toBe( 'NSW' );
			expect( mw.toInspect.nested.state.length ).toBe( 2 );
		} );

		it( "converts simple data types", function() {

			var processor = new metawidget.widgetprocessor.SimpleBindingProcessor();
			var attributes = {
				name: "foo",
			};
			var mw = {
				toInspect: {
					string1: 'string',
					boolean1: true,
					number1: 42
				},
				path: "testPath"
			};

			processor.onStartBuild( mw );

			// Text

			var attributes = {
				name: "string1",
				type: "string"
			};
			var widget = simpleDocument.createElement( 'input' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.value ).toBe( 'string' );
			expect( processor.save( mw ) ).toBe( false );
			widget.value = 'string2';
			expect( processor.save( mw ) ).toBe( true );
			expect( mw.toInspect.string1 ).toBe( 'string2' );

			widget.value = '';
			expect( processor.save( mw ) ).toBe( true );
			expect( mw.toInspect.string1 ).toBeUndefined();

			widget.value = 'string3';
			expect( processor.save( mw ) ).toBe( true );
			expect( mw.toInspect.string1 ).toBe( 'string3' );

			widget.value = null;
			expect( processor.save( mw ) ).toBe( true );
			expect( mw.toInspect.string1 ).toBeUndefined();

			// Numbers

			var attributes = {
				name: "number1",
				type: "number"
			};
			var widget = simpleDocument.createElement( 'input' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.value ).toBe( 42 );
			expect( processor.save( mw ) ).toBe( false );
			widget.value = '43';
			expect( processor.save( mw ) ).toBe( true );
			expect( mw.toInspect.number1 ).toBe( 43 );
			widget.value = '43.5';
			expect( processor.save( mw ) ).toBe( true );
			expect( mw.toInspect.number1 ).toBe( 43.5 );

			widget.value = null;
			expect( processor.save( mw ) ).toBe( true );
			expect( mw.toInspect.number1 ).toBeUndefined();

			widget.value = 'not a number';

			// (returns false because does not actually change any data)

			expect( processor.save( mw ) ).toBe( false );
			expect( mw.toInspect.number1 ).toBeUndefined();

			// Booleans

			var attributes = {
				name: "boolean1",
				type: "boolean"
			};
			var widget = simpleDocument.createElement( 'input' );
			widget.setAttribute( 'type', 'checkbox' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.checked ).toBe( true );
			expect( processor.save( mw ) ).toBe( false );
			widget.checked = false;
			expect( processor.save( mw ) ).toBe( true );
			expect( mw.toInspect.boolean1 ).toBe( false );

			// Non-checkbox booleans

			var attributes = {
				name: "boolean1",
				type: "boolean"
			};
			var widget = simpleDocument.createElement( 'input' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.value ).toBe( false );
			widget.value = 'true';
			processor.save( mw );
			expect( mw.toInspect.boolean1 ).toBe( true );
			widget.value = 'false';
			processor.save( mw );
			expect( mw.toInspect.boolean1 ).toBe( false );

			// Read-only booleans

			var attributes = {
				name: "boolean1",
				type: "boolean"
			};
			var widget = simpleDocument.createElement( 'output' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.innerHTML ).toBe( 'No' );
			mw.toInspect.boolean1 = true;
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.innerHTML ).toBe( 'Yes' );

			// Read-only i10n booleans

			var attributes = {
				name: "boolean1",
				type: "boolean"
			};
			mw.l10n = {
				yes: 'Oui',
				no: 'Pas'
			};
			var widget = simpleDocument.createElement( 'output' );
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.innerHTML ).toBe( 'Oui' );
			mw.toInspect.boolean1 = false;
			processor.processWidget( widget, "property", attributes, mw );
			expect( widget.innerHTML ).toBe( 'Pas' );
		} );
	} );
} )();