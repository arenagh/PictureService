package org.arenadev.pictureservice.event;

import java.util.UUID;

public abstract class EventHandler {
	
	protected UUID uuid;
	
	public EventHandler() {
		uuid =UUID.randomUUID();
	}
	
	public abstract void handle(Event ev);
	
	public abstract void start(String str);
	
	public abstract void end(String str);

}
