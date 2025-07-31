//package dev.erpix.easykan.controller;
//
//import dev.erpix.easykan.exception.ResourceNotFoundException;
//import dev.erpix.easykan.entities.EKBoard;
//import dev.erpix.easykan.entities.EKColumn;
//import dev.erpix.easykan.service.BoardColumnService;
//import dev.erpix.easykan.service.BoardService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/boards/{boardId}/columns")
//@RequiredArgsConstructor
//public class BoardColumnController {
//
//    private final BoardService boardService;
//    private final BoardColumnService columnService;
//
//    @GetMapping("/count")
//    public long countByBoardId(@PathVariable int boardId) {
//        return columnService.count(boardId);
//    }
//
//    @PostMapping
//    public ResponseEntity<EKColumn> createColumn(@PathVariable int boardId, @RequestBody EKColumn column) {
//        EKBoard board = boardService.getBoardById(boardId)
//                .orElseThrow(() -> new ResourceNotFoundException("Board with id '" + boardId + "' not found"));
//
//        column.setBoard(board);
//        EKColumn created = columnService.create(column);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }
//
//    @DeleteMapping("/{columnId}")
//    public void deleteColumn(@PathVariable int boardId, @PathVariable int columnId) {
//        EKBoard board = boardService.getBoardById(boardId)
//                .orElseThrow(() -> new ResourceNotFoundException("Board with id '" + boardId + "' not found"));
//        columnService.getById(columnId)
//                .orElseThrow(() -> new ResourceNotFoundException("Column with id '" + columnId + "' not found"));
//        columnService.delete(columnId);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<EKColumn>> getColumnsForBoard(@PathVariable int boardId) {
//        List<EKColumn> columns = columnService.getAllByBoardId(boardId);
//        if (columns.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(columns);
//    }
//
//    @GetMapping("/column/{columnId}")
//    public EKColumn getColumnById(@PathVariable int columnId) {
//        return columnService.getById(columnId)
//                .orElseThrow(() -> new IllegalArgumentException("Column not found with ID: " + columnId));
//    }
//
//}
