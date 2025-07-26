//package dev.erpix.easykan.controller;
//
//import dev.erpix.easykan.entities.EKCard;
//import dev.erpix.easykan.service.ColumnCardService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/cards")
//@RequiredArgsConstructor
//public class ColumnCardController {
//
//    private final ColumnCardService cardService;
//
//    @GetMapping("/count")
//    public long count() {
//        return cardService.count();
//    }
//
//    @PostMapping
//    public ResponseEntity<EKCard> createCard(@RequestBody EKCard card) {
//        EKCard created = cardService.create(card);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }
//
//    @DeleteMapping("/{cardId}")
//    public ResponseEntity<Void> deleteCard(@PathVariable int cardId) {
//        if (!cardService.exists(cardId)) {
//            return ResponseEntity.notFound().build();
//        }
//        cardService.delete(cardId);
//        return ResponseEntity.noContent().build();
//    }
//
//}
