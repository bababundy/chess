package server;

import com.google.gson.Gson;
import requests.*;
import results.*;

import java.io.*;
import java.net.*;
import java.util.Objects;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;;
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException {
        var path = "/user";
        try {
            return this.makeRequest("POST", path, req, RegisterResult.class, null);
        } catch (ResponseException e) {
            throw new ResponseException(400, "Username already taken, try something else");
        }
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        var path = "/session";
        try{
            return this.makeRequest("POST", path, req, LoginResult.class, null);
        } catch (ResponseException e) {
            throw new ResponseException(400, "Incorrect Password");
        }
    }

    public LogoutResult logout(LogoutRequest req) throws ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, null, LogoutResult.class, req.authToken());
    }

    public CreateResult createGame(CreateRequest req) throws ResponseException {
        var path = "/game";
        if(req.gameName() == null) {
            throw new ResponseException(400, "bad request");
        }
        try {
            return this.makeRequest("POST", path, new CreateReqHelper(req.gameName()), CreateResult.class, req.authToken());
        } catch (ResponseException e){
            throw new ResponseException(400, "Bad request Probably");
        }
    }

    public JoinResult joinGame(JoinRequest req) throws ResponseException {
        var path = "/game";
        try {
            return this.makeRequest("PUT", path, new JoinReqHelper(req.playerColor(), req.gameID()), JoinResult.class, req.authToken());
        } catch (ResponseException e) {
            throw new ResponseException(400, "Bad request Probably");
        }
    }

    public ListResult listGames(ListRequest req) throws ResponseException {
        var path = "/game";
        try {
            return this.makeRequest("GET", path, null, ListResult.class, req.authToken());
        } catch (ResponseException e) {
            throw new ResponseException(400, "Bad request Probably");
        }
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
            if (authToken != null && !Objects.equals(http.getRequestProperty("Authorization"), "Already connected")) {
                http.setRequestProperty("Authorization", authToken);
            }
            //http.setRequestProperty("Content-Type", "application/json");
            if (!method.equals("GET") && request != null) {
                http.setDoOutput(true);
                writeBody(request, http);
            }

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
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(respBody);
            if (responseClass != null) {
                response = new Gson().fromJson(reader, responseClass);
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
