/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.artofsolving.jodconverter.office;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;

/**
 *
 * @author Administrator
 */
public class ComplicateDocToHtml {

    public static void main(String[] args) throws IOException {
        OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
        OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();
        officeManager.start();
        try {
            File file = new File("target/test-result/html/index.html");
            FileUtils.forceMkdirParent(file);
            converter.convert(ComplicateDocToHtml.class.getResourceAsStream("/complicate-documents/test.doc"), formatRegistry.getFormatByExtension("doc"), file);
        } finally {
            officeManager.stop();
        }
    }
}
