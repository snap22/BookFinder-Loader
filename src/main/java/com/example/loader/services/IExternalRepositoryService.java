package com.example.loader.services;

import com.example.loader.models.User;

import java.util.List;

public interface IExternalRepositoryService {
    void setupUsers(List<User> users);
}
