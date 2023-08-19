package com.post.parcels.repository;

import com.post.parcels.model.entity.PostalOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostalOfficeRepository extends JpaRepository<PostalOffice, String> {
}
