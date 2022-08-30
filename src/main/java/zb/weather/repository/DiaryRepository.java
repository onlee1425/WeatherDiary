package zb.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zb.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    //특정 날짜 다이어리 조회용
    List<Diary> findAllByDate(LocalDate date);

    //특정 기간 다이어리 조회용
    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    //다이어리 수정 (같은 날짜에 두개이상 다이어리가 있을경우, 첫번째것을 가져옴)
    Diary getFirstByDate(LocalDate date);

    //다이어리 삭제
    @Transactional
    void deleteAllByDate(LocalDate date);
}
