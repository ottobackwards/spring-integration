/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.xml.router;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.integration.message.GenericMessage;
import org.springframework.integration.message.MessagingException;
import org.springframework.integration.message.StringMessage;
import org.springframework.integration.xml.util.XmlTestUtil;
import org.springframework.xml.xpath.XPathExpression;
import org.springframework.xml.xpath.XPathExpressionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Jonas Partner
 */
public class XPathSingleChannelNameResolverTests {

	@Test
	public void testSimpleDocType() throws Exception {
		Document doc = XmlTestUtil.getDocumentForString("<doc type='one' />");
		XPathExpression expression = XPathExpressionFactory.createXPathExpression("/doc/@type");
		XPathSingleChannelNameResolver resolver = new XPathSingleChannelNameResolver(expression);
		String channelName = resolver.resolveChannelNames(new GenericMessage<Document>(doc))[0];
		assertEquals("Wrong channel name", "one", channelName);
	}

	@Test
	public void testSimpleStringDoc() throws Exception {
		XPathExpression expression = XPathExpressionFactory.createXPathExpression("/doc/@type");
		XPathSingleChannelNameResolver resolver = new XPathSingleChannelNameResolver(expression);
		String channelName = resolver.resolveChannelNames(new GenericMessage<String>("<doc type='one' />"))[0];
		assertEquals("Wrong channel name", "one", channelName);
	}

	@Test(expected = MessagingException.class)
	public void testNonNodePayload() throws Exception {
		XPathExpression expression = XPathExpressionFactory.createXPathExpression("/doc/@type");
		XPathSingleChannelNameResolver resolver = new XPathSingleChannelNameResolver(expression);
		resolver.resolveChannelNames(new StringMessage("test"));
	}

	@Test
	public void testNodePayload() throws Exception {
		XPathSingleChannelNameResolver resolver = new XPathSingleChannelNameResolver("./three/text()");
		Document testDocument = XmlTestUtil.getDocumentForString("<one><two><three>bob</three></two></one>");
		String[] channelNames = resolver.resolveChannelNames(new GenericMessage<Node>(testDocument
				.getElementsByTagName("two").item(0)));
		assertEquals("bob", channelNames[0]);
	}
	
	@Test(expected=MessagingException.class)
	public void testEvaluationReturnsEmptyString() throws Exception {
		XPathSingleChannelNameResolver resolver = new XPathSingleChannelNameResolver("/yellow");
		Document testDocument = XmlTestUtil.getDocumentForString("<one><two><three>bob</three></two></one>");
		resolver.resolveChannelNames(new GenericMessage<Node>(testDocument));
	
	}
}
