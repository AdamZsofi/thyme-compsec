package hu.bme.crysys.server.server.domain;

import hu.bme.crysys.server.server.repository.CaffCommentRepository;
import hu.bme.crysys.server.server.repository.CaffFileRepository;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public final class CaffModel {

    private static final Logger logger = LoggerFactory.getLogger(CaffModel.class);

    @Autowired
    private CaffFileRepository caffFileRepository;
    @Autowired
    private CaffCommentRepository caffCommentRepository;
    @Autowired
    private UserDataRepository userDataRepository;

}
