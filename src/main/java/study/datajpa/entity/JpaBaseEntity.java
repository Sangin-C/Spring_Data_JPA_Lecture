package study.datajpa.entity;


import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity {    //순수 JPA로 Auditing 이용하기

    @Column(updatable = false)  //DB값 변경X
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist //persist시 실행
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate  //update시 실행
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

}
