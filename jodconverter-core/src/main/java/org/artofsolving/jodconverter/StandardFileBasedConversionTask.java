//
// JODConverter - Java OpenDocument Converter
// Copyright 2009 Art of Solving Ltd
// Copyright 2004-2009 Mirko Nasato
//
// JODConverter is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// JODConverter is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General
// Public License along with JODConverter.  If not, see
// <http://www.gnu.org/licenses/>.
//
package org.artofsolving.jodconverter;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.artofsolving.jodconverter.document.DocumentFormat;

/**
 *
 * Added Map<String, Object> params to control DocumentIndex update
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 *
 */
public class StandardFileBasedConversionTask extends AbstractFileBasedConversionTask {

    public static final String UPDATE_DOCUMENT_INDEX = "updateDocumentIndex";

    public StandardFileBasedConversionTask(File inputFile, DocumentFormat inputFormat,
            File outputFile, DocumentFormat outputFormat,
            Map<String, Serializable> params) {
        super(inputFile, inputFormat, outputFile, outputFormat, params);
    }

    public StandardFileBasedConversionTask(File inputFile, DocumentFormat inputFormat,
            File outputFile, DocumentFormat outputFormat) {
        this(inputFile, inputFormat, outputFile, outputFormat, null);
    }

}
