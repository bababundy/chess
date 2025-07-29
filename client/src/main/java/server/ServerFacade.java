package server;

import com.google.gson.Gson;
import model.GameData;
import requests.*;
import results.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, req, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, req, LoginResult.class, null);
    }

    public LogoutResult logout(LogoutRequest req) throws ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, req, LogoutResult.class, req.authToken());
    }

    public CreateResult createGame(CreateRequest req) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, req, CreateResult.class, req.authToken());
    }

    public JoinResult joinGame(JoinRequest req) throws ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, req, JoinResult.class, req.authToken());
    }

    public ArrayList<GameData> listGames(ListRequest req) throws ResponseException {
        var path = "/game";
        var response = this.makeRequest("GET", path, req, ListResult.class, req.authToken());
        return response.games();
    }

    public ClearResult clear() throws ResponseException {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, ClearResult.class, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (!method.equals("GET") && request != null) {
                http.setDoOutput(true);
            }
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

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
