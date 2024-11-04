package com.amazonclone.userauthentification.Repository;

import com.amazonclone.userauthentification.Model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepo extends JpaRepository<Session, Long> {

}
