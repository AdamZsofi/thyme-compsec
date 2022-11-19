package hu.bme.crysys.server.server.domain.service;

import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDataRepository userDataRepository;

}
