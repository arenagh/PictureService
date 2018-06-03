package org.arenadev.pictureservice.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.arenadev.pictureservice.util.ObjectMapperFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonEventHandler extends EventHandler {
	
	private static final String LOG_FILE = "/var/log/pictureservice/pictureservice.log"; // TODO to env...
	
	ObjectMapper mapper;
	
	Logger logger;

	public CommonEventHandler() {
		mapper = ObjectMapperFactory.newObjectMapper();

		logger = Logger.getLogger("org.arenadev.pictureservice");
		try {
			logger.setLevel(Level.INFO);
			
			Handler handler = new FileHandler(LOG_FILE);
			handler.setFormatter(new SimpleFormatter());
			
			logger.addHandler(handler);
			
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void handle(Event ev) {
		Level logLevel = (ev.isError()) ? Level.WARNING : Level.INFO;
		try {
			logger.log(logLevel, mapper.writeValueAsString(ev));
		} catch (JsonProcessingException e) {
			logger.log(logLevel, "fail make log for " + ev.getType().toString());
		};
		Exception e = ev.getCause();
		if (e != null) {
			List<String> causeStrings = getCauseStrings(e);
			causeStrings.forEach(str -> logger.log(logLevel, "[cause]: " + str));
			logger.log(logLevel, e.getMessage(), e);
		}
	}

	private List<String> getCauseStrings(Throwable cause) {
		List<String> strings = new ArrayList<>();
		String str = cause.getClass().getName();
		String message = cause.getMessage();
		if ((message != null) && (!message.isEmpty())) {
			str = message + "(" + str +")";
		}
		strings.add(str);
		if (cause.getCause() != null) {
			strings.addAll(getCauseStrings(cause.getCause()));
		}
		
		return strings;
	}

	@Override
	public void start(String str) {
		logger.log(Level.INFO, "[start]: " + str);
	}

	@Override
	public void end(String str) {
		logger.log(Level.INFO, "[end]: " + str);
	}
}
