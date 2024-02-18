package com.securepass.apisecurepass.controllers;

import com.securepass.apisecurepass.models.LoginModel;
import com.securepass.apisecurepass.models.UserModel;
import com.securepass.apisecurepass.repositories.LoginRepository;
import com.securepass.apisecurepass.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@RestController
//@RequestMapping(value = "/login", produces = {"application/json"})
@Service
public class LoginController {

    @Autowired
    LoginRepository loginRepository;

    @Autowired
    UserRepository userRepository;

//    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Boolean loginComFoto(Optional<UserModel> user){

        Optional<UserModel> searchUser = userRepository.findById( user.get().getId() );

        if (searchUser.isEmpty()) {
            return false;
        }

        LoginModel loginModel = new LoginModel(searchUser.get());

        loginRepository.save( loginModel );

        return true;
    }
}
