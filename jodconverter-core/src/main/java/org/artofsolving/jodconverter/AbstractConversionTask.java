/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.artofsolving.jodconverter;

import com.sun.star.container.XIndexAccess;
import com.sun.star.lang.XComponent;
import com.sun.star.text.XDocumentIndex;
import com.sun.star.text.XDocumentIndexesSupplier;
import com.sun.star.text.XTextDocument;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.XCloseable;
import com.sun.star.util.XRefreshable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import static org.artofsolving.jodconverter.StandardFileBasedConversionTask.UPDATE_DOCUMENT_INDEX;
import org.artofsolving.jodconverter.document.DocumentFamily;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.office.OfficeContext;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeTask;
import static org.artofsolving.jodconverter.office.OfficeUtils.cast;

/**
 *
 * @author Administrator
 */
public abstract class AbstractConversionTask implements OfficeTask {

    protected final DocumentFormat outputFormat;

    protected Map<String, ?> defaultLoadProperties;

    protected final DocumentFormat inputFormat;

    protected final Map<String, Serializable> params;

    public AbstractConversionTask(DocumentFormat inputFormat, DocumentFormat outputFormat, Map<String, Serializable> params) {
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.params = params != null ? new HashMap<>(params) : new HashMap<String, Serializable>();
    }

    protected abstract XComponent loadDocument(OfficeContext context) throws OfficeException;

    protected abstract void storeDocument(XComponent document) throws OfficeException;

    @Override
    public void execute(OfficeContext context) throws OfficeException {
        XComponent document = null;
        try {
            document = loadDocument(context);
            storeDocument(document);
        } catch (OfficeException officeException) {
            throw officeException;
        } catch (Exception exception) {
            throw new OfficeException("conversion failed", exception);
        } finally {
            if (document != null) {
                XCloseable closeable = cast(XCloseable.class, document);
                if (closeable != null) {
                    try {
                        closeable.close(true);
                    } catch (CloseVetoException closeVetoException) {
                        // whoever raised the veto should close the document
                    }
                } else {
                    document.dispose();
                }
            }
        }
    }

    public void setDefaultLoadProperties(Map<String, ?> defaultLoadProperties) {
        this.defaultLoadProperties = defaultLoadProperties;
    }

    public Map<String, ?> getDefaultLoadProperties() {
        return defaultLoadProperties;
    }

    protected Map<String, ?> getLoadProperties() {
        Map<String, Object> loadProperties = new HashMap<>();
        if (defaultLoadProperties != null) {
            loadProperties.putAll(defaultLoadProperties);
        }
        if (inputFormat != null && inputFormat.getLoadProperties() != null) {
            loadProperties.putAll(inputFormat.getLoadProperties());
        }
        return loadProperties;
    }

    protected Map<String, ?> getStoreProperties(XComponent document) {
        DocumentFamily family = OfficeDocumentUtils.getDocumentFamily(document);
        return outputFormat.getStoreProperties(family);
    }

    protected void handleDocumentLoaded(XComponent document) {
        XRefreshable refreshable = cast(XRefreshable.class, document);
        if (refreshable != null) {
            refreshable.refresh();
        }
        if (updateDocumentIndexes()) {
            doUpdateDocumentIndexes(document);
        }
    }

    protected boolean updateDocumentIndexes() {
        Serializable flag = params.get(UPDATE_DOCUMENT_INDEX);
        return flag != null && flag.toString().equalsIgnoreCase("true");
    }

    @SuppressWarnings("UseSpecificCatch")
    protected void doUpdateDocumentIndexes(XComponent document) {
        XTextDocument xDocument = cast(XTextDocument.class, document);
        if (xDocument != null) {
            XDocumentIndexesSupplier indexSupplier = cast(
                    XDocumentIndexesSupplier.class, xDocument);
            XDocumentIndex index;

            XRefreshable xRefreshable = cast(XRefreshable.class, document);
            if (xRefreshable != null) {
                // This refresh operation solves issues with ToC update operations,
                // which could lead to bad page numbers in some scenarios (specific
                // hosts, specific documents, conversion to non-single-file document 
                // format like ODT - PDF not affected).
                // References:
                //   * http://www.oooforum.org/forum/viewtopic.phtml?t=7826
                //   * https://issues.apache.org/ooo/show_bug.cgi?id=29165
                xRefreshable.refresh();
            }

            if (indexSupplier != null) {
                XIndexAccess ia = indexSupplier.getDocumentIndexes();
                for (int i = 0; i < ia.getCount(); i++) {
                    Object idx;
                    try {
                        idx = ia.getByIndex(i);
                        index = cast(XDocumentIndex.class, idx);
                        if (index != null) {
                            index.update();
                        }
                    } catch (Exception e) {
                        throw new OfficeException("Update document index failed.", e);
                    }
                }
            }
        }
    }
}
