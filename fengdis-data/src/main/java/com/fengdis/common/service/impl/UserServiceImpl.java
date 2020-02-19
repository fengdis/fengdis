package com.fengdis.common.service.impl;

import com.fengdis.common.BaseExServiceException;
import com.fengdis.common.dto.UserDTO;
import com.fengdis.common.entity.User;
import com.fengdis.common.repository.UserRepository;
import com.fengdis.common.service.UserService;
import com.fengdis.util.DesensitizeUtils;
import com.fengdis.util.MD5Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Descrittion:
 * @author: fengdi
 * @since: 2019/08/26 10:28
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public UserDTO insert(User user) {
        user.setPassword(MD5Utils.encrypt(user.getPassword()));
        User result = userRepository.save(user);
        UserDTO target = new UserDTO();
        BeanUtils.copyProperties(result,target);
        return target;
    }

    @Transactional
    @Override
    public UserDTO update(User user) {
        User result = userRepository.save(user);
        UserDTO target = new UserDTO();
        BeanUtils.copyProperties(result,target);
        return target;
    }

    @Transactional
    @Override
    public void delete(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(!optionalUser.isPresent()){
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,String.format("%s不存在",id));
        }
        User user = optionalUser.get();
        user.setIsValid(User.USER_ISVALID_INVALID);
        User result = userRepository.save(user);
        //userRepository.deleteById(id);
    }

    @Override
    public UserDTO findOne(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(!optionalUser.isPresent()){
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,String.format("%s不存在",id));
        }
        User result = optionalUser.get();
        result.setPhone(DesensitizeUtils.around(result.getPhone(),3,4));
        UserDTO target = new UserDTO();
        BeanUtils.copyProperties(result,target);
        return target;
    }

    @Override
    public List<UserDTO> findAll() {
        List<User> result = userRepository.findAll();
        List<UserDTO> targetList = new ArrayList<>();
        for(User user : result){
            UserDTO target = new UserDTO();
            BeanUtils.copyProperties(user,target);
            targetList.add(target);
        }
        return targetList;
    }

    @Override
    public Page<User> findPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

}
