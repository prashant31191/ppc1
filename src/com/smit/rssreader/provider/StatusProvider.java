/**
 * Copyright (c) 2009 julien
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.smit.rssreader.provider;

import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.PacketExtension;

import com.smit.rssreader.InteractiveServer;
import com.smit.rssreader.extension.notification.HttpExtension;
import com.smit.rssreader.extension.notification.StatusExtension;

public class StatusProvider extends EmbeddedExtensionProvider {

	@Override
	protected PacketExtension createReturnExtension(String currentElement,
			String currentNamespace, Map<String, String> attributeMap,
			List<? extends PacketExtension> content) {
		String feedURL = attributeMap.get("feed");
		DefaultPacketExtension nextFetch = ((DefaultPacketExtension) content
				.get(1));
		DefaultPacketExtension channel = ((DefaultPacketExtension) content
				.get(2));
		return new StatusExtension(feedURL, InteractiveServer
				.convertDate(nextFetch.getValue("next")),
				(HttpExtension) content.get(0), channel.getValue("title"));
	}

}
