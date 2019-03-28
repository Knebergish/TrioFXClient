package trio.core;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import trio.ObjectMapperFactory;
import trio.model.field.Coordinates;
import trio.model.field.StepResult;
import trio.model.game.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class HttpTrioFacade implements TrioFacade {
	private static final Logger log = Logger.getLogger("TrioLogging");
	
	private final String host;
	
	HttpTrioFacade(String host) {
		this.host = "http://" + host;
	}
	
	@Override
	public Response<String> createGame() throws RemoteException {
		return get("createGame", new ArrayList<>(), String.class);
	}
	
	@Override
	public Response<String> connectToGame(String gameId, String gamerName) throws RemoteException {
		List<Parameter> params = List.of(
				new Parameter("gameId", gameId),
				new Parameter("gamerName", gamerName));
		return get("connectToGame", params, String.class);
	}
	
	@Override
	public Response<Boolean> canMakeStep(String gameId, String gamerId) throws RemoteException {
		List<Parameter> params = List.of(
				new Parameter("gameId", gameId),
				new Parameter("gamerId", gamerId));
		return get("canMakeStep", params, Boolean.class);
	}
	
	@Override
	public Response<StepResult> makeStep(String gameId, String gamerId, Coordinates source, Coordinates dest)
	throws RemoteException {
		List<Parameter> params = List.of(
				new Parameter("gameId", gameId),
				new Parameter("gamerId", gamerId),
				new Parameter("source", serializeCoordinates(source)),
				new Parameter("dest", serializeCoordinates(dest)));
		return get("makeStep", params, StepResult.class);
	}
	
	@Override
	public Response<Game> getGameState(String gameId, String gamerId) throws RemoteException {
		List<Parameter> params = List.of(
				new Parameter("gameId", gameId),
				new Parameter("gamerId", gamerId));
		return get("getGameState", params, Game.class);
	}
	
	private String serializeCoordinates(Coordinates value) throws RemoteException {
		try {
			return new ObjectMapper().writerFor(Coordinates.class).writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RemoteException("Ошибька", e);
		}
	}
	
	private <T> Response<T> get(String action, List<Parameter> params, Class<T> clazz)
	throws RemoteException {
		try {
			String url = host + "/" + action;
			if (!params.isEmpty()) {
				url += "?";
				url += params.parallelStream().map(Parameter::convertToString).collect(Collectors.joining("&"));
			}
			log.info("Request: " + url);
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			
			BufferedReader in       = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String         inputLine;
			StringBuilder  response = new StringBuilder();
			
			//noinspection NestedAssignment
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			con.disconnect();
			log.info("Response: " + response);
			
			ObjectMapper mapper      = ObjectMapperFactory.createMapper();
			TypeFactory  typeFactory = mapper.getTypeFactory();
			JavaType     type        = typeFactory.constructParametricType(Response.class, clazz);
			return mapper.readValue(response.toString(), type);
		} catch (IOException e) {
			throw new RemoteException("Ошибька", e);
		}
	}
	
	
	@SuppressWarnings("unused")
	private static final class Parameter {
		private final String name;
		private final String value;
		
		private Parameter(String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		String convertToString() {
			return URLEncoder.encode(name, StandardCharsets.UTF_8)
			       + '='
			       + URLEncoder.encode(value, StandardCharsets.UTF_8);
		}
		
		public String getName() {
			return name;
		}
		
		public String getValue() {
			return value;
		}
	}
}
