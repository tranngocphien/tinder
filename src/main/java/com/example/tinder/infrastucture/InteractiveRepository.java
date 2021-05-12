package com.example.tinder.infrastucture;

import com.example.tinder.domain.Interactive;
import com.example.tinder.domain.InteractiveID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractiveRepository extends JpaRepository<Interactive, InteractiveID> {
    Interactive findAllByInteractiveID(InteractiveID interactiveID);

    List<Interactive> findAllByInteractiveIDAfter(InteractiveID id);

    List<Interactive> findAllByInteractiveID_ToUserID(Integer id);

  //  List<Interactive> findAllByInteractiveIDAfter(Iterable<InteractiveID> iterable);
}
