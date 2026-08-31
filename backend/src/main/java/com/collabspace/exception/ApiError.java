package com.collabspace.exception;

import java.time.LocalDateTime;

public record ApiError(LocalDateTime timestamp, int status, String error, String message, String path) {
    public static Builder builder(){ return new Builder(); }
    public static class Builder {
        private LocalDateTime timestamp; private int status; private String error; private String message; private String path;
        public Builder timestamp(LocalDateTime v){timestamp=v;return this;}
        public Builder status(int v){status=v;return this;}
        public Builder error(String v){error=v;return this;}
        public Builder message(String v){message=v;return this;}
        public Builder path(String v){path=v;return this;}
        public ApiError build(){return new ApiError(timestamp,status,error,message,path);}
    }
}