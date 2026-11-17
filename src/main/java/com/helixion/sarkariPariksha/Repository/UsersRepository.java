package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);
}
