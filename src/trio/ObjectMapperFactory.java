package trio;


import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import trio.model.field.Field;
import trio.model.field.FieldImpl;
import trio.model.game.Game;
import trio.model.game.GameImpl;


public final class ObjectMapperFactory {
	private ObjectMapperFactory() {
	}
	
	public static ObjectMapper createMapper() {
		ObjectMapper               mapper   = new ObjectMapper();
		SimpleModule               module   = new SimpleModule("TrioModel", Version.unknownVersion());
		SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
		resolver.addMapping(Field.class, FieldImpl.class);
		resolver.addMapping(Game.class, GameImpl.class);
		module.setAbstractTypes(resolver);
		mapper.registerModule(module);
		return mapper;
	}
}
