// Metawidget Examples (licensed under BSD License)
//
// Copyright (c) Richard Kennard
// All rights reserved
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
//
// * Redistributions of source code must retain the above copyright notice,
//   this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
// * Neither the name of Richard Kennard nor the names of its contributors may
//   be used to endorse or promote products derived from this software without
//   specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
// OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
// OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.

package org.metawidget.example.swing.addressbook.converter;

import org.jdesktop.beansbinding.Converter;

/**
 * @author <a href="http://kennardconsulting.com">Richard Kennard</a>
 */

public abstract class EnumConverter<T extends Enum<T>>
	extends Converter<T, String> {

	//
	// Private members
	//

	private Class<T>	mEnum;

	//
	// Constructor
	//

	protected EnumConverter( Class<T> anEnum ) {

		mEnum = anEnum;
	}

	//
	// Public methods
	//

	@Override
	public String convertForward( T anEnum ) {

		// The enum will have been converted to its '.name' by PropertyTypeInspector when
		// it creates lookup values and labels. This means we must also convert the
		// enum to its '.name' during binding.
		//
		// The alternative to this is to have the Metawidgets deal with enums directly, but
		// that is less desirable because it ties the Metawidgets to a Java 5 platform

		return anEnum.name();
	}

	@Override
	public T convertReverse( String name ) {

		return Enum.valueOf( mEnum, name );
	}
}
