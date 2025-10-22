package dataaccess.interfaces;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuthData(String username);

    void clear();

    AuthData createAuthData(String username);

    void deleteAuth(String username);
}
