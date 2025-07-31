//package dev.erpix.easykan.controller;
//
//import dev.erpix.easykan.entities.EKBoard;
//import dev.erpix.easykan.service.BoardService;
//import dev.erpix.easykan.domain.user.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/boards")
//@RequiredArgsConstructor
//public class BoardController {
//
//    private final BoardService boardService;
//    private final UserService userService;
//
//    @GetMapping("/count")
//    public ResponseEntity<Long> count() {
//        return ResponseEntity.ok(boardService.count());
//    }
//
//    @PostMapping("/user/{uuid}")
//    public ResponseEntity<EKBoard> createBoard(@PathVariable UUID uuid, @RequestBody EKBoard board) {
//        return userService.getByUniqueId(uuid)
//                .map(user -> ResponseEntity.status(HttpStatus.CREATED)
//                        .body(boardService.create(board, user)))
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @DeleteMapping("/{boardId}")
//    public ResponseEntity<Void> deleteBoard(@PathVariable int boardId) {
//        if (boardService.getBoardById(boardId).isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        boardService.delete(boardId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/user/{uuid}")
//    public ResponseEntity<List<EKBoard>> getBoardsForUser(@PathVariable UUID uuid) {
//        return userService.getByUniqueId(uuid)
//                .map(user -> ResponseEntity.ok(boardService.getBoardsForUser(user)))
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/{boardId}")
//    public ResponseEntity<EKBoard> getBoardById(@PathVariable int boardId) {
//        return boardService.getBoardById(boardId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//}
