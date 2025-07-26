package dev.erpix.easykan.repository;

import dev.erpix.easykan.model.EKBoard;
import dev.erpix.easykan.model.user.EKUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<EKBoard, Integer> {

    @NotNull List<EKBoard> findAllByOwner(@NotNull EKUser user);

}

