package jroar.web.repositories;

import jroar.web.model.StatInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<StatInfo, Long> {

    public StatInfo findByDate(String date);
}
