package com.smit.rssreader;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.ProviderManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.smit.rssreader.extension.notification.SuperfeedrEventExtension;
import com.smit.rssreader.extension.subscription.SubUnSubExtension;
import com.smit.rssreader.iqrequest.SubUnsubIQ;
import com.smit.rssreader.provider.EventProvider;
import com.smit.rssreader.provider.ItemProvider;
import com.smit.rssreader.provider.ItemsProvider;
import com.smit.rssreader.provider.TitleProvider;

public class InteractiveServer {
	private final String SUBSCRIBE = "sub";
	private final String UNSUBSCRIBE = "unsub";

	private XMPPConnection connection;
	private String jid;
	private String server;
	private Context context;
	// private boolean success;
	private ArrayList<OnNotificationHandler> onNotificationHandlers = new ArrayList<OnNotificationHandler>();
	private Map<String, List<String>> subUnsubFeed = new HashMap<String, List<String>>();
	private static DateFormat m_ISO8601Local = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss");

	public InteractiveServer(XMPPConnection conn, final String jid, final String server,
			Context context){
        this.connection = conn;
		this.jid = jid;
		this.server = server;
		this.context = context;
		registerExtensionProvider();
		connection.addPacketListener(new InteractivePacketListener(),
				new OrFilter(new PacketTypeFilter(Message.class),
						new PacketTypeFilter(IQ.class)));
	}

	public static Date convertDate(final String date) {
		try {
			return m_ISO8601Local.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	// public boolean getSuccess(){
	// return success;
	// }
	//	
	// public void setSuccess(boolean flag){
	// this.success = flag;
	// }

	public void addOnNotificationHandler(final OnNotificationHandler handler) {
		if (handler != null && !onNotificationHandlers.contains(handler)) {
			onNotificationHandlers.add(handler);
		}
	}

	/**
	 * 
	 * @param event
	 */
	private void fireOnNotificationHandlers(final SuperfeedrEventExtension event) {
		for (OnNotificationHandler handler : onNotificationHandlers) {
			handler.onNotification(event);
		}
	}

	/**
	 * Try to remove the specified handler from the list of notification handler
	 * 
	 * @param handler
	 *            the notification handler to be removed from the list
	 */
	public void removeOnNotificationHandler(final OnNotificationHandler handler) {
		onNotificationHandlers.remove(handler);
	}

	private void subUnsubscribe(final SubUnSubExtension subUnsubscription,
			List<String> list) {
		String str = subUnsubscription.toXML();
		Log.i("订阅请求包：", "-------" + str);
		SubUnsubIQ iq = new SubUnsubIQ(str);
		//iq.setTo(server);
		iq.setType(Type.SET);
		connection.sendPacket(iq);
		subUnsubFeed.put(iq.getPacketID(), list);
	}

	/**
	 * Call this method to add subscription to your easylauncher account. The
	 * passed URL must be well formatted and must be valid
	 * 
	 * @param feedUrls
	 *            the list of feeds you want to add to your superfeedr account
	 * @param handler
	 *            the callback
	 */
	public void subscribe(List<URL> feedUrls) {
		List<String> list = new ArrayList<String>();
		list.add(feedUrls.get(0).toString());
		list.add(SUBSCRIBE);
		subUnsubscribe(new SubUnSubExtension(feedUrls, jid,
				SubUnSubExtension.TYPE_SUBSCRIPTION), list);
	}

	public void unsubscribe(List<URL> feedUrls) {
		List<String> list = new ArrayList<String>();
		list.add(feedUrls.get(0).toString());
		list.add(UNSUBSCRIBE);
		subUnsubscribe(new SubUnSubExtension(feedUrls, jid,
				SubUnSubExtension.TYPE_UNSUBSCRIPTION), list);
	}

	// 注册自定义的provider
	private void registerExtensionProvider() {
		ProviderManager pm = ProviderManager.getInstance();
		pm.addExtensionProvider("event",
				"smit:pubsub:notification", new EventProvider());


		pm.addExtensionProvider("title",
				"smit:pubsub:notification", new TitleProvider());

		pm.addExtensionProvider("items",
				"smit:pubsub:notification", new ItemsProvider());

		pm.addExtensionProvider("item", "smit:pubsub:notification",
				new ItemProvider());

	}

	private class InteractivePacketListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			// TODO Auto-generated method stub
			Log.i("收到服务器回应的包消息--------", "NotificationPacketListener.processPacket()...");
			Log.i("收到的包内容---------", "packet.toXML()=" + packet.toXML());

			if (packet instanceof Message) {
				fireOnNotificationHandlers((SuperfeedrEventExtension) ((Message) packet)
						.getExtension(SuperfeedrEventExtension.NAMESPACE));
			} else {
				IQ response = (IQ)packet;
				String packetID = packet.getPacketID();
				if (subUnsubFeed.get(packetID) != null) {
					String feed = subUnsubFeed.get(packetID).get(0);
					String flag = subUnsubFeed.get(packetID).get(1);
					subUnsubFeed.remove(packetID);
					//XMPPError error = packet.getError();
					if (response.getType()==IQ.Type.ERROR) {
						// setSuccess(true);
						// 发送广播通知RSSReaderActivity数据有更新
						Intent i = new Intent(RssReaderConstant.IQRESPONSENO);
						Bundle bundle = new Bundle();
						bundle.putString("FEEDURL", feed);
						bundle.putString("SUBUNSUB", flag);
						i.putExtras(bundle);
						context.sendBroadcast(i);
						// handler1.onError(builder.toString());
					} else {
						// setSuccess(false);
						Intent i = new Intent(RssReaderConstant.IQRESPONSEYES);
						Bundle bundle = new Bundle();
						bundle.putString("FEEDURL", feed);
						bundle.putString("SUBUNSUB", flag);
						i.putExtras(bundle);
						context.sendBroadcast(i);
					}
				}
			}
		}

	}

}
