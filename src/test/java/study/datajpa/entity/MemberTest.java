package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired MemberRepository memberRepository;

    @Test
    public void testEntity() {

        //팀 데이터 넣기
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        //멤버 데이터 넣기
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();


        //멤버 확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        members.stream().forEach(m -> {
                    System.out.println("member = " + m);
                    System.out.println("-> member.team = " + m.getTeam());
                });

    }

    @Test
    public void JpaEventBaseEntity() throws InterruptedException {
        //given
        Member member = new Member("member1");
        memberRepository.save(member);  //@PrePersist 발생

        Thread.sleep(1000L);
        member.setUsername("member2");

        em.flush(); //@PreUPdate 발생
        em.clear();

        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        System.out.println("findMember.getCreatedDate() = " + findMember.getCreatedDate());
        System.out.println("findMember.getUpdatedDate() = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());


        //then

    }

}

