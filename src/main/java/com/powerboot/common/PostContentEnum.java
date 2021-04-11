package com.powerboot.common;

import okhttp3.MediaType;


public enum PostContentEnum {
    JSON(MediaType.parse("application/json")),
    FORM(MediaType.parse("multipart/form-data")),
    FORM_WWW(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"));

    private MediaType mediaType;

    PostContentEnum(MediaType mediaType){
        this.mediaType = mediaType;
    }

    public MediaType getMediaType() {
        return mediaType;
    }
}
