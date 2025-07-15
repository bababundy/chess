package service;

import model.GameData;

import java.util.ArrayList;

public class GameService {
    CreateResult create(CreateRequest r) {
        //1. verify input
        //1.5 validate authToken
        //2. check if gameName is already taken
        //3. create new game model object
        //4. insert new game into database UserDao.createGame(g)
        //5. (create new GameData model object, insert into database)
        //6. create result and return gameID
        return new CreateResult(null, null);
    }

    JoinResult join(JoinRequest r) {
        //1. verify input
        //2 validate authToken
        //3. check if username is already taken in game
        //4. database GameDao.getGame(u)
        //5. database GameDao.updateGame(u)
        //6. create result and return
        return new JoinResult(null);
    }

    ListResult list(ListRequest r) {
        //1. verify input
        //2 validate authToken
        //3. create new arraylist
        //4. GameDao.getList()
        //5. create result and return
        return new ListResult(null, null);
    }

}

