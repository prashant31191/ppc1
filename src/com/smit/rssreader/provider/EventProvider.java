package com.smit.rssreader.provider;

import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.provider.EmbeddedExtensionProvider;

import com.smit.rssreader.extension.notification.ItemsExtension;
import com.smit.rssreader.extension.notification.SuperfeedrEventExtension;


public class EventProvider extends EmbeddedExtensionProvider
{
	@Override
	protected PacketExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attMap, List<? extends PacketExtension> content)
	{
		DefaultPacketExtension channel = (DefaultPacketExtension) content.get(0);
		return new SuperfeedrEventExtension(channel.getValue("title"), (ItemsExtension)content.get(1));
	}
}
