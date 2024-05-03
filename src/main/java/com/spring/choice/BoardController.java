package com.spring.choice;

import com.spring.choice.Entity.Board;
import com.spring.choice.Entity.Comment;
import com.spring.choice.Entity.Vote;
import com.spring.choice.Entity.VoteItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;
    @GetMapping("/list")
    public String list(Model model) {
        List<Board> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);
        return "boardList.html";
    }

    @GetMapping("/create")
    public String create() {
        return "createBoard.html";
    }

    @PostMapping
    public String createBoard(@ModelAttribute Board board, @RequestParam("options") String[] options) {

        Vote vote = new Vote();

        // 각 옵션에 대해 VoteItem을 생성하고 Vote에 추가
        for (String option : options) {
            VoteItem voteItem = new VoteItem();
            voteItem.setContent(option.trim());
            vote.addVoteItem(voteItem);
        }

        // Vote를 Board에 설정
        board.setVote(vote);

        boardService.createBoard(board);
        return "redirect:/boards/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Board board = boardService.getBoardById(id);
        model.addAttribute("board", board);
        return "boardDetail";
    }

    @PostMapping("/comments")
    public String createComment( @RequestParam Long boardId, @ModelAttribute Comment comment) {
        boardService.addComment(boardId, comment);
        return "redirect:/boards/" + boardId;
    }
}
