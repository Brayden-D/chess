package dataaccess.interfaces;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuthData(String username);

    void clear();

    AuthData createAuthData(String username);

    public void deleteAuth(String authToken) throws Exception;
}
