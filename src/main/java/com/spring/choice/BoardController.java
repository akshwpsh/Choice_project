package com.spring.choice;

import com.spring.choice.Entity.Board;
import com.spring.choice.Entity.Comment;
import com.spring.choice.Entity.Vote;
import com.spring.choice.Entity.VoteItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;
    @GetMapping("/list")
    public String list(Model model) {
        List<Board> boards = boardService.getAllBoards();
        for (Board board : boards) {
            if (board.getVote() == null) {
                board.setVote(new Vote());
            }
            if (board.getVote().getItems() == null) {
                board.getVote().setItems(new ArrayList<>());
            }
        }
        model.addAttribute("boards", boards);
        return "index.html";
    }

    @GetMapping("/create")
    public String create() {
        return "createBoard.html";
    }

    @PostMapping
    public String createBoard(@ModelAttribute Board board, @RequestParam("options") String[] options, HttpSession session) {

        // 현재 로그인된 사용자의 이름을 세션에서 가져옴
        String username = (String) session.getAttribute("username");
        board.setWriter(username); // 게시글 작성자를 현재 로그인된 사용자로 설정

        // 현재 시간을 가져옴
        LocalDateTime currentTime = LocalDateTime.now();
        board.setCreatedDate(currentTime); //

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
        return "redirect:/boards/list";
    }

    @PostMapping("/vote")
    @ResponseBody
    public ResponseEntity<List<String>> vote(@RequestParam Long voteItemId) {
        List<String> voteCounts = boardService.vote(voteItemId);
        return ResponseEntity.ok(voteCounts);
    }
}
