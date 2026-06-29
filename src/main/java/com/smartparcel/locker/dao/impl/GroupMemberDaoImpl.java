package com.smartparcel.locker.dao.impl;

import com.smartparcel.locker.dao.GroupMemberDao;
import com.smartparcel.locker.entity.GroupMember;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GroupMemberDaoImpl implements GroupMemberDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public GroupMember save(GroupMember member) {
        if (member.getId() == null) {
            em.persist(member);
            return member;
        }
        return em.merge(member);
    }

    @Override
    public List<GroupMember> findByGroupId(Long groupId) {
        return em.createQuery(
                        "select m from GroupMember m where m.groupId = :groupId order by m.id",
                        GroupMember.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    @Override
    public Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId) {
        return em.createQuery(
                        "select m from GroupMember m where m.groupId = :groupId and m.userId = :userId",
                        GroupMember.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public boolean existsByGroupIdAndUserId(Long groupId, Long userId) {
        Long count = em.createQuery(
                        "select count(m) from GroupMember m where m.groupId = :groupId and m.userId = :userId",
                        Long.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public void delete(GroupMember member) {
        em.remove(em.contains(member) ? member : em.merge(member));
    }
}
