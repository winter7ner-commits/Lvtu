package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.User;

public interface AdminPermissionService {

    User requireAdmin(String authorization, String... allowedRoles);
}

