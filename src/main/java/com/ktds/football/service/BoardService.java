package com.ktds.football.service;

import com.ktds.football.domain.PostStatus;
import com.ktds.football.dto.MyPostPageDTO;
import com.ktds.football.dto.PageDTO;
import com.ktds.football.domain.Post;
import com.ktds.football.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final PageService pageService;
    private final RequestService requestService;

    public List<Post> findAll() {
        return boardRepository.findAll();
    }

    public Post findById(Long postId) {
        return boardRepository.findById(postId);
    }

    public List<Post> findPage(int currentPage) {

        return boardRepository.findPage(pageService.findStartPage(currentPage));

    }

    public PageDTO pagingParam(int currentPage) {

        int totalPostCount = findAllCount();

        return pageService.pagingParam(currentPage, totalPostCount);
    }

    public int findAllCount() {
        return boardRepository.findAllCount();
    }

    public void add(Post post) {
        boardRepository.add(post);
    }

    public void update(Post post) {
        boardRepository.update(post);
    }

    public void delete(Long postId) {
        boardRepository.delete(postId);
    }

	public void checkPeopleCount(Long postId) {
        Post post = findById(postId);
        int requestCount = requestService.findApproveByPostIdCount(postId);

        if(post.getStatus() == PostStatus.PROCEEDING && post.getPeople() <= requestCount) {
            post.setStatus(PostStatus.DEADLINE);
            update(post);
        }

        if(post.getStatus() == PostStatus.DEADLINE && post.getPeople() > requestCount) {
            post.setStatus(PostStatus.PROCEEDING);
            update(post);
        }
	}

    public List<Post> findByMemberIdPage(int currentPage, Long memberId) {

        Map<String, Integer> map = pageService.findStartPage(currentPage);

        MyPostPageDTO myPostPageDTO = new MyPostPageDTO();
        myPostPageDTO.setMemberId(memberId);
        myPostPageDTO.setPerPage(map.get("perPage"));
        myPostPageDTO.setStartPostNum(map.get("startPostNum"));



        return boardRepository.findByMemberIdPage(myPostPageDTO);
    }

    public PageDTO myPostPagingParam(int currentPage, Long memberId) {
        int totalPostCount = boardRepository.findByMemberIdCount(memberId);

        return pageService.pagingParam(currentPage, totalPostCount);
    }
}
