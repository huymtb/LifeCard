package com.prostage.lifecard.Model;

/**
 * Created by congnguyen on 9/27/17.
 */

public class Token {
    private int status;
    private String result;

    public int getStatus() {
        return status;
    }

    public Token(int status, String result) {
        this.status = status;
        this.result = result;
    }

    public void setStatus(int status) {

        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
