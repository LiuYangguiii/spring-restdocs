/*
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.hypermedia;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.testfixtures.OperationBuilder;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Tests for failures when rendering {@link LinksSnippet} due to missing or undocumented
 * links.
 *
 * @author Andy Wilkinson
 */
public class LinksSnippetFailureTests {

	@Rule
	public OperationBuilder operationBuilder = new OperationBuilder(TemplateFormats.asciidoctor());

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void undocumentedLink() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Links with the following relations were not" + " documented: [foo]"));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("foo", "bar")),
				Collections.<LinkDescriptor>emptyList()).document(this.operationBuilder.build());
	}

	@Test
	public void missingLink() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(
				equalTo("Links with the following relations were not" + " found in the response: [foo]"));
		new LinksSnippet(new StubLinkExtractor(), Arrays.asList(new LinkDescriptor("foo").description("bar")))
				.document(this.operationBuilder.build());
	}

	@Test
	public void undocumentedLinkAndMissingLink() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Links with the following relations were not"
				+ " documented: [a]. Links with the following relations were not" + " found in the response: [foo]"));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha")),
				Arrays.asList(new LinkDescriptor("foo").description("bar"))).document(this.operationBuilder.build());
	}

	@Test
	public void linkWithNoDescription() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("No description was provided for the link with rel 'foo' and no"
				+ " title was available from the link in the payload"));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("foo", "bar")),
				Arrays.asList(new LinkDescriptor("foo"))).document(this.operationBuilder.build());
	}

}
