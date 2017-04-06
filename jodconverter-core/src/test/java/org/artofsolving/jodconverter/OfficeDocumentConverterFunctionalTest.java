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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.testng.annotations.Test;

@Test(groups = "functional")
public class OfficeDocumentConverterFunctionalTest {

    public void runAllPossibleConversions() throws IOException {
        OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
        OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();

        officeManager.start();
        try {
            File docs = new File("src/test/resources/documents");
            File[] files = docs.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !name.startsWith(".");
                }
            });
            for (File inputFile : files) {
                String inputExtension = FilenameUtils.getExtension(inputFile.getName());
                DocumentFormat inputFormat = formatRegistry.getFormatByExtension(inputExtension);
                assertNotNull(inputFormat, "unknown input format: " + inputExtension);
                Set<DocumentFormat> outputFormats = formatRegistry.getOutputFormats(inputFormat.getInputFamily());
                for (DocumentFormat outputFormat : outputFormats) {
                    // LibreOffice 4 fails natively on this one
                    if (inputFormat.getExtension().equals("odg") && outputFormat.getExtension().equals("svg")) {
                        System.out.println("-- skipping odg to svg test... ");
                        continue;
                    }
                    if (outputFormat.getExtension().equals("sxc")) {
                        System.out.println("-- skipping * to sxc test... ");
                        continue;
                    }
                    if (outputFormat.getExtension().equals("sxw")) {
                        System.out.println("-- skipping * to sxw test... ");
                        continue;
                    }
                    if (outputFormat.getExtension().equals("sxi")) {
                        System.out.println("-- skipping * to sxi test... ");
                        continue;
                    }
                    File outputFile = File.createTempFile("test", "." + outputFormat.getExtension());
                    outputFile.deleteOnExit();
                    System.out.printf("-- (file) converting %s to %s... ", inputFormat.getExtension(), outputFormat.getExtension());
                    converter.convert(inputFile, outputFile, outputFormat);
                    System.out.printf("done.%n");
                    assertTrue(outputFile.isFile() && outputFile.length() > 0);

                    File outputFileForStream = File.createTempFile("test-stream", "." + outputFormat.getExtension());
                    outputFileForStream.deleteOnExit();
                    System.out.printf("-- (stream) converting %s to %s... ", inputFormat.getExtension(), outputFormat.getExtension());
                    try (InputStream is = new FileInputStream(inputFile)) {
                        try (OutputStream os = new FileOutputStream(outputFileForStream)) {
                            converter.convert(is, inputFormat, os, outputFormat);
                        }
                    }
                    System.out.printf("done.%n");
                    assertTrue(outputFileForStream.isFile() && outputFileForStream.length() > 0);
                }
            }
        } finally {
            officeManager.stop();
        }
    }

}
