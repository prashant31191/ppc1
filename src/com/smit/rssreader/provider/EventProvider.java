package com.smit.rssreader.provider;

import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.PacketExtension;

import com.smit.rssreader.extension.notification.ItemsExtension;
import com.smit.rssreader.extension.notification.StatusExtension;
import com.smit.rssreader.extension.notification.SuperfeedrEventExtension;


public class EventProvider extends EmbeddedExtensionProvider
{
	@Override
	protected PacketExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attMap, List<? extends PacketExtension> content)
	{
		return new SuperfeedrEventExtension((StatusExtension)content.get(0), (ItemsExtension)content.get(1));
	}
}
