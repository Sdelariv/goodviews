package be.svend.goodviews.services;

import be.svend.goodviews.models.Tag;
import be.svend.goodviews.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    TagRepository tagRepo;

    public TagService(TagRepository tagrepo) {
        this.tagRepo = tagrepo;
    }

    public List<Tag> saveTags(List<Tag> tags) {
        List<Tag> savedTags = new ArrayList<>();

        for (Tag tag : tags) {
            savedTags.add(saveTag(tag));
        }

        return savedTags;
    }

    public Tag saveTag(Tag tag) {
        Optional<Tag> foundTag = tagRepo.findByName(tag.getName());
        if (foundTag.isEmpty()) {
            System.out.println("Saving " + tag.getName());
            return tagRepo.save(tag);
        } else {
            System.out.println("Not saving " + tag.getName() + " because it already exists");
            return foundTag.get();
        }
    }
}
