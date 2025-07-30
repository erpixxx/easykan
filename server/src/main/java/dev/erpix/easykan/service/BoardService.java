//package dev.erpix.easykan.service;
//
//import dev.erpix.easykan.model.EKBoard;
//import dev.erpix.easykan.model.EKColumn;
//import dev.erpix.easykan.model.user.EKUser;
//import dev.erpix.easykan.repository.BoardColumnRepository;
//import dev.erpix.easykan.repository.BoardRepository;
//import lombok.RequiredArgsConstructor;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class BoardService {
//
//    private final BoardRepository boardRepository;
//    private final BoardColumnRepository columnRepository;
//
//    public long count() {
//        return boardRepository.count();
//    }
//
//    public @NotNull EKBoard create(EKBoard board, EKUser user) {
//        board.setOwner(user);
//        return boardRepository.save(board);
//    }
//
//    public void delete(int id) {
//        boardRepository.deleteById(id);
//    }
//
//    public @NotNull List<EKBoard> getBoardsForUser(@NotNull EKUser user) {
//        return boardRepository.findAllByOwner(user);
//    }
//
//    public @NotNull Optional<EKBoard> getBoardById(int id) {
//        return boardRepository.findById(id);
//    }
//
//    public @NotNull List<EKColumn> getColumnsForBoard(@NotNull EKBoard board) {
//        return columnRepository.findAllByBoardId(board.getId());
//    }
//
//}
