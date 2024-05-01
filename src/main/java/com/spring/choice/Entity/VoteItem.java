package com.spring.choice.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "VOTE_ITEM_TABLE")
@Getter
@Setter
public class VoteItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private int voteCount = 0; // 투표 수

    @ManyToOne(fetch = FetchType.LAZY)
    private Vote vote;

}
