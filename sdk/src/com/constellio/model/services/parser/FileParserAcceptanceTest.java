package com.constellio.model.services.parser;

import static java.util.Arrays.asList;
import static junit.framework.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

import java.io.InputStream;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.constellio.data.io.streamFactories.StreamFactory;
import com.constellio.model.entities.Language;
import com.constellio.model.entities.records.ParsedContent;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.constellio.model.services.parser.FileParserException.FileParserException_CannotParse;
import com.constellio.model.services.parser.FileParserException.FileParserException_FileSizeExceedLimitForParsing;
import com.constellio.sdk.tests.ConstellioTest;

//@SlowTest
public class FileParserAcceptanceTest extends ConstellioTest {

	StreamFactory<InputStream> inputStreamFactory;

	private FileParser fileParser;

	@Test
	public void givenStreamOfDOCMimetypeWhenParsingThenValidParsedContentReturned()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFile.doc");
		long length = getLengthOf("testFile.doc");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getParsedContent()).contains("This is the content of").contains("a doc file");
		assertThat(parsedContent.getLanguage()).isEqualTo(Language.English.getCode());
		assertThat(parsedContent.getMimeType()).isEqualTo("application/msword");
		assertThat(parsedContent.getLength()).isEqualTo(22528L);
		assertThat(parsedContent.getProperties()).containsEntry("Company", "DocuLibre");
	}

	//@Test
	public void parse1000times()
			throws Exception {

		inputStreamFactory = getTestResourceInputStreamFactory("architecture_logicielle.pdf");
		long length = getLengthOf("testFile.doc");
		long start = new Date().getTime();
		for (int i = 0; i < 1000; i++) {
			System.out.println(i);
			fileParser.parse(inputStreamFactory, length);
		}
		System.out.println(new Date().getTime() - start);
	}

	//@Test
	public void detectMimetype1000times()
			throws Exception {

		inputStreamFactory = getTestResourceInputStreamFactory("architecture_logicielle.pdf");
		long length = getLengthOf("testFile.doc");
		long start = new Date().getTime();
		for (int i = 0; i < 10000; i++) {
			System.out.println(i);
			fileParser.detectMimetype(inputStreamFactory, "test.pdf");
		}
		System.out.println(new Date().getTime() - start);
	}

	@Test
	public void givenStreamOfDOCXMimetypeWhenParsingThenValidParsedContentReturned()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFile.docx");
		long length = getLengthOf("testFile.docx");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getParsedContent()).contains("This is the content of").contains("a docx file");
		assertThat(parsedContent.getLanguage()).isEqualTo(Language.English.getCode());
		assertThat(parsedContent.getMimeType())
				.isEqualTo("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		assertThat(parsedContent.getLength()).isEqualTo(13771L);
		assertThat(parsedContent.getProperties()).containsEntry("Title", "Document sans titre.docx");
	}

	@Test
	public void givenStreamOfHTMLMimetypeWhenParsingThenCorrectContentReturned()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFile.html");
		long length = getLengthOf("testFile.html");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getParsedContent()).contains("This is the content of").contains("a html file");
		assertThat(parsedContent.getLanguage()).isEqualTo(Language.English.getCode());
		assertThat(parsedContent.getMimeType()).startsWith("text/html;");
		assertThat(parsedContent.getProperties()).isEmpty();
	}

	@Test
	public void givenStreamOfPDFMimetypeWhenParsingThenCorrectContentReturned()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFile.pdf");
		long length = getLengthOf("testFile.pdf");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getParsedContent()).contains("This is the content of").contains("a pdf file");
		assertThat(parsedContent.getLanguage()).isEqualTo(Language.English.getCode());
		assertThat(parsedContent.getMimeType()).isEqualTo("application/pdf");
		assertThat(parsedContent.getLength()).isEqualTo(27171L);
		assertThat(parsedContent.getProperties()).containsEntry("Title", "Untitled");
	}

	@Test
	public void givenPasswordProtectedPDFFileThenReturnEmptyParsedContentWithUnknownLanguageAndPDFMimetype()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("passwordProtected.pdf");
		long length = getLengthOf("passwordProtected.pdf");

		try {

			fileParser.parse(inputStreamFactory, length);
			fail("Exception expected");
		} catch (FileParserException_CannotParse e) {
			assertThat(e.getDetectedMimetype()).isEqualTo("application/pdf");
		}

	}

	@Test
	public void givenStreamOfXLSMimetypeWhenParsingThenCorrectContentReturned()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFile.xls");
		long length = getLengthOf("testFile.xls");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getParsedContent()).contains("Feuille1").contains("This is the content of")
				.contains("the xsl file");
		assertThat(parsedContent.getLanguage()).isEqualTo(Language.English.getCode());
		assertThat(parsedContent.getMimeType()).isEqualTo("application/vnd.ms-excel");
		assertThat(parsedContent.getLength()).isEqualTo(23552L);
		assertThat(parsedContent.getProperties()).containsEntry("Title", "zeTitle");
		assertThat(parsedContent.getProperties()).containsEntry("List:Keywords", asList("zeKeywords"));
		assertThat(parsedContent.getProperties()).containsEntry("Comments", "zeComments");
		assertThat(parsedContent.getProperties()).containsEntry("Author", "zeAuthor");
	}

	@Test
	public void givenStreamOfXLSXMimetypeWhenParsingThenCorrectContentReturned()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFile.xlsx");
		long length = getLengthOf("testFile.xlsx");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getParsedContent()).contains("Sheet1").contains("This is the content of")
				.contains("the xslx file");
		assertThat(parsedContent.getLanguage()).isEqualTo(Language.English.getCode());
		assertThat(parsedContent.getMimeType()).isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		assertThat(parsedContent.getLength()).isEqualTo(8022L);
		assertThat(parsedContent.getProperties()).isEmpty();
	}

	@Test
	public void givenStreamOfXMLMimetypeWhenParsingThenCorrectContentReturned()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFile.xml");
		long length = getLengthOf("testFile.xml");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getParsedContent()).contains("This is the content of").contains("the xml file");
		assertThat(parsedContent.getLanguage()).isEqualTo(Language.English.getCode());
		assertThat(parsedContent.getMimeType()).startsWith("text/html;");
		assertThat(parsedContent.getProperties()).isEmpty();
	}

	@Test
	public void givenStreamOfDOCWhenParsingThenAllPropertiesAreCatch()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFileWithProperties.doc");
		long length = getLengthOf("testFileWithProperties.doc");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getProperties()).isNotEmpty();
		assertThatAllCommunPropertiesAreCatchIn(parsedContent);
		assertThatAllLessCommunPropertiesAreCatchIn(parsedContent);
	}

	@Test
	public void givenStreamOfDOCXWhenParsingThenAllPropertiesAreCatch()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFileWithProperties.docx");
		long length = getLengthOf("testFileWithProperties.docx");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getProperties()).isNotEmpty();
		assertThatAllCommunPropertiesAreCatchIn(parsedContent);
		assertThatAllLessCommunPropertiesAreCatchIn(parsedContent);
	}

	@Test
	public void givenStreamOfPDFWhenParsingThenAllPropertiesAreCatch()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFileWithProperties.pdf");
		long length = getLengthOf("testFileWithProperties.pdf");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getProperties()).isNotEmpty();
		assertThatAllCommunPropertiesAreCatchIn(parsedContent);
	}

	@Test
	public void givenStreamOfPPTWhenParsingThenAllPropertiesAreCatch()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFileWithProperties.ppt");
		long length = getLengthOf("testFileWithProperties.ppt");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getProperties()).isNotEmpty();
		assertThatAllCommunPropertiesAreCatchIn(parsedContent);
		assertThatAllLessCommunPropertiesAreCatchIn(parsedContent);
	}

	@Test
	public void givenStreamOfPPTXWhenParsingThenAllPropertiesAreCatch()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFileWithProperties.pptx");
		long length = getLengthOf("testFileWithProperties.pptx");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getProperties()).isNotEmpty();
		assertThatAllCommunPropertiesAreCatchIn(parsedContent);
		assertThatAllLessCommunPropertiesAreCatchIn(parsedContent);
	}

	@Test
	public void givenStreamOfXLSWhenParsingThenAllPropertiesAreCatch()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFileWithProperties.xls");
		long length = getLengthOf("testFileWithProperties.xls");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getProperties()).isNotEmpty();
		assertThatAllCommunPropertiesAreCatchIn(parsedContent);
		assertThatAllLessCommunPropertiesAreCatchIn(parsedContent);
	}

	@Test
	public void givenStreamOfXLSXWhenParsingThenAllPropertiesAreCatch()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testFileWithProperties.xlsx");
		long length = getLengthOf("testFileWithProperties.xlsx");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);
		assertThat(parsedContent.getProperties()).isNotEmpty();
		assertThatAllCommunPropertiesAreCatchIn(parsedContent);
		assertThatAllLessCommunPropertiesAreCatchIn(parsedContent);
	}

	@Test
	public void givenMessageWithAttachedDOCAndAttachedTextWhenParsingThenValidParsedContentReturned()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("testMessage.msg");
		long length = getLengthOf("testFile.docx");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getParsedContent()).contains("contenu");
		assertThat(parsedContent.getParsedContent()).contains("Microsoft word document");
		assertThat(parsedContent.getParsedContent()).contains("text document");
		assertThat(parsedContent.getProperties().get("Subject")).isEqualTo("objet");
		assertThat(parsedContent.getProperties().get("To"))
				.isEqualTo("a1@doculibre.com; a2@doculibre.com");
		assertThat(parsedContent.getProperties().get("CC"))
				.isEqualTo("c1@doculibre.com; c2@doculibre.com");
		assertThat(parsedContent.getProperties().get("BCC")).isEqualTo("b1@doculibre.com; b2@doculibre.com");
		assertThat(parsedContent.getMimeType())
				.isEqualTo("application/vnd.ms-outlook");
		assertThat(parsedContent.getLength()).isEqualTo(13771L);
	}

	@Test
	public void givenWord2003DocumentWithStylesThenStylesExtracted()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("DocumentWithStylesAndProperties.doc");
		long length = getLengthOf("DocumentWithStylesAndProperties.doc");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getProperties()).containsOnly(
				entry("Category", "category2"),
				entry("Comments", "comments2"),
				entry("Subject", "subject2"),
				entry("List:Keywords", asList("zeKeyword2", "anotherKeyword2")),
				entry("Manager", "manager2"),
				entry("Author", "author2"),
				entry("Company", "company2"),
				entry("Title", "title2")
		);

		assertThat(parsedContent.getMimeType())
				.isEqualTo("application/msword");
		assertThat(parsedContent.getStyles()).containsOnly(
				entry("titreofficiel", asList("The ring contract")),
				entry("nomdelacompagnie", asList("Frodon", "Bilbon")),
				entry("adressedelacompagnie", asList("Hobbiton, Shire")),
				entry("nomduclient", asList("Gandalf Leblanc")),
				entry("adresseduclient", asList("Somewhere, Terre du Milieu"))
		);

	}

	@Test
	public void givenWord2007DocumentWithStylesThenStylesExtracted()
			throws Exception {
		inputStreamFactory = getTestResourceInputStreamFactory("DocumentWithStylesAndProperties.docx");
		long length = getLengthOf("DocumentWithStylesAndProperties.docx");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getProperties()).containsOnly(
				entry("Category", "category2"),
				entry("Comments", "comments2"),
				entry("Subject", "subject2"),
				entry("List:Keywords", asList("zeKeyword2", "anotherKeyword2")),
				entry("Manager", "manager2"),
				entry("Author", "author2"),
				entry("Company", "company2"),
				entry("Title", "title2")
		);

		assertThat(parsedContent.getMimeType())
				.isEqualTo("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		assertThat(parsedContent.getStyles()).containsOnly(
				entry("titreofficiel", asList("The ring contract")),
				entry("nomdelacompagnie", asList("Frodon", "Bilbon")),
				entry("adressedelacompagnie", asList("Hobbiton, Shire")),
				entry("nomduclient", asList("Gandalf Leblanc")),
				entry("adresseduclient", asList("Somewhere, Terre du Milieu"))
		);

	}

	@Test
	public void whenParsingLargeFileNotExceedingFileSizeLimitThenParsed()
			throws Exception {
		givenConfig(ConstellioEIMConfigs.CONTENT_MAX_LENGTH_FOR_PARSING_IN_MEGAOCTETS, 2);
		inputStreamFactory = getTestResourceInputStreamFactory("testFileWithLargePictureOfEdouard.pptx");
		long length = getLengthOf("testFileWithLargePictureOfEdouard.pptx");

		ParsedContent parsedContent = fileParser.parse(inputStreamFactory, length);

		assertThat(parsedContent.getMimeType())
				.isEqualTo("application/vnd.openxmlformats-officedocument.presentationml.presentation");
		assertThat(parsedContent.getParsedContent()).contains("history of cats");

	}

	@Test
	public void whenParsingLargeFileExceedingFileSizeLimitThenParsed()
			throws Exception {
		givenConfig(ConstellioEIMConfigs.CONTENT_MAX_LENGTH_FOR_PARSING_IN_MEGAOCTETS, 1);
		inputStreamFactory = getTestResourceInputStreamFactory("testFileWithLargePictureOfEdouard.pptx");
		long length = getLengthOf("testFileWithLargePictureOfEdouard.pptx");

		try {
			fileParser.parse(inputStreamFactory, length);
			fail("FileParserException_FileSizeExceedLimitForParsing expected");

		} catch (FileParserException_FileSizeExceedLimitForParsing e) {
			assertThat(e.getDetectedMimetype()).isNull();
		}

	}

	private void assertThatAllCommunPropertiesAreCatchIn(ParsedContent parsedContent) {
		assertThat(parsedContent.getProperties()).containsEntry("Title", "Ze title");
		assertThat(parsedContent.getProperties())
				.containsEntry("List:Keywords", asList("Ze keyword1", "Ze keyword2", "Ze keyword 3"));
		assertThat(parsedContent.getProperties()).containsEntry("Author", "Ze author");
		assertThat(parsedContent.getProperties()).containsEntry("Subject", "Ze subject");
	}

	private void assertThatAllLessCommunPropertiesAreCatchIn(ParsedContent parsedContent) {
		assertThat(parsedContent.getProperties()).containsEntry("Company", "Ze company");
		assertThat(parsedContent.getProperties()).containsEntry("Category", "Ze category");
		assertThat(parsedContent.getProperties()).containsEntry("Manager", "Ze ultimate manager");
		assertThat(parsedContent.getProperties()).containsEntry("Comments", "Ze very useful comments Line2");
	}

	@Before
	public void setup() {
		fileParser = getModelLayerFactory().newFileParser();
	}

	private long getLengthOf(String resourceName) {
		return getTestResourceFile(resourceName).length();
	}
}
