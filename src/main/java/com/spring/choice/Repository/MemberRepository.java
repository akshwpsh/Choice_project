package com.spring.choice.Repository;

import com.spring.choice.Entity.MemberDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberDTO, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    MemberDTO findByUsername(String username);
    MemberDTO findByEmail(String email);
}
