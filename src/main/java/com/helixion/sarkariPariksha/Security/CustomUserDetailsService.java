package com.helixion.sarkariPariksha.Security;

import com.helixion.sarkariPariksha.Entity.CandidateProfile;
import com.helixion.sarkariPariksha.Entity.Users;
import com.helixion.sarkariPariksha.Repository.CandidatesRepository;
import com.helixion.sarkariPariksha.Repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CandidatesRepository candidatesRepository;

    @Override
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        Users user = usersRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile number: " + mobileNumber));

        Long candidateId = null;
        String fullName = null;

        Optional<CandidateProfile> profileOpt = candidatesRepository.findByUserId(user.getId());
        if (profileOpt.isPresent()) {
            CandidateProfile profile = profileOpt.get();
            candidateId = profile.getId();
            fullName = profile.getFullName();
        }

        return new CustomUserDetails(user, candidateId, fullName);
    }
}

