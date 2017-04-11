//
// JODConverter Document Formats Configuration
//
[
	{
		"extension":"pdf",
		"mediaType":"application/pdf",
		"name":"Portable Document Format",
		"storePropertiesByFamily":{"DRAWING":{
				"FilterName":"draw_pdf_Export"
			},"TEXT":{
				"FilterName":"writer_pdf_Export"
			},"SPREADSHEET":{
				"FilterName":"calc_pdf_Export"
			},"PRESENTATION":{
				"FilterName":"impress_pdf_Export"
			}
		}
	},
	{
		"extension":"swf",
		"mediaType":"application/x-shockwave-flash",
		"name":"Macromedia Flash",
		"storePropertiesByFamily":{"DRAWING":{
				"FilterName":"draw_flash_Export"
			},"PRESENTATION":{
				"FilterName":"impress_flash_Export"
			}
		}
	},
	{
		"extension":"html",
		"inputFamily":"TEXT",
		"mediaType":"text/html",
		"name":"HTML",
		"storePropertiesByFamily":{"DRAWING":{
				"FilterName":"draw_html_Export"
			},"TEXT":{
				"FilterName":"HTML (StarWriter)"
			},"SPREADSHEET":{
				"FilterName":"HTML (StarCalc)"
			},"PRESENTATION":{
				"FilterName":"impress_html_Export"
			}
		}
	},
	{
		"extension":"odt",
		"inputFamily":"TEXT",
		"mediaType":"application/vnd.oasis.opendocument.text",
		"name":"OpenDocument Text",
		"storePropertiesByFamily":{"TEXT":{
				"FilterName":"writer8"
			}
		}
	},
	{
		"extension":"sxw",
		"inputFamily":"TEXT",
		"mediaType":"application/vnd.sun.xml.writer",
		"name":"OpenOffice.org 1.0 Text Document"
	},
	{
		"extension":"doc",
		"inputFamily":"TEXT",
		"mediaType":"application/msword",
		"name":"Microsoft Word",
		"storePropertiesByFamily":{"TEXT":{
				"FilterName":"MS Word 97"
			}
		}
	},
	{
		"extension":"docx",
		"inputFamily":"TEXT",
		"mediaType":"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
		"name":"Microsoft Word 2007 XML",
		"storePropertiesByFamily":{"TEXT":{
				"FilterName":"MS Word 2007 XML"
			}
		}
	},
	{
		"extension":"rtf",
		"inputFamily":"TEXT",
		"mediaType":"text/rtf",
		"name":"Rich Text Format",
		"storePropertiesByFamily":{"TEXT":{
				"FilterName":"Rich Text Format"
			}
		}
	},
	{
		"extension":"wpd",
		"inputFamily":"TEXT",
		"mediaType":"application/wordperfect",
		"name":"WordPerfect"
	},
	{
		"extension":"txt",
		"inputFamily":"TEXT",
		"loadProperties":{
			"FilterName":"Text (encoded)",
			"FilterOptions":"utf8"
		},
		"mediaType":"text/plain",
		"name":"Plain Text",
		"storePropertiesByFamily":{"TEXT":{"$ref":"$[9].loadProperties"}
		}
	},
	{
		"extension":"ods",
		"inputFamily":"SPREADSHEET",
		"mediaType":"application/vnd.oasis.opendocument.spreadsheet",
		"name":"OpenDocument Spreadsheet",
		"storePropertiesByFamily":{"SPREADSHEET":{
				"FilterName":"calc8"
			}
		}
	},
	{
		"extension":"sxc",
		"inputFamily":"SPREADSHEET",
		"mediaType":"application/vnd.sun.xml.calc",
		"name":"OpenOffice.org 1.0 Spreadsheet"
	},
	{
		"extension":"xls",
		"inputFamily":"SPREADSHEET",
		"mediaType":"application/vnd.ms-excel",
		"name":"Microsoft Excel",
		"storePropertiesByFamily":{"SPREADSHEET":{
				"FilterName":"MS Excel 97"
			}
		}
	},
	{
		"extension":"xlsx",
		"inputFamily":"SPREADSHEET",
		"mediaType":"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
		"name":"Microsoft Excel 2007 XML",
		"storePropertiesByFamily":{"SPREADSHEET":{
				"FilterName":"Calc MS Excel 2007 XML"
			}
		}
	},
	{
		"extension":"csv",
		"inputFamily":"SPREADSHEET",
		"loadProperties":{
			"FilterName":"Text - txt - csv (StarCalc)",
			"FilterOptions":"44,34,0"
		},
		"mediaType":"text/csv",
		"name":"Comma Separated Values",
		"storePropertiesByFamily":{"SPREADSHEET":{"$ref":"$[14].loadProperties"}
		}
	},
	{
		"extension":"tsv",
		"inputFamily":"SPREADSHEET",
		"loadProperties":{
			"FilterName":"Text - txt - csv (StarCalc)",
			"FilterOptions":"9,34,0"
		},
		"mediaType":"text/tab-separated-values",
		"name":"Tab Separated Values",
		"storePropertiesByFamily":{"SPREADSHEET":{"$ref":"$[15].loadProperties"}
		}
	},
	{
		"extension":"odp",
		"inputFamily":"PRESENTATION",
		"mediaType":"application/vnd.oasis.opendocument.presentation",
		"name":"OpenDocument Presentation",
		"storePropertiesByFamily":{"PRESENTATION":{
				"FilterName":"impress8"
			}
		}
	},
	{
		"extension":"sxi",
		"inputFamily":"PRESENTATION",
		"mediaType":"application/vnd.sun.xml.impress",
		"name":"OpenOffice.org 1.0 Presentation"
	},
	{
		"extension":"ppt",
		"inputFamily":"PRESENTATION",
		"mediaType":"application/vnd.ms-powerpoint",
		"name":"Microsoft PowerPoint",
		"storePropertiesByFamily":{"PRESENTATION":{
				"FilterName":"MS PowerPoint 97"
			}
		}
	},
	{
		"extension":"pptx",
		"inputFamily":"PRESENTATION",
		"mediaType":"application/vnd.openxmlformats-officedocument.presentationml.presentation",
		"name":"Microsoft PowerPoint 2007 XML"
	},
	{
		"extension":"odg",
		"inputFamily":"DRAWING",
		"mediaType":"application/vnd.oasis.opendocument.graphics",
		"name":"OpenDocument Drawing",
		"storePropertiesByFamily":{"DRAWING":{
				"FilterName":"draw8"
			}
		}
	},
	{
		"extension":"svg",
		"mediaType":"image/svg+xml",
		"name":"Scalable Vector Graphics",
		"storePropertiesByFamily":{"DRAWING":{
				"FilterName":"draw_svg_Export"
			}
		}
	}
]
