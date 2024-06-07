package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.TodoEntity;
import com.example.demo.persistence.TodoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TodoService {

	@Autowired
	private TodoRepository repository;
	
	public String testService() {
		// TodoEntity 생성
		TodoEntity entity = TodoEntity.builder().title("My first todo item").build();
		// TodoEntity 저장
		repository.save(entity);
		// TodoEntity 검색
		TodoEntity savedEntity = repository.findById(entity.getId()).get();
		return savedEntity.getTitle();
	}
	
	// todo 생성
	public List<TodoEntity> create(final TodoEntity entity) {
		// validations
		validate(entity);
		
		repository.save(entity);
		
		log.info("Entity Id : {} is saved.", entity.getId());
		
		return repository.findByUserId(entity.getUserId());
	}
	
	// todo 조회
	public List<TodoEntity> retrieve(final String userId) {
		return repository.findByUserId(userId);
	}
	
	// todo 수정
	public List<TodoEntity> update(final TodoEntity entity) {
		// (1) 저장할 엔티티가 유효한지 확인
		validate(entity);
		
		// (2) 넘겨받은 엔티티 id를 이용해 TodoEntity를 가져온다. 존재하지 않는 엔티티는 업데이트 x
		final Optional<TodoEntity> original = repository.findById(entity.getId());
		
		original.ifPresent(todo -> {
			// (3) 반환된 TodoEntity가 존재하면 값을 새 entity 값으로 덮어 씌운다.
			todo.setTitle(entity.getTitle());
			todo.setDone(entity.isDone());
			
			// (4) 데이터베이스에 새 값을 저장한다.
			repository.save(todo);
		});
		
		// 사용자의 모든 Todo 리스트를 리턴
		return retrieve(entity.getUserId());
	}
	
	// todo 삭제
	public List<TodoEntity> delete(final TodoEntity entity) {
		// (1) 저장할 엔티티가 유효한지 확인
		validate(entity);
		
		try {
			// (2) 엔티티를 삭제
			repository.delete(entity);
		} catch (Exception e) {
			// (3) exception 발생 시 id와 exception을 로깅한다.
			log.error("error deleting entity", entity.getId(), e);
			
			// (4) 컨트롤러로 exception을 보낸다. 
			// 데이터베이스 내부 로직을 캡슐화하려면 e를 리턴하지 않고
			// 새 exception 오브젝트를 리턴한다.
			throw new RuntimeException("error deleting entity " + entity.getId());
		}
		
		// (5) 새 Todo 리스트를 가져와 리턴한다.
		return retrieve(entity.getUserId());
	}
	
	// 검증
	private void validate(final TodoEntity entity) {
		if (entity == null) {
			log.warn("Entity cannot be null.");
			throw new RuntimeException("Entity cannat be null.");
		}
		
		if (entity.getUserId() == null) {
			log.warn("Unknown user.");
			throw new RuntimeException("unknonw user.");
		}
	}
	
}
