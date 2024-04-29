package com.spring.choice;

import com.spring.choice.Repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    // 필요한 경우 다른 리포지토리를 주입받습니다.

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // 게시글 생성
    public Board createBoard(Board board) {
        return boardRepository.save(board);
    }

    // 게시글 목록 조회
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
    }

    //deleteBoard
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    //updateBoard
    public Board updateBoard(Long boardId, Board board) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        findBoard.setTitle(board.getTitle());
        findBoard.setContent(board.getContent());
        return boardRepository.save(findBoard);
    }

    //addcomment
    public Comment addComment(Long boardId, Comment comment) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        comment.setBoard(board);
        return boardRepository.save(board).getComments().get(board.getComments().size() - 1);
    }

    //toggleLike
    public void toggleLike(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        board.setLikeCount(board.getLikeCount() + 1);
        boardRepository.save(board);
    }
}
