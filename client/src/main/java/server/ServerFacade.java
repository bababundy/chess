package server;

import com.google.gson.Gson;
import model.GameData;
import requests.*;
import results.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public RegisterResult register(RegisterRequest req) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, req, RegisterResult.class);
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, req, LoginResult.class);
    }

    public void logout(LogoutRequest req) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
    }

    public CreateResult createGame(CreateRequest req) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, req, CreateResult.class);
    }

    public JoinResult joinGame(JoinRequest req) throws ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, req, JoinResult.class);
    }

    public ArrayList<GameData> listPets(ListRequest req) throws ResponseException {
        var path = "/game";
        var response = this.makeRequest("GET", path, req, ListResult.class);
        return response.games();
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws ResponseException, IOException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }
            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
