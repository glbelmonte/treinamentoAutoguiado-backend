package br.pucrs.ages.treinamentoautoguiado.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "user_progresses")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserProgress extends BaseEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="USER_ID")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name="MODULE_ITEM_ID")
	private ModuleItem module_item;

	public UserProgress(ModuleItem moduleItem, User user) {
		this.module_item = moduleItem;
		this.user = user;
	}
}
