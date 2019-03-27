package trio.view;


public class GameCredentials {
	private final String gameId;
	private final String gamerId;
	private final String gamerName;
	
	public GameCredentials(String gameId, String gamerId, String gamerName) {
		this.gameId = gameId;
		this.gamerId = gamerId;
		this.gamerName = gamerName;
	}
	
	@Override
	public String toString() {
		return "GameCredentials{" +
		       "gameId='" + gameId + '\'' +
		       ", gamerId='" + gamerId + '\'' +
		       '}';
	}
	
	public String getGameId() {
		return gameId;
	}
	
	public String getGamerId() {
		return gamerId;
	}
	
	public String getGamerName() {
		return gamerName;
	}
}
