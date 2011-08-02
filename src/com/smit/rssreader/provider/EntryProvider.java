package com.smit.rssreader.provider;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import com.smit.rssreader.InteractiveServer;
import com.smit.rssreader.extension.notification.EntryExtension;

import java.util.Date;

public class EntryProvider implements PacketExtensionProvider{

	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		
		String name = parser.getName();
		
		String id = null;
		String link = null;
		String linkType = null;
		Date published = null;
        Date updated = null;
		String summary = null;
		String title = null;
        String content = null;
		
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
					linkType = parser.getAttributeValue(null, "type");
				}else if ("id".equals(parser.getName())){
					parser.next();
					id = parser.getText();
				} else if ("published".equals(parser.getName())){
					parser.next();
					published = InteractiveServer.convertDate(parser.getText());
				} else if ("content".equals(parser.getName())) {
                    parser.next();
                    content = parser.getText();
                } else if ("updated".equals(parser.getName())){
					parser.next();
					updated = InteractiveServer.convertDate(parser.getText());
				}
			}
			tag = parser.next();
		}
		return new EntryExtension(id, link, linkType, published, updated, summary, title, content);
	}

}
