package com.spring.choice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        return ResponseEntity.ok(boardService.createBoard(board));
    }

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    // 특정 게시글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoardById(boardId));
    }

    // 게시글 업데이트
    @PutMapping("/{boardId}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long boardId, @RequestBody Board board) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, board));
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok().build();
    }

    // 댓글 생성
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long boardId, @RequestBody Comment comment) {
        return ResponseEntity.ok(boardService.addComment(boardId, comment));
    }

    // 좋아요 추가/제거
    @PostMapping("/{boardId}/likes")
    public ResponseEntity<Void> toggleLike(@PathVariable Long boardId) {
        boardService.toggleLike(boardId);
        return ResponseEntity.ok().build();
    }

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

}
