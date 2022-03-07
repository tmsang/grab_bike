package com.intec.grab.bike.histories;

public interface MessageStatus
{
    String PENDING = "1";
    String ACCEPTED = "2";
    String STARTED = "3";
    String END = "4";
    String EVALUATION = "5";

    String CANCEL_BY_USER = "10";
    String CANCEL_BY_DRIVER = "11";
    String CANCEL_BY_ADMIN = "12";
    String CANCEL_BY_SYSTEM = "13";
}