package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
