package com.netcracker.model;

import org.springframework.data.jpa.repository.JpaRepository;

// daba репка - не часть модели, ей нужен отдельный пакет
public interface UserRepository extends JpaRepository<User, String> {
}
