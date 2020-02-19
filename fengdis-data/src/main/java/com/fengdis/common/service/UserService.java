package com.fengdis.common.service;

import com.fengdis.common.dto.UserDTO;
import com.fengdis.common.entity.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @version 1.0
 * @Descrittion:
 * @author: fengdi
 * @since: 2019/08/26 10:27
 */
@CacheConfig(cacheNames = "user")
public interface UserService {

    //@CachePut(key = "#p0.getAccount()")
    UserDTO insert(User user);

    @CacheEvict(key = "#user.id",allEntries = false)
    UserDTO update(User user);

    @CacheEvict(key = "#id",allEntries = false)
    void delete(String id);

    @Cacheable(key = "#id")
    UserDTO findOne(String id);

    List<UserDTO> findAll();

    Page<User> findPage(Pageable pageable);

}
