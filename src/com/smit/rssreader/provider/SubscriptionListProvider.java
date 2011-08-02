package com.smit.rssreader.provider;

import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.PacketExtension;

import com.smit.rssreader.extension.subscription.SubscriptionListExtension;

public class SubscriptionListProvider extends EmbeddedExtensionProvider{

	@Override
	protected PacketExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap,
			List<? extends PacketExtension> content) {
		return new SubscriptionListExtension(null, null, 1);
	}

}
