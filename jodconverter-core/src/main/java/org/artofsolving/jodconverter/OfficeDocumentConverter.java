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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;

public class OfficeDocumentConverter {

    private final static Logger LOGGER = Logger.getLogger(OfficeDocumentConverter.class.getName());
    private final OfficeManager officeManager;

    private final DocumentFormatRegistry formatRegistry;

    private Map<String, ?> defaultLoadProperties = createDefaultLoadProperties();

    public OfficeDocumentConverter(OfficeManager officeManager) {
        this(officeManager, new DefaultDocumentFormatRegistry());
    }

    public OfficeDocumentConverter(OfficeManager officeManager,
            DocumentFormatRegistry formatRegistry) {
        this.officeManager = officeManager;
        this.formatRegistry = formatRegistry;
    }

    private Map<String, Object> createDefaultLoadProperties() {
        Map<String, Object> loadProperties = new HashMap<>();
        loadProperties.put("Hidden", true);
        loadProperties.put("ReadOnly", true);
        return loadProperties;
    }

    public void setDefaultLoadProperties(Map<String, ?> defaultLoadProperties) {
        this.defaultLoadProperties = defaultLoadProperties;
    }

    public DocumentFormatRegistry getFormatRegistry() {
        return formatRegistry;
    }

    public void convert(File inputFile, File outputFile) {
        convert(inputFile, null, outputFile, null, null);
    }

    public void convert(File inputFile, File outputFile, DocumentFormat outputFormat) {
        convert(inputFile, null, outputFile, outputFormat, null);
    }

    public void convert(File inputFile, DocumentFormat inputFormat,
            File outputFile, DocumentFormat outputFormat,
            Map<String, Serializable> params) {
        if (inputFormat == null) {
            String extension = FilenameUtils.getExtension(inputFile.getName());
            inputFormat = formatRegistry.getFormatByExtension(extension);
        }
        if (outputFormat == null) {
            String extension = FilenameUtils.getExtension(outputFile.getName());
            outputFormat = formatRegistry.getFormatByExtension(extension);
        }
        StandardFileBasedConversionTask conversionTask = new StandardFileBasedConversionTask(
                inputFile, inputFormat, outputFile, outputFormat, params);
        conversionTask.setDefaultLoadProperties(defaultLoadProperties);
        officeManager.execute(conversionTask);
    }

    public void convert(File inputFile, OutputStream os, DocumentFormat outputFormat) {
        convert(inputFile, null, os, outputFormat, null);
    }

    public void convert(File inputFile, DocumentFormat inputFormat, OutputStream os, DocumentFormat outputFormat) {
        convert(inputFile, inputFormat, os, outputFormat, null);
    }

    public void convert(File inputFile, DocumentFormat inputFormat, OutputStream os, DocumentFormat outputFormat, Map<String, Serializable> params) {
        String tmpName = UUID.randomUUID().toString();
        File tmpOutputFile = null;
        try {
            tmpOutputFile = File.createTempFile(tmpName + "-output", "." + outputFormat.getExtension());
            convert(inputFile, inputFormat, tmpOutputFile, outputFormat, params);
            FileUtils.copyFile(tmpOutputFile, os);
        } catch (IOException e) {
            throw new OfficeException("Convert failed!", e);
        } finally {
            if (tmpOutputFile != null && tmpOutputFile.exists()) {
                if (!tmpOutputFile.delete()) {
                    LOGGER.log(Level.WARNING, "The Temp file {0} delete failed.", tmpOutputFile.getAbsoluteFile());
                }
            }
        }
    }

    public void convert(InputStream is, DocumentFormat inputFormat, File outputFile) {
        convert(is, inputFormat, outputFile, null, null);
    }

    public void convert(InputStream is, DocumentFormat inputFormat, File outputFile, DocumentFormat outputFormat) {
        convert(is, inputFormat, outputFile, outputFormat, null);
    }

    public void convert(InputStream is, DocumentFormat inputFormat, File outputFile, DocumentFormat outputFormat, Map<String, Serializable> params) {
        String tmpName = UUID.randomUUID().toString();
        File tmpInputFile = null;
        try {
            tmpInputFile = File.createTempFile(tmpName + "-input", "." + inputFormat.getExtension());
            FileUtils.copyToFile(is, tmpInputFile);
            convert(tmpInputFile, inputFormat, outputFile, outputFormat, params);
        } catch (IOException e) {
            throw new OfficeException("Convert failed!", e);
        } finally {
            if (tmpInputFile != null && tmpInputFile.exists()) {
                if (!tmpInputFile.delete()) {
                    LOGGER.log(Level.WARNING, "The Temp file {0} delete failed.", tmpInputFile.getAbsoluteFile());
                }
            }
        }
    }

    public void convert(InputStream is, DocumentFormat inputFormat, OutputStream os, DocumentFormat outputFormat, Map<String, Serializable> params) {
        String tmpName = UUID.randomUUID().toString();
        File tmpInputFile = null, tmpOutputFile = null;
        try {
            tmpInputFile = File.createTempFile(tmpName + "-input", "." + inputFormat.getExtension());
            tmpOutputFile = File.createTempFile(tmpName + "-output", "." + outputFormat.getExtension());
            FileUtils.copyToFile(is, tmpInputFile);
            convert(tmpInputFile, inputFormat, tmpOutputFile, outputFormat, params);
            FileUtils.copyFile(tmpOutputFile, os);
        } catch (IOException e) {
            throw new OfficeException("Convert failed!", e);
        } finally {
            if (tmpInputFile != null && tmpInputFile.exists()) {
                if (!tmpInputFile.delete()) {
                    LOGGER.log(Level.WARNING, "The Temp file {0} delete failed.", tmpInputFile.getAbsoluteFile());
                }
            }
            if (tmpOutputFile != null && tmpOutputFile.exists()) {
                if (!tmpOutputFile.delete()) {
                    LOGGER.log(Level.WARNING, "The Temp file {0} delete failed.", tmpOutputFile.getAbsoluteFile());
                }
            }
        }
    }

    public void convert(InputStream is, DocumentFormat inputFormat, OutputStream os, DocumentFormat outputFormat) {
        convert(is, inputFormat, os, outputFormat, null);
    }
}
