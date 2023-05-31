package com.mokke.componentbuilder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mokke.componentbuilder.model.ContextHistory;
import com.mokke.componentbuilder.model.Session;

@Repository
public interface ContextHistoryRepository extends JpaRepository<ContextHistory, Long>{

    public List<ContextHistory> findBySessionid(Session session);
}
//201886de-932d-4c4d-990f-17046f1d5f50