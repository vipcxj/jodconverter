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

import static org.artofsolving.jodconverter.office.OfficeUtils.SERVICE_DESKTOP;
import static org.artofsolving.jodconverter.office.OfficeUtils.cast;
import static org.artofsolving.jodconverter.office.OfficeUtils.toUnoProperties;
import static org.artofsolving.jodconverter.office.OfficeUtils.toUrl;

import java.io.File;
import java.util.Map;

import org.artofsolving.jodconverter.office.OfficeContext;
import org.artofsolving.jodconverter.office.OfficeException;

import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XComponent;
import com.sun.star.task.ErrorCodeIOException;
import java.io.Serializable;
import org.artofsolving.jodconverter.document.DocumentFormat;

/**
 * Added an overridable method to handle processing of the document before it
 * get's converted
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 *
 */
public abstract class AbstractFileBasedConversionTask extends AbstractConversionTask {

    private final File inputFile;

    private final File outputFile;

    public AbstractFileBasedConversionTask(File inputFile, DocumentFormat inputFormat, File outputFile, DocumentFormat outputFormat, Map<String, Serializable> params) {
        super(inputFormat, outputFormat, params);
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    @Override
    protected XComponent loadDocument(OfficeContext context) throws OfficeException {
        if (!inputFile.exists()) {
            throw new OfficeException("input document not found");
        }
        Object desktopService = context.getService(SERVICE_DESKTOP);
        XComponentLoader loader = cast(XComponentLoader.class, desktopService);

        Map<String, ?> loadProperties = getLoadProperties();
        XComponent document = null;
        try {
            document = loader.loadComponentFromURL(toUrl(inputFile), "_blank",
                    0, toUnoProperties(loadProperties));
        } catch (ErrorCodeIOException errorCodeIOException) {
            throw new OfficeException("could not load document: "
                    + inputFile.getName() + "; errorCode: "
                    + errorCodeIOException.ErrCode, errorCodeIOException);
        } catch (IllegalArgumentException | IOException illegalArgumentException) {
            throw new OfficeException("could not load document: "
                    + inputFile.getName(), illegalArgumentException);
        }
        if (document == null) {
            throw new OfficeException("could not load document: "
                    + inputFile.getName());
        }

        handleDocumentLoaded(document);

        return document;
    }

    @Override
    protected void storeDocument(XComponent document) throws OfficeException {
        Map<String, ?> storeProperties = getStoreProperties(document);
        if (storeProperties == null) {
            throw new OfficeException("unsupported conversion");
        }
        try {
            cast(XStorable.class, document).storeToURL(toUrl(outputFile),
                    toUnoProperties(storeProperties));
        } catch (ErrorCodeIOException errorCodeIOException) {
            throw new OfficeException("could not store document: "
                    + outputFile.getName() + "; errorCode: "
                    + errorCodeIOException.ErrCode, errorCodeIOException);
        } catch (IOException ioException) {
            throw new OfficeException("could not store document: "
                    + outputFile.getName(), ioException);
        }
    }

}
