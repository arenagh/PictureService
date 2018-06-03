package org.arenadev.pictureservice.event;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Event {
	
	private EventType type;
	
	private Instant start;
	
	private Instant end;
	
	@JsonIgnore
	private Exception cause;
	
	public Event(EventType type, Instant start, Instant end, Exception exception) {
		this.type = type;
		this.start = start;
		this.end = end;
		this.cause = exception;
	}

	public EventType getType() {
		return type;
	}

	public Instant getStart() {
		return start;
	}

	public Instant getEnd() {
		return end;
	}

	@JsonIgnore
	public Exception getCause() {
		return cause;
	}
	
	public abstract boolean isError();

}
