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

import java.util.Date;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import com.smit.rssreader.InteractiveServer;
import com.smit.rssreader.extension.notification.ItemExtension;


public class ItemProvider implements PacketExtensionProvider{
	
      public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		
		String name = parser.getName();
		
		String title = null;
		String summary = null;
		String link = null;
		String id = null;
		Date published = null;
		
		int tag = parser.next();
		
		while (!name.equals(parser.getName())){
			if (tag == XmlPullParser.START_TAG){
				if ("title".equals(parser.getName())){
					parser.next();
					title = parser.getText();
				}else if ("summary".equals(parser.getName())){
					parser.next();
					summary = parser.getText();
				}else if ("link".equals(parser.getName())){
					link = parser.getAttributeValue(null, "href");
				}else if ("id".equals(parser.getName())){
					parser.next();
					id = parser.getText();
				} else if ("published".equals(parser.getName())){
					parser.next();
					published = InteractiveServer.convertDate(parser.getText());
				} 
			}
			tag = parser.next();
		}
		return new ItemExtension(title, summary, link, id,published);
	}


}
