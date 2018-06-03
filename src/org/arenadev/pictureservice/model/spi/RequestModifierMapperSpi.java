package org.arenadev.pictureservice.model.spi;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.arenadev.pictureservice.model.InvocationBuilderGenerator;
import org.arenadev.pictureservice.model.RequestModifierMapper;
import org.arenadev.pictureservice.model.SimpleInvocationBuilderGenerator;

public class RequestModifierMapperSpi implements RequestModifierMapper {

	private static List<InvocationBuilderGeneratorSpi> transformerList = Arrays.asList(new PixivUriTransformer(), new NijieUriTransformer(), new TwImgUriTransformer());
	
	@Override
	public InvocationBuilderGenerator getUriTransformer(URI uri) {
		return transformerList.stream().filter(t -> t.canAdapted(uri)).map(t -> (InvocationBuilderGenerator) t).findFirst().orElse(new SimpleInvocationBuilderGenerator());
	}

}
