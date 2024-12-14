package com.example.loader.services;

import com.example.loader.dto.User;

import java.util.List;

public interface IDataUploadService {
    void uploadUsers(List<User> users);
}
