package guru.sfg.brewery.security;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class UserUnlockService {

    private final UserRepository userRepository;

    @Scheduled(fixedRate = 5000)
    public void unlockAccounts (){
        log.debug("running unlock accounts ...");
        List<User> lockedUsers = userRepository.findAllByAccountNonLockedAndLastModifiedDateIsBefore(false,
                Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)));
        if(lockedUsers.size() > 0){
            log.info("locked accounts found, unlocking ...");
            lockedUsers.forEach(user -> user.setAccountNonLocked(true));
            userRepository.saveAll(lockedUsers);
        }
    }
}
