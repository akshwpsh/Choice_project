package com.spring.choice;

import com.spring.choice.Entity.Board;
import com.spring.choice.Entity.Comment;
import com.spring.choice.Entity.VoteItem;
import com.spring.choice.Repository.BoardRepository;
import com.spring.choice.Repository.CommentRepository;
import com.spring.choice.Repository.VoteItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    private final VoteItemRepository voteItemRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository, CommentRepository commentRepository, VoteItemRepository voteItemRepository) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.voteItemRepository = voteItemRepository;
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

    //addComment
    public Comment addComment(Long boardId, Comment comment) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        comment.setBoard(board);
        return commentRepository.save(comment);
    }

    //toggleLike
    public void toggleLike(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        board.setLikeCount(board.getLikeCount() + 1);
        boardRepository.save(board);
    }

    public List<String> vote(Long voteItemId) {
        VoteItem voteItem = voteItemRepository.findById(voteItemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid voteItemId: " + voteItemId));
        voteItem.setVoteCount(voteItem.getVoteCount() + 1);
        voteItemRepository.save(voteItem);

        List<String> voteCounts = new ArrayList<>();
        for (VoteItem item : voteItem.getVote().getItems()) {
            voteCounts.add(item.getContent() + " (" + item.getVoteCount() + ")");
        }
        return voteCounts;
    }
}
