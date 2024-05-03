package com.spring.choice.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VOTE_TABLE")
@Getter
@Setter
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteItem> items = new ArrayList<>();

    public void addVoteItem(VoteItem voteItem) {
        items.add(voteItem);
        voteItem.setVote(this);
    }

}
