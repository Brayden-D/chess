package dataaccess.interfaces;

import dataaccess.DataAccessException;
import model.AuthData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public interface AuthDAO {

    public void clear();

    public AuthData createAuthData(String username);

    public void deleteAuth(String authToken) throws Exception;

    public String getUsername(String authToken);

    public boolean authTokenExists(String authToken) throws DataAccessException;
}
