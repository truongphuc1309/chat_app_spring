package com.truongphuc.service;

public interface WebSocketService {
    void sendUpdateUserStatus(String email, boolean online);
}
