package dataaccess.interfaces;

import model.UserData;

public interface UserDAO {
    void create(UserData userData);

    void clear();

    UserData findUser(String username);

}
