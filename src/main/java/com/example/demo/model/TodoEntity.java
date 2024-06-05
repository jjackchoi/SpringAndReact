package com.example.demo.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Todo")
public class TodoEntity {
	@Id
	private String id; // 이 오브젝트의 아이디
	private String userId; // 이 오브젝트를 생성한 사용자의 아이디
	private String title; // Todo타이틀(예: 운동하기)
	private boolean done; // true - todo를 완료한 경우(checked)
	
	// 자동 UUID생성
	@PrePersist
	protected void onCreate() {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString();
		}
	}
}
