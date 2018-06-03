package org.arenadev.pictureservice.event;

import java.time.Instant;

public class UndefinedEvent extends Event {

	private UndefinedEvent(Instant start, Instant end) {
		super(EventType.UNDEFINED, start, end, null);
	}
	
	public static UndefinedEvent genEvent(Instant start, Instant end) {
		return new UndefinedEvent(start, end);
	}

	@Override
	public boolean isError() {
		return false;
	}

}
