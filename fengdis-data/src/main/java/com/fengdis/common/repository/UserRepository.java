package com.fengdis.common.repository;

import com.fengdis.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @version 1.0
 * @Descrittion:
 * @author: fengdi
 * @since: 2019/08/26 10:52
 */
@Repository
public interface UserRepository extends JpaRepository<User,String> {

    @Query("from User u where u.account = ?1")
    List<User> findByAccount(String account);
}
