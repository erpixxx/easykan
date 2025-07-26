package dev.erpix.easykan.service;

import dev.erpix.easykan.model.EKColumn;
import dev.erpix.easykan.repository.BoardColumnRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardColumnService {

    private final BoardColumnRepository columnRepository;

    public long count() {
        return columnRepository.count();
    }

    public long count(int boardId) {
        return columnRepository.countByBoardId(boardId);
    }

    public @NotNull EKColumn create(@NotNull EKColumn column) {
        return columnRepository.save(column);
    }

//    public @NotNull BoardColumn create(int boardId, @NotNull BoardColumn column) {
//        column.setBoard(new Board(boardId));
//        return columnRepository.save(column);
//    }

    public void delete(int columnId) {
        columnRepository.deleteById(columnId);
    }

    public @NotNull Optional<EKColumn> getById(int columnId) {
        return columnRepository.findById(columnId);
    }

    public @NotNull List<EKColumn> getAllByBoardId(int boardId) {
        return columnRepository.findAllByBoardId(boardId);
    }

}
