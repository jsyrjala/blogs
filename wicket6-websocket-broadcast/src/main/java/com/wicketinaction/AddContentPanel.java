package com.wicketinaction;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.WebSocketRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddContentPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(AddContentPanel.class);

	public AddContentPanel(String id) {
		super(id);
		setOutputMarkupId(true);
	}

	protected void onInitialize() {
		super.onInitialize();
		final Label latestMessage = new Label("latestMessage",
				new Model<String>(""));
		add(latestMessage.setOutputMarkupId(true));
		add(new WebSocketBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConnect(ConnectedMessage message) {
				super.onConnect(message);
				log.info("Client connected");
			}

			@Override
			protected void onMessage(WebSocketRequestHandler handler,
					TextMessage message) {
				log.info("Received message {}", message.getText());
				FeedItem feedItem = new FeedItem(message.getText());
				new WebSocketPushBroadcaster().broadcastAll(Application.get(),
						feedItem);
				latestMessage.setDefaultModelObject(feedItem).modelChanged();
				handler.add(latestMessage);
			}

			@Override
			public void onException(Component component,
					RuntimeException exception) {
				log.warn("Got exception", exception);
			}
		});
	};

}
