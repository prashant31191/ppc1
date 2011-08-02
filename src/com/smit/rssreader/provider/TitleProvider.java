package com.smit.rssreader.provider;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class TitleProvider implements PacketExtensionProvider{

	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		DefaultPacketExtension extension =  new DefaultPacketExtension(parser.getName(), parser.getNamespace());
		extension.setValue("title", parser.nextText());   //获取频道的标题
		return extension;
	}

}
