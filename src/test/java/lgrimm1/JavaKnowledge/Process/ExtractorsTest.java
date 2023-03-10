package lgrimm1.JavaKnowledge.Process;

import lgrimm1.JavaKnowledge.Title.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.mockito.Mockito.when;

class ExtractorsTest {

	Extractors extractors = new Extractors();
	TitleRepository titleRepository;
	Formulas formulas;

	@BeforeEach
	void setUp() {
		formulas = Mockito.mock(Formulas.class);
		when(formulas.getConstant(Formulas.ConstantName.TAB_IN_SPACES))
				.thenReturn("TABINSPACES");
		when(formulas.getConstant(Formulas.ConstantName.SUPERLINE))
				.thenReturn("SUPERLINE");
		titleRepository = Mockito.mock(TitleRepository.class);
	}

	@Test
	void extractReferenceNoHeader() {
		String line = "=>Title Text";

		when(titleRepository.findByTitle("Title Text"))
				.thenReturn(Optional.of(new TitleEntity(
						1L,
						"Title Text",
						"title_text",
						1L,
						1L
				)));
		Assertions.assertEquals(
				"TABINSPACES" + "<a href=\"title_text.html\">See: Title Text</a></br>",
				extractors.extractReference(line, formulas, titleRepository)
		);

		when(titleRepository.findByTitle("Title Text"))
				.thenReturn(Optional.empty());
		Assertions.assertEquals(
				"TABINSPACES" + "See: Title Text</br>",
				extractors.extractReference(line, formulas, titleRepository)
		);
	}

	@Test
	void extractReferenceWithHeader() {
		String line = "=>Title Text;1.2. Header Word";

		when(titleRepository.findByTitle("Title Text"))
				.thenReturn(Optional.of(new TitleEntity(1L,
						"Title Text",
						"title_text",
						1L,
						1L
				)));
		Assertions.assertEquals(
				"TABINSPACES" + "<a href=\"title_text.html#1.2. Header Word\">See: Title Text / 1.2. Header Word</a></br>",
				extractors.extractReference(line, formulas, titleRepository)
		);

		when(titleRepository.findByTitle("Title Text"))
				.thenReturn(Optional.empty());
		Assertions.assertEquals(
				"TABINSPACES" + "See: Title Text / 1.2. Header Word</br>",
				extractors.extractReference(line, formulas, titleRepository)
		);
	}

	@Test
	void extractTable() {
		List<String> originalText = List.of(
				"||Header 1|Header 2|Header 3||",
				"||Cell 11|Cell 12|Cell 13||",
				"||Cell 21|Cell 22|Cell 23||"
		);
		List<String> expectedHtml = List.of(
				"TABINSPACES" + "<table class=\"table\">",
				"TABINSPACES" + "TABINSPACES" + "<tr class=\"table_element\">",
				"TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<th class=\"table_element\">Header 1</th>",
				"TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<th class=\"table_element\">Header 2</th>",
				"TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<th class=\"table_element\">Header 3</th>",
				"TABINSPACES" + "TABINSPACES" + "</tr>",
				"TABINSPACES" + "TABINSPACES" + "<tr class=\"table_element\">",
				"TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<td class=\"table_element\">Cell 11</td>",
				"TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<td class=\"table_element\">Cell 12</td>",
				"TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<td class=\"table_element\">Cell 13</td>",
				"TABINSPACES" + "TABINSPACES" + "</tr>",
				"TABINSPACES" + "TABINSPACES" + "<tr class=\"table_element\">",
				"TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<td class=\"table_element\">Cell 21</td>",
				"TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<td class=\"table_element\">Cell 22</td>",
				"TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<td class=\"table_element\">Cell 23</td>",
				"TABINSPACES" + "TABINSPACES" + "</tr>",
				"TABINSPACES" + "</table>"
		);
		Assertions.assertIterableEquals(expectedHtml, extractors.extractTable(originalText, formulas));
	}

	@Test
	void extractTitleWithEmptyText() {
		List<String> originalText = new ArrayList<>();
		Assertions.assertTrue(extractors.extractTitle(originalText, formulas).isEmpty());
	}

	@Test
	void extractTitleWithNoLegalTitle() {
		List<String> originalText = List.of(
				"abcdef",
				"",
				"xyz"
		);
		Assertions.assertTrue(extractors.extractTitle(originalText, formulas).isEmpty());
	}

	@Test
	void extractTitleWithLegalTitle() {
		List<String> originalText = List.of(
				"SUPERLINE",
				"Title Text",
				"SUPERLINE"
		);
		Assertions.assertEquals("Title Text".toUpperCase(), extractors.extractTitle(originalText, formulas));
	}
	
	@Test
	void extractExample() {
		List<String> originalText = List.of(
				"EXAMPLE FOR SOMETHING",
				"Line 1",
				"    Line 2"
		);
		List<String> expectedHtml = new ArrayList<>();
		expectedHtml.add("TABINSPACES" + "<h4>" + originalText.get(0) + "</h4>");
		expectedHtml.add("TABINSPACES" + "<table class=\"formatter_table\">");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "<tr>");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<td style=\"width: 85%\">");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "TABINSPACES" +
				"<textarea id=\"example01\" class=\"textarea\" onclick=\"element_to_full_size(this)\" readonly>");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "TABINSPACES" + 
				"Line 1" + "<br>");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "TABINSPACES" + 
				"    Line 2" + "<br>");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "</textarea>");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "</td>");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "<td style=\"width: 15%\">");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "TABINSPACES" +
				"<input type=\"button\" value=\"COPY\" class=\"button\" onclick=\"example_to_clipboard('example01')\" />");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "TABINSPACES" + "</td>");
		expectedHtml.add("TABINSPACES" + "TABINSPACES" + "</tr>");
		expectedHtml.add("TABINSPACES" + "</table>");

		Assertions.assertEquals(expectedHtml, extractors.extractExample(originalText, formulas));
	}
}