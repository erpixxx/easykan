//package dev.erpix.easykan.service;
//
//import dev.erpix.easykan.model.EKCard;
//import dev.erpix.easykan.repository.ColumnCardRepository;
//import lombok.RequiredArgsConstructor;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ColumnCardService {
//
//    private final ColumnCardRepository columnCardRepository;
//
//    public long count() {
//        return columnCardRepository.count();
//    }
//
//    public long count(int columnId) {
//        return columnCardRepository.countByColumnId(columnId);
//    }
//
//    public @NotNull EKCard create(@NotNull EKCard card) {
//        return columnCardRepository.save(card);
//    }
//
//    public void delete(@NotNull EKCard card) {
//        columnCardRepository.delete(card);
//    }
//
//    public void delete(int id) {
//        columnCardRepository.deleteById(id);
//    }
//
//    public boolean exists(int id) {
//        return columnCardRepository.existsById(id);
//    }
//
//    public @NotNull List<EKCard> getCardsForColumn(int columnId) {
//        return columnCardRepository.findAllByColumnId(columnId);
//    }
//
//    public @NotNull List<EKCard> getCardsForColumnOrdered(int columnId) {
//        return columnCardRepository.findAllByColumnIdOrderByPositionAsc(columnId);
//    }
//
//}
