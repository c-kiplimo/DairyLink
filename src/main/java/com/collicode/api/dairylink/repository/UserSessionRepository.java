package com.collicode.api.dairylink.repository;


import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.domain.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession,Long> {

    UserSession findByUser(User user);

    UserSession findByUserId(Long id);
}
