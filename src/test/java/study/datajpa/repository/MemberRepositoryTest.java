package study.datajpa.repository;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCURD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);


        //단건 조회
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("aaa", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("aaa", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("aaa");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("aaa");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("aaa", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }


    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("aaa", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println(dto);
        }
    }


    @Test
    public void findByNames() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("aaa", "bbb"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //주의점. 값이없으면 빈 컬렉션을 return 해준다.
        List<Member> memberList = memberRepository.findListByUsername("aaa");
        System.out.println("memberList = " + memberList);


        //주의점. 단건이면 값이없으면 null을 return 해준다.
        Member member = memberRepository.findMemberByUsername("aaa");
        System.out.println("member = " + member);

        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("aaa");
        System.out.println("optionalMember = " + optionalMember.get());

    }


    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));


        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        //전체 갯수
        assertThat(page.getTotalElements()).isEqualTo(5);
        //페이지 번호
        assertThat(page.getNumber()).isEqualTo(0);
        //총 페이지 갯수
        assertThat(page.getTotalPages()).isEqualTo(2);
        //첫번재 페이지 인지
        assertThat(page.isFirst()).isTrue();
        //다음 페이지가 있는지
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        //bulk연산은 영속성 컨텍스트를 거치지 않고 바로 DB에 update문을 날려서 데이터를 수정하는것이다.
        //bulk update를 날린 후 영속성 컨텍스트에 있는 데이터 값을 조회해보면 bulk update가 실행 되기 이전의 값이 들어가져있다.
        //그래서 bulk update를 한 뒤에는 영속성 컨텍스트를 초기화 시켜줘야한다.
        em.flush();
        em.clear();

        //then
        assertThat(resultCount).isEqualTo(3);
    }


    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        //N+1 문제 발생
        List<Member> members = memberRepository.findAll();  //멤버 객체 조회

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member = " + member.getTeam().getName());
        }

    }

    @Test
    public void queryHint() {

        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        //보통 트랜잭션 커밋시점에 flush가 되는데, 강제로 영속성 컨텍스트에 있는 값들을 flush시킬 수 있다.
        em.flush();
        //영속성 컨텍스트에 있는 값들을 모두 날린다.
        em.clear();

        //then
        Member findMember = memberRepository.findReadOnlyByUsername("member1"); //스냅샷을 만들지 않는다. 읽기 전용으로만 사용한다.
        findMember.setUsername("member2");
        em.flush();
    }

    @Test
    public void lock() {

        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        //보통 트랜잭션 커밋시점에 flush가 되는데, 강제로 영속성 컨텍스트에 있는 값들을 flush시킬 수 있다.
        em.flush();
        //영속성 컨텍스트에 있는 값들을 모두 날린다.
        em.clear();

        //then
        List<Member> member11 = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();


        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        Assertions.assertThat(result.size()).isEqualTo(1);


    }


}
