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
package org.artofsolving.jodconverter.document;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultDocumentFormatRegistry extends SimpleDocumentFormatRegistry {

    public DefaultDocumentFormatRegistry() {
        DocumentFormat pdf = new DocumentFormat("Portable Document Format", "pdf", "application/pdf");
        pdf.setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "writer_pdf_Export"));
        pdf.setStoreProperties(DocumentFamily.SPREADSHEET, Collections.singletonMap("FilterName", "calc_pdf_Export"));
        pdf.setStoreProperties(DocumentFamily.PRESENTATION, Collections.singletonMap("FilterName", "impress_pdf_Export"));
        pdf.setStoreProperties(DocumentFamily.DRAWING, Collections.singletonMap("FilterName", "draw_pdf_Export"));
        addFormat(pdf);

        DocumentFormat swf = new DocumentFormat("Macromedia Flash", "swf", "application/x-shockwave-flash");
        swf.setStoreProperties(DocumentFamily.PRESENTATION, Collections.singletonMap("FilterName", "impress_flash_Export"));
        swf.setStoreProperties(DocumentFamily.DRAWING, Collections.singletonMap("FilterName", "draw_flash_Export"));
        addFormat(swf);

		// disabled because it's not always available
        //DocumentFormat xhtml = new DocumentFormat("XHTML", "xhtml", "application/xhtml+xml");
        //xhtml.setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "XHTML Writer File"));
        //xhtml.setStoreProperties(DocumentFamily.SPREADSHEET, Collections.singletonMap("FilterName", "XHTML Calc File"));
        //xhtml.setStoreProperties(DocumentFamily.PRESENTATION, Collections.singletonMap("FilterName", "XHTML Impress File"));
        //addFormat(xhtml);
        DocumentFormat html = new DocumentFormat("HTML", "html", "text/html");
        // HTML is treated as Text when supplied as input, but as an output it is also
        // available for exporting Spreadsheet and Presentation formats
        html.setInputFamily(DocumentFamily.TEXT);
        html.setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "XHTML Writer File"));
        html.setStoreProperties(DocumentFamily.SPREADSHEET, Collections.singletonMap("FilterName", "XHTML Calc File"));
        html.setStoreProperties(DocumentFamily.PRESENTATION, Collections.singletonMap("FilterName", "XHTML Impress File"));
        html.setStoreProperties(DocumentFamily.DRAWING, Collections.singletonMap("FilterName", "XHTML Draw File"));
        addFormat(html);
        
        DocumentFormat htm = new DocumentFormat("HTML", "htm", "text/html");
        // HTML is treated as Text when supplied as input, but as an output it is also
        // available for exporting Spreadsheet and Presentation formats
        htm.setInputFamily(DocumentFamily.TEXT);
        htm.setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "XHTML Writer File"));
        htm.setStoreProperties(DocumentFamily.SPREADSHEET, Collections.singletonMap("FilterName", "XHTML Calc File"));
        htm.setStoreProperties(DocumentFamily.PRESENTATION, Collections.singletonMap("FilterName", "XHTML Impress File"));
        htm.setStoreProperties(DocumentFamily.DRAWING, Collections.singletonMap("FilterName", "XHTML Draw File"));
        addFormat(htm);

        DocumentFormat odt = new DocumentFormat("OpenDocument Text", "odt", "application/vnd.oasis.opendocument.text");
        odt.setInputFamily(DocumentFamily.TEXT);
        odt.setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "writer8"));
        addFormat(odt);

        DocumentFormat sxw = new DocumentFormat("OpenOffice.org 1.0 Text Document", "sxw", "application/vnd.sun.xml.writer");
        sxw.setInputFamily(DocumentFamily.TEXT);
        addFormat(sxw);

        DocumentFormat doc = new DocumentFormat("Microsoft Word", "doc", "application/msword");
        doc.setInputFamily(DocumentFamily.TEXT);
        doc.setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "MS Word 97"));
        addFormat(doc);

        DocumentFormat docx = new DocumentFormat("Microsoft Word 2007 XML", "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        docx.setInputFamily(DocumentFamily.TEXT);
        docx.setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "MS Word 2007 XML"));
        addFormat(docx);

        DocumentFormat rtf = new DocumentFormat("Rich Text Format", "rtf", "text/rtf");
        rtf.setInputFamily(DocumentFamily.TEXT);
        rtf.setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "Rich Text Format"));
        addFormat(rtf);

        DocumentFormat wpd = new DocumentFormat("WordPerfect", "wpd", "application/wordperfect");
        wpd.setInputFamily(DocumentFamily.TEXT);
        addFormat(wpd);

        DocumentFormat txt = new DocumentFormat("Plain Text", "txt", "text/plain");
        txt.setInputFamily(DocumentFamily.TEXT);
        Map<String, Object> txtLoadAndStoreProperties = new LinkedHashMap<>();
        txtLoadAndStoreProperties.put("FilterName", "Text (encoded)");
        txtLoadAndStoreProperties.put("FilterOptions", "utf8");
        txt.setLoadProperties(txtLoadAndStoreProperties);
        txt.setStoreProperties(DocumentFamily.TEXT, txtLoadAndStoreProperties);
        addFormat(txt);

        DocumentFormat wikitext = new DocumentFormat("MediaWiki wikitext", "wiki", "text/x-wiki");
        wikitext.setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "MediaWiki"));
        //addFormat(wikitext);

        DocumentFormat ods = new DocumentFormat("OpenDocument Spreadsheet", "ods", "application/vnd.oasis.opendocument.spreadsheet");
        ods.setInputFamily(DocumentFamily.SPREADSHEET);
        ods.setStoreProperties(DocumentFamily.SPREADSHEET, Collections.singletonMap("FilterName", "calc8"));
        addFormat(ods);

        DocumentFormat sxc = new DocumentFormat("OpenOffice.org 1.0 Spreadsheet", "sxc", "application/vnd.sun.xml.calc");
        sxc.setInputFamily(DocumentFamily.SPREADSHEET);
        addFormat(sxc);

        DocumentFormat xls = new DocumentFormat("Microsoft Excel", "xls", "application/vnd.ms-excel");
        xls.setInputFamily(DocumentFamily.SPREADSHEET);
        xls.setStoreProperties(DocumentFamily.SPREADSHEET, Collections.singletonMap("FilterName", "MS Excel 97"));
        addFormat(xls);

        DocumentFormat xlsx = new DocumentFormat("Microsoft Excel 2007 XML", "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        xlsx.setInputFamily(DocumentFamily.SPREADSHEET);
        xlsx.setStoreProperties(DocumentFamily.SPREADSHEET, Collections.singletonMap("FilterName", "Calc MS Excel 2007 XML"));
        addFormat(xlsx);

        DocumentFormat csv = new DocumentFormat("Comma Separated Values", "csv", "text/csv");
        csv.setInputFamily(DocumentFamily.SPREADSHEET);
        Map<String, Object> csvLoadAndStoreProperties = new LinkedHashMap<>();
        csvLoadAndStoreProperties.put("FilterName", "Text - txt - csv (StarCalc)");
        csvLoadAndStoreProperties.put("FilterOptions", "44,34,0");  // Field Separator: ','; Text Delimiter: '"' 
        csv.setLoadProperties(csvLoadAndStoreProperties);
        csv.setStoreProperties(DocumentFamily.SPREADSHEET, csvLoadAndStoreProperties);
        addFormat(csv);

        DocumentFormat tsv = new DocumentFormat("Tab Separated Values", "tsv", "text/tab-separated-values");
        tsv.setInputFamily(DocumentFamily.SPREADSHEET);
        Map<String, Object> tsvLoadAndStoreProperties = new LinkedHashMap<>();
        tsvLoadAndStoreProperties.put("FilterName", "Text - txt - csv (StarCalc)");
        tsvLoadAndStoreProperties.put("FilterOptions", "9,34,0");  // Field Separator: '\t'; Text Delimiter: '"' 
        tsv.setLoadProperties(tsvLoadAndStoreProperties);
        tsv.setStoreProperties(DocumentFamily.SPREADSHEET, tsvLoadAndStoreProperties);
        addFormat(tsv);

        DocumentFormat odp = new DocumentFormat("OpenDocument Presentation", "odp", "application/vnd.oasis.opendocument.presentation");
        odp.setInputFamily(DocumentFamily.PRESENTATION);
        odp.setStoreProperties(DocumentFamily.PRESENTATION, Collections.singletonMap("FilterName", "impress8"));
        addFormat(odp);

        DocumentFormat sxi = new DocumentFormat("OpenOffice.org 1.0 Presentation", "sxi", "application/vnd.sun.xml.impress");
        sxi.setInputFamily(DocumentFamily.PRESENTATION);
        addFormat(sxi);

        DocumentFormat ppt = new DocumentFormat("Microsoft PowerPoint", "ppt", "application/vnd.ms-powerpoint");
        ppt.setInputFamily(DocumentFamily.PRESENTATION);
        ppt.setStoreProperties(DocumentFamily.PRESENTATION, Collections.singletonMap("FilterName", "MS PowerPoint 97"));
        addFormat(ppt);

        DocumentFormat pptx = new DocumentFormat("Microsoft PowerPoint 2007 XML", "pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        pptx.setInputFamily(DocumentFamily.PRESENTATION);
        addFormat(pptx);

        DocumentFormat odg = new DocumentFormat("OpenDocument Drawing", "odg", "application/vnd.oasis.opendocument.graphics");
        odg.setInputFamily(DocumentFamily.DRAWING);
        odg.setStoreProperties(DocumentFamily.DRAWING, Collections.singletonMap("FilterName", "draw8"));
        addFormat(odg);

        DocumentFormat svg = new DocumentFormat("Scalable Vector Graphics", "svg", "image/svg+xml");
        svg.setStoreProperties(DocumentFamily.DRAWING, Collections.singletonMap("FilterName", "draw_svg_Export"));
        addFormat(svg);
    }

}
