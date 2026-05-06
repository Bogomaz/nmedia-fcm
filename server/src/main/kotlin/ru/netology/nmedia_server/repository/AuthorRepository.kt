package ru.netology.nmedia_server.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.netology.nmedia_server.entity.AuthorEntity

interface AuthorRepository : JpaRepository<AuthorEntity, Long>