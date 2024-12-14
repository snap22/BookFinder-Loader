package com.example.loader.services;

import com.example.loader.dto.User;

import java.util.List;

public interface IDataDownloadService {
    List<User> downloadUsers();
}
