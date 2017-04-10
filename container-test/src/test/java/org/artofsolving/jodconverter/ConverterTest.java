package org.artofsolving.jodconverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author cxj
 */
@RunWith(Arquillian.class)
public class ConverterTest {

    private static final String[] TEST_FILES = new String[]{
        "test.doc",
        "test.html",
        "test.odg",
        "test.odp",
        "test.ods",
        "test.odt",
        "test.ppt",
        "test.rtf",
        "test.sxc",
        "test.sxi",
        "test.sxw",
        "test.txt",
        "test.xls"
    };

    @Deployment
    public static WebArchive createDeployment() {
        PomEquippedResolveStage resolver = Maven.configureResolver().withClassPathResolution(false).workOffline().loadPomFromFile("pom.xml");
        JavaArchive[] jodArchives = resolver.resolve("org.artofsolving.jodconverter:jodconverter-core:?").withTransitivity().as(JavaArchive.class);
        WebArchive archive = ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(jodArchives)
                .addAsResource("jodconverter.properties")
                .addAsResource("documents")
                .setWebXML("web.xml");
        System.out.println(archive.toString(Formatters.VERBOSE));
        return archive;
    }

    @Test
    public void testOfficeConverter() throws FileNotFoundException, IOException, URISyntaxException {
        OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
        OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();

        officeManager.start();
        try {
            for (String inputFile : TEST_FILES) {
                String inputExtension = FilenameUtils.getExtension(inputFile);
                DocumentFormat inputFormat = formatRegistry.getFormatByExtension(inputExtension);
                Assert.assertNotNull("unknown input format: " + inputExtension, inputFormat);
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
                    try (InputStream is = getClass().getResourceAsStream("/documents/" + inputFile)){
                        converter.convert(is, inputFormat, outputFile, outputFormat);
                    }
                    System.out.printf("done.%n");
                    Assert.assertTrue(outputFile.isFile() && outputFile.length() > 0);
                }
            }
        } finally {
            officeManager.stop();
        }
    }
}
