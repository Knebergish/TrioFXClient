package trio.core;


import trio.model.field.Coordinates;
import trio.model.field.StepResult;
import trio.model.game.Game;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface TrioFacade extends Remote {
	/**
	 * Создаёт новую игру.
	 *
	 * @param width  ширина создаваемого поля.
	 * @param height высота создаваемого поля.
	 * @return ошибка или идентификатор игры.
	 */
	Response<String> createGame(int width, int height) throws RemoteException;
	
	/**
	 * Подключает к игре нового игрока.
	 *
	 * @param gameId    идентификатор игры.
	 * @param gamerName отображаемое имя игрока.
	 * @return ошибка или идентификатор игрока.
	 */
	Response<String> connectToGame(String gameId, String gamerName) throws RemoteException;
	
	/**
	 * Проверяет, может ли игрок сделать ход в игре.
	 *
	 * @param gameId  идентификатор игры.
	 * @param gamerId идентификатор игрока.
	 * @return ошибка или флаг.
	 */
	Response<Boolean> canMakeStep(String gameId, String gamerId) throws RemoteException;
	
	/**
	 * Делает ход, меняя местами две ячейки на карте.
	 *
	 * @param gameId  идентификатор игры.
	 * @param gamerId идентификатор игрока.
	 * @param source  исходная ячейка.
	 * @param dest    целевая ячейка.
	 * @return ошибка или новое состояние игры.
	 */
	Response<StepResult> makeStep(String gameId, String gamerId, Coordinates source, Coordinates dest)
	throws RemoteException;
	
	/**
	 * @param gameId  идентификатор игры.
	 * @param gamerId идентификатор игрока.
	 * @return ошиба или текущее состояние игры.
	 */
	Response<Game> getGameState(String gameId, String gamerId) throws RemoteException;
}
